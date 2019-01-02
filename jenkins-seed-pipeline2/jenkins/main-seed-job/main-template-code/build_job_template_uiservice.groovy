// This file use as template for all UI services
// =================================================

job("PLP-Builder-SERVICENAME") {
    logRotator(-1, 10)
    // Modify Jenkins's slave through generate-DSL_script.sh
    label("JENKINSSLAVELABEL")

  scm {
      git {
      remote {
        // Modify credential through generate-DSL_script.sh
        credentials 'AAAAAAAAAAa'
        url "ssh://git@github.com/plp/GITHUBURL.git"
             }
       branch "PLP-1.1.0"
       extensions {
                 cloneOptions {
                             depth(2)
                             shallow(true)
                             noTags(true)
                             timeout(45)
                              }
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

        shell('mkdir plp-config-data-ui')

        copyArtifacts('PLP-Builder-UI-common_Perf') {
        targetDirectory('plp-config-data-ui')
        flatten()
        optional()
        buildSelector {
         workspace()
        }
        flatten(false)
        optional(false)
        }

        shell('sudo npm install \
            \n sudo npm run build-integration-linux \
            \n curl -L -o ./consul.zip https://releases.hashicorp.com/envconsul/0.6.2/envconsul_0.6.2_linux_amd64.zip \
            \n unzip ./consul.zip')

        shell ('gitIDTag=`echo ${GIT_COMMIT} | cut -c 1-15` \
                \n echo ${gitIDTag}_buildNo${BUILD_NUMBER} > version.txt \
                \naws s3 cp dist/ s3://plp-ui/int/SERVICENAME --recursive')
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
  */
  shell ('set +e \
          \naws cloudfront create-invalidation --distribution-id E1M4EJZX7U0QP2 --paths /int/SERVICENAME/\\*')


  configure {
  it / 'builders' << 'hudson.plugins.sonar.SonarRunnerBuilder' {
            javaOpts ''
            jdk '(Inherit From Job)'
            project 'sonar.properties'
            properties 'sonar.projectKey=PLP-master-SERVICENAME\nsonar.projectName=PLP-SONARPROJECTNAME'
            task ''
            }
          }

  }
  publishers {

        archiveArtifacts('version.txt, envconsul, dist/')
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
