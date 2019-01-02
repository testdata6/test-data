// This file creates builder Jobs.
// =================================================

job("PLP-Marvel-Builder-SERVICENAME") {
parameters {
stringParam('dependency_version', '0.35.5', 'This version is comming from super POM')
           }
    logRotator(-1, 20)
    // Modify Jenkins's slave through generate-DSL_script.sh
    label("JENKINSSLAVELABEL")

    scm {
      git {
      remote {
        credentials 'AAAAAAAa'
        url "ssh://git@github.com/plp/GITHUBURL.git"
             }
       branch "marvel-master"
       extensions {
                 wipeOutWorkspace()
                  }

          }
        }


    triggers {
       configure {
        it / 'triggers' << 'com.cloudbees.jenkins.GitHubPushTrigger'{
        spec''
                   }
   scm('')
                 }
             }

    wrappers {
        preBuildCleanup()
        timestamps()
             }
    steps {

        shell ('set +e \
              \nfilename=git-full-stat.log; html_file=git-log-result.html\
              \nrm -f $html_file temp-html; rm -rf file1 file2 file3 git-log-result.html;\
              \necho "<html>\
              \n<head>\
              \n<br>\
              \n<h3><mark>Error: Git is not working </mark></h3>\
              \n<br>\
              \n</body>\
              \n</html>" > git-error\
              \n\
              \ngit log > output.txt; gitstat=$? ; cat output.txt|head -n 25|sed "/^$/d" > $filename; if [ "0" == "$gitstat" ];then echo Git log is working; else echo $gitstat; cp git-error $html_file;exit 0;fi;\
              \n\
              \nsed -i "s|commit|Commit_ID|g; s|Author:|Commit_UserName|g; s|Date:|Commit_Date|g; s|Merge:|Merge_ID|g" $filename\
              \nfor i in $(grep -n "Commit_Date" $filename|cut -d ":" -f 1);do i=$(echo $(($i + 1)));sed -i "$i s/^/User_Comment/" $filename;done\
              \nsed -i "/Commit_ID/iline" $filename ; echo "line" >> $filename\
              \nnum=($(grep -nx line git-full-stat.log|grep -xv 1:line|cut -d ":" -f 1) ) ; template1=$(echo ${num[0]})\
              \nhead -n  ${num[0]} $filename > file1 ; template2=$(echo $(( ${num[1]} - ${num[0]} )) ) ; echo "${num[1]}"\
              \nif [[ -z "${num[1]}" ]];then echo ;else head -n  ${num[1]} $filename|tail -n $template2 > file2;fi\
              \ntemplate3=$(echo $(( ${num[2]} - ${num[1]} )) )\
              \nif [[ -z "${num[2]}" ]];then echo ;else head -n  ${num[2]} $filename|tail -n $template3 > file3;fi\
              \necho "${num[2]}"\
              \n\
              \necho "<html>\
              \n<head>\
              \n<style>\
			  \ntable, td{\
			  \n	border: 1px solid black;\
			  \n	border-collapse: collapse;\
			  \n}\
			  \nth {\
			  \n	border-collapse: collapse;\
			  \n	border: 1px solid black;\
			  \n	table-layout: fixed;\
			  \n	width: 160px;\
			  \n}\
			  \nth, td {\
			  \n	padding: 3px;\
			  \n	text-align: left;\
			  \n}\
              \n</style>\
              \n</head>\
              \n<body>\
              \n<h2>Last commit details:</h2>" > $html_file\
              \n\
              \necho "<h2> </h2>\
              \n<table style="width:60%">\
              \n  <colgroup>\
              \n    <col style="background-color:DarkGray">\
              \n\
              \n  <tr>\
			  \n	<col style=background-color:AliceBlue>\
              \n    <th>Commit ID:</th>\
              \n    <td>Commit_ID</td>\
              \n  </tr>\
              \n\
              \n  <tr>\
              \n    <th>Merge ID:</th>\
              \n    <td>Merge_ID</td>\
              \n  </tr>\
              \n\
              \n  <tr bgcolor="Gold">\
              \n    <th>Commit UserName:</th>\
              \n    <td>Commit_UserName</td>\
              \n  </tr>\
              \n\
              \n  <tr>\
              \n    <th>Commit Date:</th>\
              \n    <td>Commit_Date</td>\
              \n  </tr>\
              \n\
              \n  <tr>\
              \n    <th>User Comment:</th>\
              \n    <td>User_Comment</td>\
              \n</table>" > temp-html\
              \n\
              \nfor a in  file1 file2 file3; do if [ -f "$a" ]; then cp temp-html temp-html-$a; b=$(cat $a|grep -wc -e Commit_ID -e Merge_ID -e Commit_UserName -e Commit_Date -e User_Comment); if [  5 == "$b" ] || [ 4 == "$b" ];then for i in Commit_ID Merge_ID Commit_UserName Commit_Date User_Comment; do if [ "User_Comment" == "$i" ];then j=$i;comments=($(cat $a|sed "/line/d"|sed "s|<|- |; s|>| |"|grep $i -A 20|sed "s|$i ||"|sed -e "s/^[ \t]*//"|sed "s/$/<br>/"|sed "s/^/ + /g"));sed -i "s#$j#$(echo ${comments[*]}|tr "#" "@")#g" temp-html-$a;else j=$i; k=$(cat $a|sed "/line/d"|sed "s|<|- |; s|>| |"|grep -w $i|sed "s|$i ||"); sed -i "s/$j/$k/g" temp-html-$a; fi; done; cat temp-html-$a >> $html_file; rm -fv temp-html-$a; else  echo; fi;fi;done\
              \n\
              \necho "</body>\
              \n</html>" >> $html_file')

  /*     shell ('wget -O - -o /dev/null  --user ${nexus_user} --password ${nexus_pass} https://my-nexus.com/nexus-deps/content/repositories/plp-releases/com/mongo/platform/common/maven-metadata.xml | grep -Po \'(?<=<version>)([0-9\\.]+(-SNAPSHOT)?)\' | sort --version-sort -r | head -n 1 > rel.txt \
              \necho ${dependency_version} \
              \nif grep -qw "${dependency_version}" rel.txt;then \
              \necho "good to go with superpom" > log.txt  \
              \nelse \
              \necho "BUILD FAILED: Not latest Version of Platform Common" > log.txt \
              \nfi \
              \ncat log.txt|grep -qw "good to go with superpom" ') */

        maven{
            goals('clean deploy -e -U -Dnexus.repo=https://my-nexus.com -Drel.version=${dependency_version}')
            providedGlobalSettings('b20898ef-a4b8-4342-8804-8ca8b49caa6f')
            }

        shell ('gitIDTag=`echo ${GIT_COMMIT} | cut -c 1-10` \
              \n echo ${gitIDTag}_RVbNo${BUILD_NUMBER}_dv_${dependency_version} > version.txt \
              \ncp target/*exec.jar . ')


    shell ('# Upload serviceMasterData file to s3 \
          \n if [ -f src/main/resources/serviceMasterData ] ; then \
          \n    git diff $(git log -2 | grep "commit" | cut -d " " -f2) | grep serviceMasterData && findDiff=1 || findDiff=0 \
          \n    p=$( aws s3 ls plp-master-data-provision/plp-marvel/SERVICENAME/ | grep "v" | cut -d " " -f 29 | cut -d "/" -f 1 | cut -d "v" -f 2 | sort -r )  && arr=( $p ) && m=${arr[0]} \
          \n    if [ $findDiff -eq 1 ];then  \
          \n      echo "UPdate in s3" \
          \n      echo $m \
          \n      oldcommit=$(aws s3 ls s3://plp-master-data-provision/plp-marvel/SERVICENAME/v${m}/ |grep commitFile_ | awk {\'print $4\'} | cut -d_ -f2) \
          \n      if [ $oldcommit != $GIT_COMMIT ] ;then  \
          \n         let m=m+1 \
          \n         echo "creating new folder $m" \
          \n         aws s3 cp src/main/resources/serviceMasterData s3://plp-master-data-provision/plp-marvel/SERVICENAME/v${m}/serviceMasterData \
          \n         aws s3 cp version.txt s3://plp-master-data-provision/plp-marvel/SERVICENAME/v${m}/commitFile_${GIT_COMMIT} \
          \n         echo $(cat version.txt)_T${m} > version.txt \
          \n      else \
          \n        echo $(cat version.txt)_T${m} > version.txt \
          \n      fi \
          \n   else \
          \n     if [ $m -eq 0 ];then \
          \n       echo "Creation of v1" \
          \n       aws s3 cp src/main/resources/serviceMasterData s3://plp-master-data-provision/plp-marvel/SERVICENAME/v1/serviceMasterData \
          \n       aws s3 cp version.txt s3://plp-master-data-provision/plp-marvel/SERVICENAME/v1/commitFile_${GIT_COMMIT} \
          \n       echo $(cat version.txt)_T1 > version.txt \
          \n     else \
          \n     echo $(cat version.txt)_T${m} > version.txt \
          \n     fi \
          \n   fi \
          \n else \
          \n echo "serviceMasterData does not exist" \
          \n echo $(cat version.txt)_F > version.txt \
          \n fi')
        }



    steps {
        configure {
            it / 'builders' << 'org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater' {
              buildName 'version.txt'
              fromFile 'true'
                       }
                  }
/*
    dockerBuildAndPublish {
            repositoryName("plp-SERVICENAME")
            tag('${BUILD_DISPLAY_NAME}')
            dockerRegistryURL("https://939393939339.dkr.ecr.us-east-1.amazonaws.com/")
            createFingerprints(true)
            forceTag(false)
            forcePull(false)
            skipDecorate(true)
            }

    shell ('set +e \
            \n# docker start f5082dd30b50 \
            \n docker image tag 939393939339.dkr.ecr.us-east-1.amazonaws.com/plp-SERVICENAME:${BUILD_DISPLAY_NAME} \
            localhost:5000/plp/SERVICENAME:${BUILD_DISPLAY_NAME} \
            \n docker push localhost:5000/plp/SERVICENAME:${BUILD_DISPLAY_NAME}')
*/
    configure {                                                        // SONARQUBE_CONFIG
    it / 'builders' << 'hudson.plugins.sonar.SonarRunnerBuilder' {     // SONARQUBE_CONFIG
          javaOpts ''                                                  // SONARQUBE_CONFIG
          jdk '(Inherit From Job)'                                     // SONARQUBE_CONFIG
          project 'config/sonar.properties'                            // SONARQUBE_CONFIG
          properties 'sonar.projectKey=PLP-MARVELl-SERVICENAME\nsonar.projectName=PLP-MARVELl-SONARPROJECTNAME' // SONARQUBE_CONFIG
          task ''                                                      // SONARQUBE_CONFIG
                } }                                                    // SONARQUBE_CONFIG



   }
     publishers {
        archiveArtifacts('version.txt, *exec.jar')
        findbugs('**/findbugs-result.xml', false)
        downstreamParameterized {
          trigger("PLP-Marvel-Docker-Build-SERVICENAME"){
          triggerWithNoParameters()
          }
                                 }
        extendedEmail {
               recipientList('Mongo-project-PLP-ScrumMasters')
               defaultSubject('$DEFAULT_SUBJECT')
               defaultContent('$DEFAULT_CONTENT<br/><br/> \
                                  \nBuild tag:- $BUILD_DISPLAY_NAME<br>\
                                  \n${FILE,path="git-log-result.html"} \
                                   \n<br>Thanks,<br>\
                                   \nTeam DevOps<br>')
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
