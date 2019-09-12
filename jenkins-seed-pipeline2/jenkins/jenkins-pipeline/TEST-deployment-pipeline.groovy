node (){
    // deployEnv = "Dev-Kube-deployer"
     stage 'Getting previous Version'
     currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}"
     sh 'curl -X GET -v -u jenkins:password http://10.168.1.222:8080/job/Dev-Kube-deployer-${serviceName}/lastSuccessfulBuild/api/json 2> /dev/null | jq .displayName > previousVersion.txt'
     previousVersion=readFile('previousVersion.txt')
     print previousVersion
}
 node(){
     stage 'DEV Deployment'
     try{
     result = build job: "Dev-Kube-deployer-${serviceName}",
         parameters:
             [[$class: 'StringParameterValue', name: 'buildVersion', value: "${buildVersion}"]]

     }
     catch(err){
         echo "${err}"
         currentBuild.result = 'FAILURE'
         throw err
         return
     }
     BUILD_JOB_ID = "${result.id}"
     echo "${BUILD_JOB_ID}"
     echo "${buildVersion}"

}

 node(){
     stage 'Health Check'

     echo "${serviceName}-${buildVersion} AWS-QA"


     try{
     healthResult = build job: 'Health-aws-dashboard-pipeline',
         parameters:
             [[$class: 'StringParameterValue', name: 'DEPLOY_VERSION', value: "${buildVersion}"],
             [$class: 'StringParameterValue', name: 'SERVICE_NAME', value: "${serviceName}"],
             [$class: 'StringParameterValue', name: 'ENV_NAME', value: "AWS-DEV"]]
     }
     catch(err){
         print err
         echo "Deployment failed. Please deploy manually"
         currentBuild.result = 'UNSTABLE'
         print "Health fail"
         currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_Health_Fail"
         return
     }

 }


node(){
     stage 'API-Automation-Sanity-QA'


     if (env.skipUiTest != 'TRUE')
     if ( currentBuild.result != 'UNSTABLE' )
     try{
     uatresult = build job: 'API-Automation-Sanity-QA-devops',
         parameters:
             [[$class: 'StringParameterValue', name: 'Sprint', value: "AWS UAT : Sprint 28"],
             [$class: 'StringParameterValue', name: 'env', value: "uat"],
             [$class: 'StringParameterValue', name: 'ProductID', value: "${ProductID}"]]
             echo "${serviceName}-${buildVersion} AWS-UAT"
     }
     catch(err){
         print err
         echo "Deployment failed. Please deploy manually"
         currentBuild.result = 'UNSTABLE'
         print "API fail"
         currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_API_Fail"
         return
     }

     if (env.skipUiTest == 'TRUE')
        echo "UI Test is skipping"

 }
/*
node(){
     stage 'QA-UI-Automation'

     echo "${serviceName}-${buildVersion} AWS-UAT"

     if ( currentBuild.result != 'UNSTABLE' )
     try{
     uatresult = build job: 'QA-UI-Automation-devops',
         parameters:
             [[$class: 'StringParameterValue', name: 'sprint', value: "Sprint 28"],
             [$class: 'StringParameterValue', name: 'course', value: "M1_Adaptive"],
             [$class: 'StringParameterValue', name: 'execution_envoirment', value: "UAT"],
             [$class: 'StringParameterValue', name: 'groups', value: "smoke"]]
     }
     catch(err){
         print err
         echo "Deployment failed. Please deploy manually"
         currentBuild.result = 'UNSTABLE'
         currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_UI_Fail"
     }
     tarUrl = "<a href='http://test-reports.plp-gmail.com:8080/APIDashboard/uat/' target='_blank'>API Test Dashboard link</a><br>"
     currentBuild.description = "${tarUrl}"

 }

 */

if ( currentBuild.result == 'UNSTABLE' )
    {
 stage 'Roll Back'
 node(){


       if ( env.autoRollBack == 'YES')
       try{
       result = build job: "Dev-Kube-deployer-${serviceName}",
           parameters:
               [[$class: 'StringParameterValue', name: 'buildVersion', value: "${previousVersion}"]]
               echo "Roll Back to $previousVersion"

       }
      catch(err){
          print err
          echo "Deployment failed. Please deploy manually"
          currentBuild.result = 'UNSTABLE'

      }
      if ( env.autoRollBack != 'YES')
          echo "Roll Back is Disabled"
      // currentBuild.displayName = "${BUILD_NUMBER}-${serviceName}-${buildVersion}_RollBack"
  }

      }
