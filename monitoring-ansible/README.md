# Microservice Monitoring Box (Kubernetes & ECS)

## Getting Started

To deploy this Monitoring Box you should have AWSCli, Kubectl on your system. This Monitor Box is designed for Kubernetes and ECS based Miroservice Infrastructure.

Microservices give you flexibility , hyperscale  and room to grow and we observed some highlighted a series of challenges for monitoring cloud-native container-based systems, and introduced microservice monitoring , alerting and visualisation tool, which may assist devops with testing microservice monitoring at scale.



##### PLP MonitoringBox

        					                  -----------
        					                 | Kubernetes |
        					                  -----------
        					                      / \
        					                     /   \
        					                    /     \
        					      -----------------  ------------------  
        					     | Dio Monitoring |  Marvel Monitoding |
        					      -----------------  ------------------
        					                /               \
        					               /                 \
        					              /                   \
              			 -------------------------     ----------------------------
              			| int | qa | uat | perf         | dev | qa |
              			 ---------------------------	 --------------------------------	         
              			               /                                 \
              			 ---------------------------          ----------------------------------
              			 Couchbase Monitoring                  Not Implemented
              			 kafka Monitoring                      Not Implemented
              			 APM Monitoring                        Not Implemented
                     K8s Monitoring                        Not Implemented
                     LoadBalancer Monitoring               Not Implemented
                     Container Monitoring                  Not Implemented
                     Node Monitoring                       Not Implemented
                     ---------------------------          -----------------------------------


## Introduction

This Monitoing Box is based on White Box Monitoring.White-Box Monitoring based on metrics exposed by the internals of the system like exporters including logs, interfaces like the Java Virtual Machine and custom metrics which is exposed by developers  and profiling Interface, or an HTTP handler that emits internal statistics.

This Code will deploy a monitoring box on AWS-ECS environment using CloudFormation .Through this Box you can Monitor Kubernetes & ECS cluster Monitoring along with Kafka Monitoring ,CouchbaseDB Monitoring ,LoadBalancer Monitoring and JVM & Application Monitoring.

## Components

This Monitoring Box has some monitoring components for infrastructure metrics and some logging components for infrastructure / apllication logging .

### Monitoring Components

| Component | Description |
| --- | --- |
| Grafana | Data visualization & Monitoring with support for Graphite, InfluxDB, Prometheus, Elasticsearch and many more databases.|
| Prometheus | An open-source monitoring system with a dimensional data model, flexible query language, efficient time series database and modern alerting approach. |
| Alertmanager | The Alertmanager handles alerts sent by client applications such as the Prometheus server. |
| Cadvisor | CAdvisor (Container Advisor) provides container users an understanding of the resource usage and performance characteristics of their running containers.|
| NodeExporter | Prometheus exporter for hardware and OS metrics exposed by *NIX kernels, written in Go with pluggable metric collectors. |
| EcsExporter | Export AWS ECS cluster metrics to Prometheus.|
| kube-state-metrics | kube-state-metrics is a simple service that listens to the Kubernetes API server and generates metrics about the state of the objects.|
| CouchbaseExporter | Couchbase Exporter for Couchbase metrics and Queries .|
| KafkaExporter| Kafka Exporter for Kafka Cluster monitoring. |
| CloudWatchExporter | An exporter for Amazon CloudWatch  for Prometheus |

### Logging Components

| Component | Description |
| --- | --- |
| Fluentd| Fluentd allows you to unify data collection and consumption for a better use and understanding of data..|
| AWSLogs Driver | tasks to send log information to CloudWatch Logs.|
| ElasticSearch | Elasticsearch is a search engine based on Lucene. It provides a distributed, multitenant-capable full-text search engine with an HTTP web interface and schema-free JSON documents |

### Deploy Monitoring Box into my AWS account

You can deploy monuitoring box by using CloudFormation and Ansible .

#### Deploying MonitoringBox by using Ansible .

##### Deploying MonitoingBox with ECS Cluster.

