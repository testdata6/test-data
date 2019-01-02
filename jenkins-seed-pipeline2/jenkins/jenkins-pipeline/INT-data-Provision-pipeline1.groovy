def revName = env.IMAGE_VERSION.reverse()

println(revName)
def s3FolderName = revName[0]
print s3FolderName
if (s3FolderName == 'F' || env.SERVICE_NAME == 'bernerui' || env.SERVICE_NAME == 'tuterorui' || env.SERVICE_NAME == 'logui')
    env.DATAPROV = 'FALSE'

if (env.DATAPROV == 'TRUE')
node(){
    stage ('Set meta Data')
    {
    print "$IMAGE_VERSION"
    try{
    result = build job: "INT-main-data-provision-k8s",
        parameters:
            [[$class: 'StringParameterValue', name: 'NAMESPACE', value: "${NAMESPACE}"],
            [$class: 'StringParameterValue', name: 'SERVICE_NAME', value: "${SERVICE_NAME}"],
            [$class: 'StringParameterValue', name: 'CB_CLUSTER', value: "${CB_CLUSTER}"],
            [$class: 'StringParameterValue', name: 'kubeJobID', value: "${BUILD_NUMBER}-meta"],
            [$class: 'StringParameterValue', name: 'PRODUCT_VERSION', value: "$s3FolderName"],
            [$class: 'StringParameterValue', name: 'MODE', value: "metadata"]]

    }
    catch(err){
        echo "${err}"
//        echo "${buildVersion} Incorrect version !! Seems copy paste issue"
        currentBuild.result = 'FAILURE'
        throw err
        return
    }
    sh'kubectl config use-context infra-clusters.kube.plp-gmail.com'
    sh'COUNTER1=60 ; echo "podOk" > flag.txt ; kubeStatus=`kubectl describe job master-data-provision-${NAMESPACE}-${BUILD_NUMBER}-meta -n ${NAMESPACE} |grep Statuses |cut -d/ -f2 |cut -d" " -f2` ;\
       echo "running job $kubeStatus" ; while [ $kubeStatus == 0 ];do echo "Wating for status .." ; sleep 1 ; echo "${COUNTER1}" ; \
       kubeStatus=`kubectl describe job master-data-provision-${NAMESPACE}-${BUILD_NUMBER}-meta -n ${NAMESPACE} |grep Statuses |cut -d/ -f2 |cut -d" " -f2` ;\
       let let COUNTER1-=1 ; if [ "$COUNTER1" -eq 1 ];then echo "podError" > flag.txt; exit 0; fi ;done'

    env.WORKSPACE = pwd()
   def statusFlag1 = readFile "${env.WORKSPACE}/flag.txt"
   print statusFlag1
//   def errstatus = 'podError'
//   print errstatus
//   sh 'sleep 10'
   sh'kubectl logs $(kubectl get pod -n ${NAMESPACE} |grep master-data-provision-${NAMESPACE}-${BUILD_NUMBER}-meta | head -1 |cut -d" " -f1) -n ${NAMESPACE} > podStatus1.txt || echo "Waiting to start pod" '
//   sh'myPod=$(kubectl get pod -n ${NAMESPACE} |grep master-data-provision-${NAMESPACE}-${BUILD_NUMBER}-meta | head -1 |cut -d" " -f1);kubectl logs $myPod -n ${NAMESPACE} > podStatus1.txt || echo "Waiting to start pod" '

   env.WORKSPACE = pwd()
   def podStatus1 = readFile "${env.WORKSPACE}/podStatus1.txt"
   print podStatus1
   currentBuild.displayName = "${BUILD_NUMBER}-${SERVICE_NAME}-${IMAGE_VERSION}"
   if ( statusFlag1 == "podError" )
       {
           currentBuild.result = 'UNSTABLE'
           print "Job UNSTABLE"
       }

 }
}

if (env.DATAPROV == 'TRUE')
node(){
    stage ('Master Data Provision')
    {
    try{
    result = build job: "INT-main-data-provision-k8s",
        parameters:
            [[$class: 'StringParameterValue', name: 'NAMESPACE', value: "${NAMESPACE}"],
            [$class: 'StringParameterValue', name: 'SERVICE_NAME', value: "${SERVICE_NAME}"],
            [$class: 'StringParameterValue', name: 'CB_CLUSTER', value: "${CB_CLUSTER}"],
            [$class: 'StringParameterValue', name: 'kubeJobID', value: "${BUILD_NUMBER}-prov"],
            [$class: 'StringParameterValue', name: 'PRODUCT_VERSION', value: "$s3FolderName"],
            [$class: 'StringParameterValue', name: 'MODE', value: 'Provision']]

    }
    catch(err){
        echo "${err}"
//        echo "${buildVersion} Incorrect version !! Seems copy paste issue"
        currentBuild.result = 'FAILURE'
        throw err
        return
    }
    BUILD_JOB_ID = "${result.displayName}"
    echo "${BUILD_JOB_ID}"
 sh'kubectl config use-context infra-clusters.kube.plp-gmail.com'
 sh'COUNTER2=60 ; echo "PodOk" > flag2.txt ; kubeStatus=`kubectl describe job master-data-provision-${NAMESPACE}-${BUILD_NUMBER}-prov -n ${NAMESPACE} |grep Statuses |cut -d/ -f2 |cut -d" " -f2` ;\
       echo "running job $kubeStatus" ; while [ $kubeStatus == 0 ];do echo "Wating for status .." ; sleep 1 ; echo $COUNTER2 ; \
       kubeStatus=`kubectl describe job master-data-provision-${NAMESPACE}-${BUILD_NUMBER}-prov -n ${NAMESPACE} |grep Statuses |cut -d/ -f2 |cut -d" " -f2` ;\
       let let COUNTER2-=1 ; if [ "$COUNTER2" -eq 1 ];then echo "podError" > flag2.txt; exit 0; fi ;done'

   env.WORKSPACE = pwd()
   def statusFlag2 = readFile "${env.WORKSPACE}/flag2.txt"
   print statusFlag2
   sh'kubectl logs $(kubectl get pod -n ${NAMESPACE} |grep master-data-provision-${NAMESPACE}-${BUILD_NUMBER}-prov | head -1 |cut -d" " -f1) -n ${NAMESPACE} > podStatus2.txt || echo "Waiting to start pod"'

   env.WORKSPACE = pwd()
   def podStatus2 = readFile "${env.WORKSPACE}/podStatus2.txt"
   print podStatus2

    if ( podStatus2 == 'podError' )
        currentBuild.result = 'UNSTABLE'
     }
  }

