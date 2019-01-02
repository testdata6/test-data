#!/bin/bash

# Checking any update in input file ( app-list-input-withPort.txt )
git diff $(git log -2 | grep "commit" | cut -d " " -f2) | grep app-list-input-withPort.txt && findDiff=1 || findDiff=0

# execute loop when update found in input file
 if [ $findDiff -eq 1 ];then
    touch commitFile_000000
    cat app-list-input-withPort.txt |grep -v '^#'| grep -v ^$ |while read serviceName others
    do
    #aws s3 rm s3://plp-master-data-provision/plp-featureTeam/${serviceName}/v0
    aws s3api put-object --bucket plp-master-data-provision --key plp-featureTeam/${serviceName}/v0/
    aws s3api put-object --bucket plp-master-data-provision --key plp-marvel/${serviceName}/v0/
    aws s3api put-object --bucket plp-master-data-provision --key plp-master/${serviceName}/v0/

    aws s3 cp commitFile_000000 s3://plp-master-data-provision/plp-master/${serviceName}/v0/commitFile_000000
    aws s3 cp commitFile_000000 s3://plp-master-data-provision/plp-marvel/${serviceName}/v0/commitFile_000000
    aws s3 cp commitFile_000000 s3://plp-master-data-provision/plp-featureTeam/${serviceName}/v0/commitFile_000000

   done
 else
   echo "NO change in Input file"
 fi
