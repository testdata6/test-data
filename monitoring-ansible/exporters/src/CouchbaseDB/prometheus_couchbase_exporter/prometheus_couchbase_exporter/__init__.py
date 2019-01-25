from prometheus_client import start_http_server
from prometheus_client.core import CounterMetricFamily, GaugeMetricFamily, REGISTRY
import couchbase as couchbase_metrics

from operator import getitem
from requests.auth import HTTPBasicAuth
import json, requests, sys, time, os, ast, signal, re, argparse

class CouchbaseCollector(object):
    METRIC_PREFIX = 'couchbase_'
    metrics = couchbase_metrics.get_metrics()
    gauges = {}

    def __init__(self, target, query_url):
        #print target , query_url
        self.BASE_URL = target.rstrip("/")
        self.QUERY_URL = query_url.rstrip("/")

    """
    Split dots in metric name and search for it in obj dict
    """
    def _dot_get(self, metric, obj):
        try:
            return reduce(getitem, metric.split('.'), obj)
        except Exception as e:
            return False

    """
    Request data via CURL with or without authentication.
    Auth username and password can be defined as environment variables
    :rtype JSON
    """
    def _request_data(self, url):
        try:
            if set(["COUCHBASE_USERNAME","COUCHBASE_PASSWORD"]).issubset(os.environ):
                response = requests.get(url, auth=HTTPBasicAuth(os.environ["COUCHBASE_USERNAME"], os.environ["COUCHBASE_PASSWORD"]))
            else:
                response = requests.get(url)
        except Exception as e:
            print('Failed to establish a new connection. Is {0} correct?'.format(self.BASE_URL))
            sys.exit(1)

        if response.status_code != requests.codes.ok:
            print('Response Status ({0}): {1}'.format(response.status_code, response.text))

        result = response.json()
        return result

    """
    Add metrics in GaugeMetricFamily format
    """
    def _add_metrics(self, metrics, metric_name, metric_gauges, data):
        metric_id = re.sub('(\.)', '_', metrics['id']).lower()
        metric_value=1
        if metric_name == self.METRIC_PREFIX + 'queries':
            #print data[metrics['id']]
            elp_time = data[metrics['id']]
            if data[metrics['id']].endswith('ms'):
                metric_value=float(elp_time[:elp_time.index('ms')])
            elif elp_time.endswith('s'):
                if 'm' in elp_time :
                    serv_time=elp_time[:elp_time.index('m')]
                    if 'h' in serv_time and 'd' in serv_time :
                        metric_value= (float(elp_time[:elp_time.index('d')])*24*60*60+float(elp_time[elp_time.index('d')+1:elp_time.index('h')])*60*60+float(elp_time[elp_time.index('h')+1:elp_time.index('m')])*60+float(elp_time[elp_time.index('m')+1:elp_time.index('s')]))*1000
                    elif 'd' in serv_time :
                        metric_value= (float(elp_time[:elp_time.index('d')])*24*60*60+float(elp_time[elp_time.index('d')+1:elp_time.index('m')])*60+float(elp_time[elp_time.index('m')+1:elp_time.index('s')]))*1000
                    elif 'h' in serv_time :
                        metric_value= (float(elp_time[:elp_time.index('h')])*60*60+float(elp_time[elp_time.index('h')+1:elp_time.index('m')])*60+float(elp_time[elp_time.index('m')+1:elp_time.index('s')]))*1000
                    else :
                        metric_value= (float(elp_time[:elp_time.index('m')])*60+float(elp_time[elp_time.index('m')+1:elp_time.index('s')]))*1000
                else :
                    metric_value=float(elp_time[:elp_time.index('s')] )* 1000
            elif data[metrics['id']].endswith('m'):
                metric_value=float(data[metrics['id']][:elp_time.index('m')]) * 60000
            elif data[metrics['id']].endswith('h'):
                metric_value=float(data[metrics['id']][:elp_time.index('h')]) * 60*60000
            elif data[metrics['id']].endswith('d'):
                metric_value=float(data[metrics['id']][:elp_time.index('d')]) * 24*60*60000
            elif data[metrics['id']].endswith(u"\u00B5"+'s' ):
                metric_value=float(data[metrics['id']][:elp_time.index(u"\u00B5")]) / 1000
            else :
                metric_value=float(data[metrics['id']][:elp_time.index('s')]) * 1000
            #print metric_value	, 	metric_id	, metrics['labels'] , metric_gauges
        else :
            metric_value = self._dot_get(metrics['id'], data)
        gauges = [metric_id]
        for index in range(len(metric_gauges)):
            gauges.append(metric_gauges[index])
        #print metric_id, metric_value, gauges, metrics['labels']
        if metric_value is not False:
            if isinstance(metric_value, list):
                metric_value = sum(metric_value) / float(len(metric_value))
            if not metric_id in self.gauges:
                self.gauges[metric_id] = GaugeMetricFamily('%s_%s' % (metric_name, metric_id), '%s' % metric_id, value=None, labels=metrics['labels'])
            self.gauges[metric_id].add_metric(gauges, value=metric_value)

    """
    Collect cluster, nodes, bucket and bucket details metrics
    """
    def _collect_metrics(self, key, values, couchbase_data):
            if key == 'cluster':
                for metrics in values['metrics']:
                    self._add_metrics(metrics, self.METRIC_PREFIX + 'cluster', [], couchbase_data)
            elif key == 'nodes':
                for node in couchbase_data['nodes']:
                    for metrics in values['metrics']:
                        self._add_metrics(metrics, self.METRIC_PREFIX + 'node', [node['hostname']], node)
            elif key == 'queries':
                for queries in couchbase_data:
                    print queries , values['metrics']
                    users= queries['users'] if 'users' in queries else 'User-NotAvailable'
                    statement= queries['preparedText'] if 'preparedText' in queries else queries['statement']
                    self._add_metrics(values['metrics'], self.METRIC_PREFIX + 'queries', [statement,queries['requestId'],queries['requestTime'],queries['state'],users], queries)
            elif key == 'buckets':
                for bucket in couchbase_data:
                    for metrics in values['metrics']:
                        self._add_metrics(metrics, self.METRIC_PREFIX + 'bucket', [bucket['name']], bucket)
                    # Get detailed stats for each bucket
                    bucket_stats = self._request_data(self.BASE_URL + bucket['stats']['uri'])
                    for bucket_metrics in values['bucket_stats']:
                        self._add_metrics(bucket_metrics, self.METRIC_PREFIX + 'bucket_stats', [bucket['name']], bucket_stats["op"]["samples"])

    """
    Clear gauges
    """
    def _clear_gauges(self):
        self.gauges = {}

    """
    Collect each metric defined in external module statsmetrics
    """
    def collect(self):
        self._clear_gauges()
        for api_key,api_values in self.metrics.items():
            # Request data for each url
            #print('calling url {0} '.format(api_values['url']))
            if api_key == 'queries':
            	couchbase_data = self._request_data(self.QUERY_URL + api_values['url'])
            	self._collect_metrics(api_key, api_values, couchbase_data)
            else:
            	couchbase_data = self._request_data(self.BASE_URL + api_values['url'])
            	self._collect_metrics(api_key, api_values, couchbase_data)

        for gauge_name, gauge in self.gauges.items():
            yield gauge

"""
Parse optional arguments
:couchase_host:port
:port
"""
def parse_args():
    parser = argparse.ArgumentParser(
        description='couchbase exporter args couchbase address and port'
    )
    parser.add_argument(
        '-c', '--couchbase',
        metavar='couchbase',
        required=False,
        help='server url from the couchbase api',
        default='http://localhost:8091'
    )
    parser.add_argument(
        '-cq', '--couchbase_query',
        metavar='couchbase_query',
        required=False,
        help='server url from the couchbase api',
        default='http://localhost:8093'
    )
    parser.add_argument(
        '-p', '--port',
        metavar='port',
        required=False,
        type=int,
        help='Listen to this port',
        default=9119
    )
    return parser.parse_args()

#if __name__ == '__main__':
def main():
	try:
		args = parse_args()
		port = int(args.port)
		REGISTRY.register(CouchbaseCollector(args.couchbase,args.couchbase_query))
		start_http_server(port)
		print "Serving at port: ", port
		while True: time.sleep(1)
	except KeyboardInterrupt:
		print(" Interrupted")
		exit(0)

if __name__ == '__main__':
			main()