```python
ansible-playbook -vv --vault-id @prompt -e "env_ver=existing-vpc action_type=create platform=ecs ssl_enable=true project=marvel" site.yml
```

#### Create a EBS Volume for Monitoing Box Persistant Storage

Create a EBS Volume to store prometheus,grafana data.   

#### Create a Elastic IP for Monitoing Box

Create a Elastic IP for ECS Cluster node.

#### Change the ECS host instance type

This is specified in the [master.yaml](master.yaml) template.

By default, [t2.large](https://aws.amazon.com/ec2/instance-types/) instances are used, but you can change this by modifying the following section:

```
ECS:
  Type: AWS::CloudFormation::Stack
    Properties:
      TemplateURL: ...
      Parameters:
        ...
        InstanceType: t2.large
        InstanceCount: 1
        ...
```


#### Build your images and Push to ECR

- Push your service images to a registry somewhere (e.g., [Amazon ECR](https://aws.amazon.com/ecr/)).
- Update `Image` for recpective services .
- Update CPU/Memory for recpective services .
- Update Service URL's for Grafana , Prometheus and Alertmanager .
- GO to your AWS `CloudFormation` console and run master template.

##  Deployment

- Infrastructure Deployment
- Service Deployment
- Addons Service Deployment
- Centralized Logging Deployment

## Infrastructure Deployment

- Setup a new VPC with public and private subnets.
- Deploy a highly vailable ECS Cluster across two [Availability Zones] with persitance storage (EBS).
- An Application Load Balancer to the public subnets to handle inbound traffic.
- ALB host based routes for each ECS service to route the inbound traffic to the correct service.
- Centralized Monitoring-Box logging with Amazon CloudWatch Logs.
- Setup SecurityGroup for ALB and ECS Node to manage inbound traffic.

### Change the VPC or subnet IP ranges

This set of templates deploys the following network design:

| Item | CIDR Range | Usable IPs | Description |
| --- | --- | --- | --- |
| VPC | 10.180.0.0/16 | 65,536 | The whole range used for the VPC and all subnets |
| Public Subnet | 10.180.8.0/21 | 2,041 | The public subnet in the first Availability Zone |
| Public Subnet | 10.180.16.0/21 | 2,041 | The public subnet in the second Availability Zone |
| Private Subnet | 10.180.24.0/21 | 2,041 | The private subnet in the first Availability Zone |
| Private Subnet | 10.180.32.0/21 | 2,041 | The private subnet in the second Availability Zone |

You can adjust the CIDR ranges used in this section of the [master.yaml](master.yaml) template:

```
VPC:
  Type: AWS::CloudFormation::Stack
    Properties:
      TemplateURL: !Sub ${TemplateLocation}/infrastructure/vpc.yaml
      Parameters:
        EnvironmentName:    !Ref AWS::StackName
        VpcCIDR:            10.180.0.0/16
        PublicSubnet1CIDR:  10.180.8.0/21
        PublicSubnet2CIDR:  10.180.16.0/21
        PrivateSubnet1CIDR: 10.180.24.0/21
        PrivateSubnet2CIDR: 10.180.32.0/21
```
### Deploy Monitoing Box in Existing VPC

If you have existing VPC then we can deploy Monitoing Box in existing VPC . Please follow below steps .

#### Comment Out the VPC selction of [master.yaml](master.yaml)

```
#VPC:
#  Type: AWS::CloudFormation::Stack
#    Properties:
#      TemplateURL: !Sub ${TemplateLocation}/infrastructure/vpc.yaml
#      Parameters:
#        EnvironmentName:    !Ref AWS::StackName
#        VpcCIDR:            10.180.0.0/16
#        PublicSubnet1CIDR:  10.180.8.0/21
#        PublicSubnet2CIDR:  10.180.16.0/21
#        PrivateSubnet1CIDR: 10.180.24.0/21
#        PrivateSubnet2CIDR: 10.180.32.0/21
```
#### Put your existing VPC_ID in [master.yaml](master.yaml) for all services .

```
VPC: !GetAtt VPC.Outputs.VPC
```
to
```
VPC: VPC_ID
```
#### Select Subnet in ECS section of [master.yaml](master.yaml) .

Please select the subnet in which you have created your EBS Volume .

```
Subnets: !GetAtt VPC.Outputs.PublicSubnet1
```
to
```
Subnets: !GetAtt VPC.Outputs.PublicSubnet2
```
#### Defined the Subnet with recpective AvailabilityZone in  [vpc.yaml](infrastructure/vpc.yaml) .
```
PublicSubnet1:
    Type: AWS::EC2::Subnet
    Properties:
        VpcId: !Ref VPC
        AvailabilityZone: us-west-2a
        CidrBlock: !Ref PublicSubnet1CIDR
        MapPublicIpOnLaunch: true
        Tags:
            - Key: Name
              Value: !Sub ${EnvironmentName} Public Subnet (AZ1)

PublicSubnet2:
    Type: AWS::EC2::Subnet
    Properties:
        VpcId: !Ref VPC
        AvailabilityZone: us-west-2b
        CidrBlock: !Ref PublicSubnet2CIDR
        MapPublicIpOnLaunch: true
        Tags:
            - Key: Name
              Value: !Sub ${EnvironmentName} Public Subnet (AZ2)

PrivateSubnet1:
    Type: AWS::EC2::Subnet
    Properties:
        VpcId: !Ref VPC
        AvailabilityZone: us-west-2a
        CidrBlock: !Ref PrivateSubnet1CIDR
        MapPublicIpOnLaunch: false
        Tags:
            - Key: Name
              Value: !Sub ${EnvironmentName} Private Subnet (AZ1)

PrivateSubnet2:
    Type: AWS::EC2::Subnet
    Properties:
        VpcId: !Ref VPC
        AvailabilityZone: us-west-2b
        CidrBlock: !Ref PrivateSubnet2CIDR
        MapPublicIpOnLaunch: false
        Tags:
            - Key: Name
              Value: !Sub ${EnvironmentName} Private Subnet (AZ2)
```                  

### Deploy Core Componentes of Monitoring Box .

| Template | Description |
| --- | --- |
| [master.yaml](master.yaml) | Please add your resources which you want to deploy in Monitoring Box |


| Template | Description |
| --- | --- |
| [infrastructure/vpc.yaml](infrastructure/vpc.yaml) | Deploy a VPC with a pair of public and private subnets spread across two Availability Zones. It deploys an [Internet gateway](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Internet_Gateway.html), with a default route on the public subnets. It deploys a pair of NAT gateways (one in each zone), and default routes for them in the private subnets. |
| [infrastructure/security-groups.yaml](infrastructure/security-groups.yaml) | Deploy [security groups](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_SecurityGroups.html) this will create a SecurityGroup for ALB and one SecurityGroup for ECS Nodes. |
| [infrastructure/load-balancers.yaml](infrastructure/load-balancers.yaml) | Deploy an ALB to the public subnets, which exposes the vaDious ECS services. It is created in in a separate nested template, so that it can be referenced by all of the other nested templates and so that the vaDious ECS services can register with it. |
| [infrastructure/ecs-cluster.yaml](infrastructure/ecs-cluster.yaml) | Deploy an ECS cluster to the private subnets using an Auto Scaling group and installs the AWS SSM agent with related policy requirements. |

## Service Deployment

| Template | Description |
| --- | --- |
| [services/Grafana/service.yaml](services/Grafana/service.yaml) | Deploy  [Grafana](https://grafana.com)v5.0.0 service on ECS clsuer |
| [services/Prometheus/service.yaml](services/Prometheus/service.yaml) | Deploy [Prometheus](https://prometheus.io/docs/introduction/overview/)v2.2.1 Service On ECS Cluster |
| [services/Alertmanager/service.yaml](services/Alertmanager/service.yaml) | Deploy [Alertmanager](https://prometheus.io/docs/alerting/alertmanager/)v0.14.0 . |
| [services/Smtp/service.yaml](services/Smtp/service.yaml) | Deploy Smtp services for Alertmanager and Grafana.|
| [services/Mysql/service.yaml](services/Mysql/service.yaml) | Deploy mysql services for Grafna backend.|
| [services/JiraAlert/service.yaml](services/JiraAlert/service.yaml) | Deploy JiraAlert for jira integraion for Alertmanager|

## Addons Service Deployment .

To deploy Addons services for additional monitoring like kafka , couchbase and Kube state metric and ECS Exporter . Addons services deployment depends on which kind of infrastructure we have like Kubernetes and ECS .

- Deploy Kafka Exporter.
- Deploy Couchbase Exporter.
- Deploy ECS Exporter.
- Deploy Kube State Metric.
- Deploy Cloudwatch Exporter.
- Deploy NodeExporter.
- Deploy CAdvisor.

### Addons Services Deployment on Kubernetes

We do not need to deploy CAdvisor on kubernetes because Kubelet has inbuilt CAdvisor for container monitoring .

| Template | Description |
| --- | --- |
| [services/Addons/Kafka/Kubernetes/Kafka.yaml](services/Addons/Kafka/Kubernetes/Kafka.yaml) | Deploy Kafka Monitoring on Kubernetes|
| [services/Addons/CouchbaseDB/Kubernetes/CouchbaseDB.yaml](services/Addons/CouchbaseDB/Kubernetes/CouchbaseDB.yaml) | Deploy CouchbaseDB Monitoring on Kubernetes |
| [services/Addons/Cloudwatch/Kubernetes/cloudwatch.yaml](services/Addons/Cloudwatch/Kubernetes/cloudwatch.yaml) | Deploy CloudWatch Monitoing for AWS resource on Kubernetes |
| [services/Addons/NodeExporter/Kubernetes/node-exporter.yml](services/Addons/NodeExporter/Kubernetes/node-exporter.yml) | Deploy NodeExporter as DemonSet on Kubernetes cluster |
| [services/Addons/Kube-state-Metric/Kubernetes/K8s-state-metrics.yaml](services/Addons/Kube-state-Metric/Kubernetes/K8s-state-metrics.yaml) | Deploy Kube state Metric to monitoring Kubernetes Cluster |
| [services/Addons/ECS/Kubernetes/ecs.yaml](services/Addons/services/Addons/ECS/Kubernetes/ecs.yaml) | Deploy ecs exporter to monitor monitoring box itself or another ECS cluster |

### Addons Services Deployment on ECS

| Template | Description |
| --- | --- |
| [services/Addons/Kafka/ECS/Kafka.yaml](services/Addons/Kafka/ECS/Kafka.yaml) | Deploy Kafka Monitoring on ECS|
| [services/Addons/CouchbaseDB/ECS/CouchbaseDB.yaml](services/Addons/CouchbaseDB/ECS/CouchbaseDB.yaml) | Deploy CouchbaseDB Monitoring on ECS |
| [services/Addons/Cloudwatch/ECS/cloudwatch.yaml](services/Addons/Cloudwatch/ECS/cloudwatch.yaml) | Deploy CloudWatch Monitoing for AWS resource on ECS |
| [services/Addons/NodeExporter/ECS/node-exporter.yml](services/Addons/NodeExporter/ECS/node-exporter.yml) | Deploy NodeExporter on ECS cluster |
| [services/Addons/ECS/ECS/ecs.yaml](services/Addons/services/Addons/ECS/Kubernetes/ecs.yaml) | Deploy ecs exporter to monitor monitoring box itself or another ECS cluster |
| [services/Addons/ECS/ECS/CAdvisor.yaml](services/Addons/services/Addons/ECS/Kubernetes/CAdvisor.yaml) | Deploy CAdvisor to monitor docker container |
