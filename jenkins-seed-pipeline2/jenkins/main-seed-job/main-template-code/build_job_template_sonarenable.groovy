// This file creates builder Jobs.
// =================================================

job("PLP-Builder-SERVICENAME") {
parameters {
stringParam('dependency_version', '0.35.10', 'This version is comming from super POM')
           }
    logRotator(-1, 5)
    // Modify Jenkins's slave through generate-DSL_script.sh
    label("JENKINSSLAVELABEL")

    scm {
      git {
      remote {
        credentials 'AAAAAAAAAAa'
        url "ssh://git@github.com/plp/GITHUBURL.git"
             }
       branch "PLP-1.1.0"
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
              \n echo ${gitIDTag}_bNo${BUILD_NUMBER}_dv_${dependency_version} > version.txt \
              \ncp target/*exec.jar . ')

          }
steps {
    shell ('# Upload serviceMasterData file to s3 \
          \n file_name=serviceMasterData \
          \n service_file=src/main/resources/${file_name} \
          \n s3_dir=plp-master-data-provision/plp-master/SERVICENAME \
          \n s3_data=s3_local_dir \
          \n  \
          \n if [ -f "$service_file" ] ; then \
          \n echo "File Found $service_file" \
          \n  \
          \n     total_count=$(aws s3 ls ${s3_dir}/ |grep -v "v0"|grep -c "v") \
          \n     echo total_count: $total_count \
          \n  \
          \n     if [ "$total_count" -eq "0" ]; then \
          \n       echo "Creating V1 Directory and uploading file in S3" \
          \n       aws s3 cp ${service_file} s3://${s3_dir}/v1/${file_name} \
          \n       echo $(cat version.txt)_v1 > version.txt \
          \n       exit 0 \
          \n  \
          \n     else \
          \n  \
          \n         latest_no=$(aws s3 ls $s3_dir/ |grep "v"| tr -s " "| cut -d "v" -f 2|tr -d "/"|sort -n|tail -n 1) \
          \n         mkdir -v ${s3_data} \
          \n         aws s3 cp s3://${s3_dir}/v${latest_no}/${file_name}  ${s3_data}/ \
          \n         set +e \
          \n         cmp -s ${s3_data}/${file_name} ${service_file} \
          \n         compare_result=$? \
          \n         set -e \
          \n         if [ "$compare_result" -eq 1 ]; then \
          \n             let new_no=latest_no+1 \
          \n             echo $new_no \
          \n             echo "File content different hence uploading file in S3" \
          \n             aws s3 cp ${service_file} s3://${s3_dir}/v${new_no}/${file_name} \
          \n             echo $(cat version.txt)_v${new_no} > version.txt \
          \n         else \
          \n             echo "File content similar" \
          \n             echo $(cat version.txt)_v${latest_no} > version.txt \
          \n         fi \
          \n     fi \
          \n else \
          \n   echo "${file_name} File Not Found" \
          \n   echo $(cat version.txt)_F > version.txt \
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
          properties 'sonar.projectKey=PLP-master-SERVICENAME\nsonar.projectName=PLP-SONARPROJECTNAME' // SONARQUBE_CONFIG
          task ''                                                      // SONARQUBE_CONFIG
                } }                                                    // SONARQUBE_CONFIG


   }
     publishers {
        archiveArtifacts('version.txt, *exec.jar')
        findbugs('**/findbugs-result.xml', false)
        downstreamParameterized {
          trigger("PLP-Docker-Build-SERVICENAME"){
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
                       recipientList('Mongo-project-PLP-Realsteel@gmail.com')
					   content('$PROJECT_DEFAULT_CONTENT')
                       sendTo {
                           recipientList()
                       }
                   }
                   }

               triggers {
                       fixed {
                       subject('$DEFAULT_SUBJECT')
                       recipientList('Mongo-project-PLP-Realsteel@gmail.com')
					   content('$PROJECT_DEFAULT_CONTENT')
                       sendTo {
                           recipientList()
                       }
                   }
                   }

                      }

                }


}
