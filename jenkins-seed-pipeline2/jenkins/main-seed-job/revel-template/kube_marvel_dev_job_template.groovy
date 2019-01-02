// This file creates deployer job for UAT
// NOT READY
// =================================================

job("RVL-kube-deployer-SERVICENAME") {

     parameters {
     stringParam('buildVersion', 'latest', 'Enter "latest" for current latest build from ECR, Enter specific ECR tag while rollback (DO NOT USE "latest" FOR ROLLBACK)')
                }

     logRotator(-1, 10)
     // Modify Jenkins's slave through generate-DSL_script.sh
     label("JENKINSSLAVELABEL")
     authenticationToken('RVLstart')
      wrappers {
        preBuildCleanup()
          }

      steps {

    shell ('# removing double quotes from Build version if any \
    \nbuildVersion=`echo $buildVersion |tr -d \\"` \
    \nrepoName=plp2-SERVICENAME \
    \nif [ "$buildVersion" == "latest" ] \n then \
    \n buildVersion=`aws ecr describe-images --repository-name $repoName --region us-east-1 --output text|grep -C1 latest |grep IMAGETAGS |grep -v latest | cut -f2` \
    \n echo $buildVersion > version.txt \
    \n else \
    \n aws ecr describe-images --repository-name $repoName --region us-east-1 --output text |grep -w $buildVersion && echo $buildVersion is available > report.txt || echo $buildVersion is the Incorrect Version > report.txt  \
    \n if grep Incorrect report.txt \
    \n  then \
    \n  buildVersionPLP=`aws ecr describe-images --repository-name plp-SERVICENAME --region us-east-1 --output text|grep -C1 latest |grep IMAGETAGS |grep -v latest | cut -f2` \
    \n  echo ================================Please find following available latest versions ========================================== \
    \n  echo == Latest version from release branch is = $buildVersionPLP ===================== \
    \n  exit 1 \
    \n fi \
    \nfi \
    \n echo $buildVersion > version.txt \
    \n#adding new Tag on Docker image.  \
    \n# MANIFEST=$(aws ecr batch-get-image --repository-name $repoName --image-ids imageTag=${buildVersion} --region us-east-1 --query images[].imageManifest --output text) \
    \n# aws ecr put-image --repository-name $repoName --region us-east-1 --image-tag RC_${BUILD_NUMBER}_${buildVersion} --image-manifest "$MANIFEST" \
    \n# Deploying desire image. \
    \n#ansible-playbook plp-ansible-code/playbooks/SERVICENAME.yml --extra-vars "env=stage region=us-east-1 ecs_action=update build_no=${buildVersion} ecr_prefix=plp"')
            }
      // Step adding to update build name with time stamp //
      steps {
         configure {
             it / 'builders' << 'org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater' {
             buildName 'version.txt'
             fromFile 'true'
             }
                   }
         downstreamParameterized {
         trigger("Marvel-DEV-Data-Provision-Pipeline") {
//          trigger("STG-Data-Provision-Pipeline") {
              block {
                        buildStepFailure('FAILURE')
                        failure('FAILURE')
                        unstable('UNSTABLE')
                    }
           parameters {
             predefinedProp('IMAGE_VERSION', '${BUILD_DISPLAY_NAME}')
             predefinedProp('SERVICE_NAME', 'SERVICENAME')
             predefinedProp('NAMESPACE', 'plp-dev')
             predefinedProp('PORT', 'SERVICEPORT')
             predefinedProp('CB_CLUSTER', 'COUCHBASECLUSTER')
             predefinedProp('CPU_LIMIT', 'PODCPU_LIMIT')
             predefinedProp('CPU_REQUEST', 'PODCPU_REQUEST')
             predefinedProp('MEMORY_LIMIT', '2G')
             predefinedProp('MEMORY_REQUEST', '2G')
             predefinedProp('REPLICA_COUNT', '1')
                                }
                   }

                  }
  /*      downstreamParameterized {
        trigger("UAT-bitesize-deployer-SERVICENAME, NFT-bitesize-deployer-SERVICENAME") {
          parameters {
                predefinedProp('buildVersion', '${BUILD_DISPLAY_NAME}')
                     }
                  }

                 }  */

            }
            publishers {
              extendedEmail {
                     recipientList('Mongo-project-PLP-ScrumMasters, Mongo-project-PLP-QA')
                     defaultSubject('$DEFAULT_SUBJECT')
                     defaultContent('$DEFAULT_CONTENT \nBuild tag:- $BUILD_DISPLAY_NAME')
                     contentType('text/html')
                     triggers {
                             always {
                             subject('$DEFAULT_SUBJECT')
                   content('$PROJECT_DEFAULT_CONTENT')
                             sendTo {
                                 developers()
                                 requester()
                                 culprits()
                     recipientList()
                             }
                         }
                         }

                     triggers {
                             failure {
                             subject('$DEFAULT_SUBJECT')
                             recipientList('notfication_user@gmail.com@gmail.com')
                   content('$PROJECT_DEFAULT_CONTENT')
                             sendTo {
                                 recipientList()
                             }
                         }
                         }

                     triggers {
                             fixed {
                             subject('$DEFAULT_SUBJECT')
                             recipientList('notfication_user@gmail.com@gmail.com')
                   content('$PROJECT_DEFAULT_CONTENT')
                             sendTo {
                                 recipientList()
                             }
                         }
                         }

                            }
                       }



}