node(){
    stage ('Kube deployment')
    {
    echo "${SERVICE_NAME}-${IMAGE_VERSION}"
    currentBuild.displayName = "${BUILD_NUMBER}-${SERVICE_NAME}-${IMAGE_VERSION}"
    sh'kubectl config use-context infra-clusters.kube.plp-gmail.com'

    try{
    healthResult = build job: 'INT-main-deployer-k8s',
        parameters:
           [[$class: 'StringParameterValue', name: 'NAMESPACE', value: "${NAMESPACE}"],
            [$class: 'StringParameterValue', name: 'SERVICE_NAME', value: "${SERVICE_NAME}"],
            [$class: 'StringParameterValue', name: 'CB_CLUSTER', value: "${CB_CLUSTER}"],
            [$class: 'StringParameterValue', name: 'IMAGE_VERSION', value: "${IMAGE_VERSION}"],
            [$class: 'StringParameterValue', name: 'CPU_LIMIT', value: "${CPU_LIMIT}"],
            [$class: 'StringParameterValue', name: 'CPU_REQUEST', value: "${CPU_REQUEST}"],
            [$class: 'StringParameterValue', name: 'MEMORY_LIMIT', value: "${MEMORY_LIMIT}"],
            [$class: 'StringParameterValue', name: 'MEMORY_REQUEST', value: "${MEMORY_REQUEST}"],
            [$class: 'StringParameterValue', name: 'PORT', value: "${PORT}"],
            [$class: 'StringParameterValue', name: 'REPLICA_COUNT', value: "${REPLICA_COUNT}"]]
    }
    catch(err){
        print err
//        echo "Deployment failed. Your build will roll back if autorollback is yes"
        currentBuild.result = 'UNSTABLE'
        print "Kube Deployment fail"
//        currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_Health_Fail"
        return
    }

// sh'kubectl log $(kubectl get pod -n ${NAMESPACE} |grep ${SERVICE_NAME}  | head -1 |cut -d" " -f1) -n ${NAMESPACE} > podStatus3.txt'
   sh'sleep 30 ; kubectl get pod -n ${NAMESPACE} |grep -w ${SERVICE_NAME} > podStatus3.txt || echo "No resorce Found" > podStatus3.txt'
   env.WORKSPACE = pwd()
   def podStatus3 = readFile "${env.WORKSPACE}/podStatus3.txt"
   print podStatus3

// print "Kube Deployment Successful"
    }
  }
node(){
    stage ('Health Check / Logs / Status')
      {

    echo "${SERVICE_NAME}-${IMAGE_VERSION} KUBE-NFR"
    sh'kubectl config use-context infra-clusters.kube.plp-gmail.com'
/*   if (env.DATAPROV == 'TRUE')
      {
    try{
    healthResult = build job: 'Health-aws-dashboard-pipeline',
        parameters:
            [[$class: 'StringParameterValue', name: 'DEPLOY_VERSION', value: "${IMAGE_VERSION}"],
            [$class: 'StringParameterValue', name: 'SERVICE_NAME', value: "${SERVICE_NAME}"],
            [$class: 'StringParameterValue', name: 'ENV_NAME', value: "${NAMESPACE}"]]
    }
    catch(err){
        print err
        print "Health fail"
        currentBuild.displayName = "${BUILD_NUMBER}-${SERVICE_NAME}-${IMAGE_VERSION}_Health_Fail"

        return
    }

      } */


  sh'kubectl get pod -n ${NAMESPACE} |grep -w ${SERVICE_NAME} > podStatus3.txt'
   env.WORKSPACE = pwd()
   def podStatus3 = readFile "${env.WORKSPACE}/podStatus3.txt"
   print podStatus3

   }
 }

if (env.DATAPROV == 'TRUE')
node(){
    stage ('Kube Cleanup')
    {
    sh'kubectl config use-context infra-clusters.kube.plp-gmail.com'
    sh 'kubectl get job -n ${NAMESPACE} | grep master-data-provision-${NAMESPACE}-${BUILD_NUMBER} ; \
    oldPod=`kubectl get job -n ${NAMESPACE} | grep master-data-provision-${NAMESPACE}-${BUILD_NUMBER} |awk {\'print $1\'}` ; \
    echo $oldPod ; \
    for i in $oldPod ; do kubectl delete job $i -n ${NAMESPACE} ; done '
    }
}
