properties([disableConcurrentBuilds()])
node (){
     stage ('Getting previous Version')
        {
     currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}"
     sh 'curl -X GET -v -u plp-jenkins:password http://10.168.0.227:8080/job/QA-Kube-deployer-${serviceName}/lastSuccessfulBuild/api/json 2> /dev/null | jq .displayName > previousVersion.txt'
     previousVersion=readFile('previousVersion.txt')
     deploymentInfo="${BUILD_NUMBER}-${serviceName}-${buildVersion}-QA"
     print deploymentInfo

     print previousVersion

     if(env.buildVersion.startsWith("fTeam_"))
        {
           print "$env.buildVersion is not master build !!"
           currentBuild.result = 'FAILURE'
        }
       }
}
 node(){
     stage ('QA Deployment')
      {
     try{
     result = build job: "QA-Kube-deployer-${serviceName}",
         parameters:
             [[$class: 'StringParameterValue', name: 'buildVersion', value: "${buildVersion}"]]

     }
     catch(err){
         echo "${err}"
         echo "${buildVersion} Incorrect version !! Seems copy paste issue"
         currentBuild.result = 'FAILURE'
         throw err
         return
     }
     BUILD_JOB_ID = "${result.displayName}"
     echo "${BUILD_JOB_ID}"
 //    echo "${serviceName}-${buildVersion} AWS-QA"
 //    tarUrl = "<a href='https://jenkins.plp-gmail.com/view/PERF-Deploy/job/PERF-kube-deployer-${serviceName}' target='_blank'>Tar Download link</a><br>"

 //    currentBuild.description = "${tarUrl}"
   }
}

 node(){
     stage ('Health Check')
     {
     echo "${serviceName}-${buildVersion} AWS-QA"
     try{
     healthResult = build job: 'Health-aws-dashboard-pipeline',
         parameters:
             [[$class: 'StringParameterValue', name: 'DEPLOY_VERSION', value: "${buildVersion}"],
             [$class: 'StringParameterValue', name: 'SERVICE_NAME', value: "${serviceName}"],
             [$class: 'StringParameterValue', name: 'ENV_NAME', value: "AWS-QA"]]
     }
     catch(err){
         print err
         echo "Deployment failed. Your build will roll back if autorollback is yes"
         currentBuild.result = 'UNSTABLE'
         print "Health fail"
         currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_Health_Fail"
         return
     }
   }
 }

node(){
     stage ('API-Automation-Sanity-QA')
     {
     echo "${serviceName}-${buildVersion} AWS-QA"

  if (env.skipApiTest != 'TRUE' && currentBuild.result != 'UNSTABLE' )
     try{
     uatresult = build job: 'API-Automation-Sanity-QA-devops',
         parameters:
             [[$class: 'StringParameterValue', name: 'env', value: "qa"],
     //        [$class: 'StringParameterValue', name: 'env', value: "qa"],
             [$class: 'StringParameterValue', name: 'deploymentInfo', value: "${deploymentInfo}"]]
     //        [$class: 'StringParameterValue', name: 'ProductID', value: "${ProductID}"]]
     }
     catch(err){
         print err
         echo "Deployment failed. Your build will roll back if autorollback is yes"
         currentBuild.result = 'UNSTABLE'
         print "API fail"
         currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_API_Fail"
         return
     }
   }
 }

node(){
     stage ('QA-UI-Automation')
     {
  if (env.skipUiTest != 'TRUE' && currentBuild.result != 'UNSTABLE' )
     try{
     uatresult = build job: 'QA-UI-Automation-devops',
         parameters:
             [[$class: 'StringParameterValue', name: 'execution_envoirment', value: "Qa"],
      //       [$class: 'StringParameterValue', name: 'course', value: "M1_Adaptive"],
      //       [$class: 'StringParameterValue', name: 'execution_envoirment', value: "Qa"],
             [$class: 'StringParameterValue', name: 'deploymentInfo', value: "${deploymentInfo}"],
             [$class: 'StringParameterValue', name: 'groups', value: "smoke"]]
    echo "${serviceName}-${buildVersion} AWS-QA"
     }
     catch(err){
         print err
         echo "Deployment failed. Your build will roll back if autorollback is yes"
         currentBuild.result = 'UNSTABLE'
         currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_UI_Fail"
     }
     tarUrl = "<a href='http://test-reports.plp-gmail.com:8080/APIDashboard/qa/' target='_blank'>API Test Dashboard link</a><br>"
     currentBuild.description = "${tarUrl}"
     if (env.skipUiTest == 'TRUE')
        echo "UI Test is skipping"
    }
 }

if ( currentBuild.result == 'UNSTABLE' )
    {
  node(){
    stage ('Roll Back')
      {

       if ( env.autoRollBack == 'YES')
       try{
       result = build job: "QA-Kube-deployer-${serviceName}",
           parameters:
               [[$class: 'StringParameterValue', name: 'buildVersion', value: "${previousVersion}"]]
               echo "Roll Back to $previousVersion"

       }
      catch(err){
          print err
          echo "Deployment failed. Please RollBack manually"
          currentBuild.result = 'UNSTABLE'

      }
      if ( env.autoRollBack != 'YES')
          echo "Roll Back is Disabled"
  }

      }
  }    
