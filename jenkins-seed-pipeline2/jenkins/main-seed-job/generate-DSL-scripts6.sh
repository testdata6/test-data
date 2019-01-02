#!/bin/bash
# This script generates actual dsl code in dynamic folder by useing groovy templates.
# This script executes on Jenkins during seed job build.

#Creating new template file without sonar configs.
#githubCredential='f0138f4b-1cae-4fa0-8052-adbecba0a674'
githubCredential='AAAAAAAAAAa'
jenkinsSlaveLabel="aws-dynamic-slave"
#Default value is true, seting to FALSE for UI or which do not have bucket.
dataProvision='TRUE'

cat marvel-template/build_marvel_job_template_sonarenable.groovy | grep -v SONARQUBE_CONFIG > marvel-template/build_marvel_job_template_sonardisable.groovy
cat marvel-template/marvel_job_template_gradle_sonarenable.groovy | grep -v SONARQUBE_CONFIG > marvel-template/marvel_job_template_gradle_sonardisable.groovy


#main loop to create jobs.
cat app-list-input-withPort-MARVEL.txt |grep -v '^#'| grep -v ^$ |while read serviceName servicePort replicaCount gitUrl sonarProjectName cbCluster templateType podCpu podMemory
#cat app-list-input-withPort.txt |grep -v '^#'| grep -v ^$ | tr A-Z a-z |while read serviceName servicePort gitUrl sonarProjectName templateType
do

#cbCluster=`echo $gitUrl | rev | cut -d- -f1 | rev`; echo $cbCluster
cpuLimit=`echo $podCpu |cut -d: -f1`
cpuRequest=`echo $podCpu |cut -d: -f2`

memoryLimit=`echo $podMemory |cut -d: -f1`
memoryRequest=`echo $podMemory |cut -d: -f2`

cp marvel-template/marvel_job_template_gradle_${templateType}.groovy dynamic-list/${serviceName}MarvelGradleBuildJob.groovy


for domainTeam in `cat domain-list.txt |grep -v '^#'`
do
  nameSpace=$( echo $domainTeam | tr [:upper:] [:lower:] )
  tier='backend'
  dataProvision='TRUE'
  if echo $serviceName | grep -e loginui -e learnerui -e instructorui -e loginuiplp ; then tier='frontend'; fi
  if echo $serviceName | grep -e loginui -e learnerui -e instructorui -e loginuiplp -e mpu -e tdxe; then dataProvision='FALSE'; fi
  echo $serviceName $gitUrl $cbCluster $domainTeam $nameSpace $tier $dataProvision
  cp marvel-template/build_DOMAIN-TEAM_job_template_uiservice.txt marvel-template/build_${domainTeam}_job_template_uiservice.groovy
  cat marvel-template/build_DOMAIN-TEAM_job_template_sonarenable.txt | grep -v SONARQUBE_CONFIG > marvel-template/build_${domainTeam}_job_template_sonardisable.groovy
  cp marvel-template/build_${domainTeam}_job_template_${templateType}.groovy dynamic-list/${serviceName}_${domainTeam}BuildJob.groovy
  cp marvel-template/DOMAIN-TEAM_view.txt dynamic-list/${domainTeam}_view.groovy

  sed -i "s/DOMAIN-TEAM/${domainTeam}/" dynamic-list/${domainTeam}_view.groovy
  #creating Dev/Sprint Build Jobs
  sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}_${domainTeam}BuildJob.groovy
  sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}_${domainTeam}BuildJob.groovy
  sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}_${domainTeam}BuildJob.groovy
  sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}_${domainTeam}BuildJob.groovy
  sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}_${domainTeam}BuildJob.groovy
  sed -i "s/DOMAIN-TEAM/${domainTeam}/" dynamic-list/${serviceName}_${domainTeam}BuildJob.groovy

  sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/SERVICEPORT/${servicePort}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/COUCHBASECLUSTER/${cbCluster}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/REPLICACOUNT/${replicaCount}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/DOMAIN-TEAM/${domainTeam}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/DOMAIN-NAMESPACE/${nameSpace}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/TIER_VALUE/${tier}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/CBDATAPROVISION/${dataProvision}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/PODMEMORY_LIMIT/${memoryLimit}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy
  sed -i "s/PODMEMORY_REQUEST/${memoryRequest}/" dynamic-list/${domainTeam}_${serviceName}_job_template.groovy

done

#creating UAT Build Jobs
sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}UatBuildJob.groovy
sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}UatBuildJob.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}UatBuildJob.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}UatBuildJob.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}UatBuildJob.groovy

#creating Master Build Jobs
sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}BuildJob.groovy
sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}BuildJob.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}BuildJob.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}BuildJob.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}BuildJob.groovy

#creating MARVEL Build Jobs
sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}MarvelBuildJob.groovy
sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}MarvelBuildJob.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}MarvelBuildJob.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}MarvelBuildJob.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}MarvelBuildJob.groovy

#creating MARVEL GRADLE Build Jobs
#sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}MarvelGradleBuildJob.groovy
#sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}MarvelGradleBuildJob.groovy
#sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}MarvelGradleBuildJob.groovy
#sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}MarvelGradleBuildJob.groovy
#sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}MarvelGradleBuildJob.groovy

#creating PR Build Jobs
sed -i "s/SERVICENAME/${serviceName}/" pr-jobs/${serviceName}prJob.groovy
sed -i "s/GITHUBURL/${gitUrl}/" pr-jobs/${serviceName}prJob.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" pr-jobs/${serviceName}prJob.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" pr-jobs/${serviceName}prJob.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" pr-jobs/${serviceName}prJob.groovy

#creating PERF Build Jobs
sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}PerfBuildJob.groovy
sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}PerfBuildJob.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}PerfBuildJob.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}PerfBuildJob.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}PerfBuildJob.groovy

#creating Dev/Sprint Build Jobs
sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}DevBuildJob.groovy
sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}DevBuildJob.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}DevBuildJob.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}DevBuildJob.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}DevBuildJob.groovy


#creating Stage hotFix Build Jobs
sed -i "s/SERVICENAME/${serviceName}/" dynamic-list/${serviceName}StageBuildJob.groovy
sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}StageBuildJob.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}StageBuildJob.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}StageBuildJob.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}StageBuildJob.groovy


for jobName in `ls marvel-template/ | grep .groovy$ | grep -v ^build | cut -d. -f1`
   do
cp marvel-template/${jobName}.groovy dynamic-list/${serviceName}_${jobName}.groovy


sed -i "s/SERVICENAME/${serviceName}/g" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/GITHUBURL/${gitUrl}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/SONARPROJECTNAME/${sonarProjectName}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/SERVICEPORT/${servicePort}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/COUCHBASECLUSTER/${cbCluster}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/GITHUBCREDENTIAL/${githubCredential}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/JENKINSSLAVELABEL/${jenkinsSlaveLabel}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/REPLICACOUNT/${replicaCount}/" dynamic-list/${serviceName}_${jobName}.groovy

sed -i "s/PODCPU_LIMIT/${cpuLimit}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/PODCPU_REQUEST/${cpuRequest}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/PODMEMORY_LIMIT/${memoryLimit}/" dynamic-list/${serviceName}_${jobName}.groovy
sed -i "s/PODMEMORY_REQUEST/${memoryRequest}/" dynamic-list/${serviceName}_${jobName}.groovy

  done

done
# to generate Jenkins view
#cp marvel-template/viewtemplate.groovy dynamic-list/viewtemplate.groovy

#Added New loop for UI apps
