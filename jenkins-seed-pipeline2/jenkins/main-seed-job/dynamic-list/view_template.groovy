listView('AWS-Dev-Build')
    {
    description('All Dev-Build')
    jobs {

       name('AWS-job build')
       regex(/Dev-mongo-builder-.*/)
    }

       columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
    }



 listView('AWS-QA-Deploy')
                 {
                 description('QA Deploy Jobs')
                jobs {

                    name('QA Deploy Jobs')
                    regex(/QA-kube-deployer.*/)
              //      regex(/QA-ECS-deployer.*/)
                       }

                      columns {
                          status()
                          weather()
                          name()
                          lastSuccess()
                          lastFailure()
                          lastDuration()
                          buildButton()
                           }
                       }

listView('AWS-Integration-Deploy')
                {
                description('Integration Deploy Jobs')
               jobs {

                   name('Dev Demo Deploy Jobs')
                   regex(/Int-ECS-deployer.*/)
                      }

                     columns {
                         status()
                         weather()
                         name()
                         lastSuccess()
                         lastFailure()
                         lastDuration()
                         buildButton()
                          }
                      }

  listView('AWS-Integration-Build')
    {
     description('Build jobs ')
      jobs {

         name('Common build job for AWS and Size')
         regex(/PLP-Builder.*/)
            }

          columns {
             status()
             weather()
             name()
             lastSuccess()
             lastFailure()
             lastDuration()
             buildButton()
              }
          }


  listView('PR-Builds')
    {
      description('PR build jobs')
      jobs {

          name('PR build jobs')
           regex(/PR-build.*/)
          }

         columns {
             status()
             weather()
             name()
             lastSuccess()
             lastFailure()
             lastDuration()
             buildButton()
              }
          }
  listView('AWS-Build-UI')
      {
      description('AWS Build jobs for UI')
      jobs {

          name('PR build jobs')
           regex(/PLP-Builder.*.ui/)
          }

         columns {
             status()
             weather()
             name()
             lastSuccess()
             lastFailure()
             lastDuration()
             buildButton()
              }
          }

    listView('RLP-EXT-Builds')
      {
        description('RLP-EXT build jobs')
        jobs {

            name('RLP-EXT build jobs')
             regex(/PLP-RLP-Ext-Builder.*/)
            }

           columns {
               status()
               weather()
               name()
               lastSuccess()
               lastFailure()
               lastDuration()
               buildButton()
                }
            }

    listView('UAT-Deploy')
      {
        description('UAT deploy jobs')
        jobs {

            name('UAT deploy jobs')
             regex(/UAT-kube-deployer.*/)
            }

           columns {
               status()
               weather()
               name()
               lastSuccess()
               lastFailure()
               lastDuration()
               buildButton()
                }
            }



  listView('Kube-QA-Deploy')
    {
      description('QA kube deploy jobs')
      jobs {

          name('QA deploy jobs')
           regex(/QA-kube-deployer.*/)
          }

         columns {
             status()
             weather()
             name()
             lastSuccess()
             lastFailure()
             lastDuration()
             buildButton()
              }
          }


  listView('BlackDuck')
    {
      description('BlackDuck jobs')
      jobs {

          name('BlackDuck jobs')
           regex(/BlackDuck-Scan.*/)
          }

         columns {
             status()
             weather()
             name()
             lastSuccess()
             lastFailure()
             lastDuration()
             buildButton()
              }
          }


    listView('PERF-Deploy')
      {
        description('PERF deploy jobs')
        jobs {

            name('PERF deploy jobs')
             regex(/PERF-kube-deployer.*/)
            }

           columns {
               status()
               weather()
               name()
               lastSuccess()
               lastFailure()
               lastDuration()
               buildButton()
                }
            }

    listView('PERF-Build-Jobs')
      {
        description('PERF Build jobs')
        jobs {

            name('PERF Build jobs')
             regex(/PLP-PERF-Builder.*/)
            }

           columns {
               status()
               weather()
               name()
               lastSuccess()
               lastFailure()
               lastDuration()
               buildButton()
                }
            }

  listView('Kube-Stage-Jobs')
    {
      description('Kube-Stage-Jobs')
      jobs {

          name('Kube-Stage-Jobs')
           regex(/STG-kube-deployer.*/)
          }

         columns {
             status()
             weather()
             name()
             lastSuccess()
             lastFailure()
             lastDuration()
             buildButton()
              }
          }



              listView('Marvel-Build-Jobs')
              {
                description('Marvel-Build-Jobs')
                jobs {

                    name('Marvel-Build-Jobs')
                     regex(/PLP-Marvel-Builder-.*/)
                    }

                   columns {
                       status()
                       weather()
                       name()
                       lastSuccess()
                       lastFailure()
                       lastDuration()
                       buildButton()
                        }
                    }
                    listView('Marvel-Gradle-Build-Jobs')
                      {
                        description('Marvel-Gradle-Build-Jobs')
                        jobs {

                            name('Marvel-Gradle-Build-Jobs')
                             regex(/PLP-Marvel-Gradle-Builder-.*/)
                            }

                           columns {
                               status()
                               weather()
                               name()
                               lastSuccess()
                               lastFailure()
                               lastDuration()
                               buildButton()
                                }
                            }
                    listView('Marvel-Docker-Jobs')
                      {
                        description('Marvel-Docker-Jobs')
                        jobs {

                            name('Marvel-Docker-Jobs')
                             regex(/PLP-Marvel-Docker-Build.*/)
                            }

                           columns {
                               status()
                               weather()
                               name()
                               lastSuccess()
                               lastFailure()
                               lastDuration()
                               buildButton()
                                }
                            }
                            listView('Marvel-Deploy-Jobs')
                              {
                                description('Marvel-Deploy-Jobs')
                                jobs {

                                    name('Marvel-Deploy-Jobs')
                                     regex(/RVL-kube-deployer.*/)
                                    }

                                   columns {
                                       status()
                                       weather()
                                       name()
                                       lastSuccess()
                                       lastFailure()
                                       lastDuration()
                                       buildButton()
                                        }
                                    }





  listView('Docker Build jobs')
    {
      description('Docker Build jobs')
      jobs {

          name('Docker Build jobs')
           regex(/PLP-Docker-Build-.*/)
          }

         columns {
             status()
             weather()
             name()
             lastSuccess()
             lastFailure()
             lastDuration()
             buildButton()
              }
          }
