// This file creates builder Jobs.
// =================================================
job("PLP-Marvel-Docker-Build-SERVICENAME") {
    logRotator(-1, 10)
    // Modify Jenkins's slave through generate-DSL_script.sh
    label("JENKINSSLAVELABEL")

    scm {
        git {
        remote {
          // Modify credential through generate-DSL_script.sh
	  credentials 'AAAAAAAAAAAAA'
          url "ssh://git@github.com/plpv2/GITHUBURL.git"
               }
         branch "master"
         extensions {
                   wipeOutWorkspace()
                   cloneOptions {
                               depth(1)
                               shallow(true)
                               noTags(true)
                               timeout(45)
                                }
                     }


             }
        }


  	  wrappers {
        preBuildCleanup()
        timestamps()
           }

    steps {
    //    shell ('sleep 10')
        copyArtifacts('PLP-Marvel-Gradle-Builder-SERVICENAME') {
            includePatterns('*exec.jar', 'version.txt', 'dist/', 'envconsul', 'index.html')
			  excludePatterns()
            targetDirectory()
            buildSelector {
                latestSuccessful(true)
            }
        }
    }

  steps {
        configure {
            it / 'builders' << 'org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater' {
              buildName 'version.txt'
              fromFile 'true'
                       }
                  }


    dockerBuildAndPublish {
            repositoryName("plp2-SERVICENAME")
            tag('${BUILD_DISPLAY_NAME}')
            dockerRegistryURL("https://939393939339.dkr.ecr.us-east-1.amazonaws.com/")
            createFingerprints(true)
            forceTag(false)
            forcePull(false)
            skipDecorate(true)
            }

    shell ('set +e \
            \n# docker start f5082dd30b50 \
            \n docker image tag 939393939339.dkr.ecr.us-east-1.amazonaws.com/plp2-SERVICENAME:${BUILD_DISPLAY_NAME} \
            localhost:5000/plp2/SERVICENAME:${BUILD_DISPLAY_NAME} \
            \n docker push localhost:5000/plp2/SERVICENAME:${BUILD_DISPLAY_NAME}')


       downstreamParameterized {
        trigger("RVL-kube-deployer-SERVICENAME") {
              parameters {
                    predefinedProp('buildVersion', '${BUILD_DISPLAY_NAME}')
				                 }
				                                            }
				                       }


   }

   publishers {
      archiveArtifacts('version.txt')
              }
}