listView('BiteSize-PLP1-NFT-Deploy-Jobs')
  {
    description('BiteSize PLP1-NFT Deploy jobs')
    jobs {

        name('BiteSize PLP1-NFT Deploy jobs')
         regex(/PLP1-NFT-bitesize-.*/)
        }

       columns {
           status()
           weather()
           name()
           lastSuccess()
           lastFailure()
           lastDuration()
           buildButton()
            }
        }
listView('BiteSize-PLP1-UAT-Deploy-Jobs')
  {
    description('BiteSize PLP1-UAT Deploy jobs')
    jobs {

        name('BiteSize PLP1-UAT Deploy jobs')
         regex(/PLP1-UAT-bitesize-.*/)
        }

       columns {
           status()
           weather()
           name()
           lastSuccess()
           lastFailure()
           lastDuration()
           buildButton()
            }
        }
listView('BiteSize-PLP1-PRD-Deploy-Jobs')
  {
    description('BiteSize PLP1-PRD Deploy jobs')
    jobs {

        name('BiteSize PLP1-PRD Deploy jobs')
         regex(/PLP1-PRD-bitesize-.*/)
        }

       columns {
           status()
           weather()
           name()
           lastSuccess()
           lastFailure()
           lastDuration()
           buildButton()
            }
        }


listView('Marvel-QA-Deploy-Jobs')
  {
    description('Marvel-QA-Deploy-Jobs')
    jobs {

        name('Marvel-QA-Deploy-Jobs')
         regex(/RVL-kube-qa-deployer-.*/)
        }

       columns {
           status()
           weather()
           name()
           lastSuccess()
           lastFailure()
           lastDuration()
           buildButton()
            }
        }



listView('BiteSize-MARVEL-PLP2-QA-Deploy-Jobs')
        {
                description('BiteSize-PLP2-QA-Deploy-Jobs')
                jobs {
                        name('PLP2-QA-bitesize-deployer jobs')
                        regex(/PLP2-QA-bitesize-deployer-.*/)
                }
                columns {
                         status()
                         weather()
                         name()
                        lastSuccess()
                        lastFailure()
                        lastDuration()
                        buildButton()
                }
      }
