# DSL code to create all build and deploy jobs on plp Jenkins:
It's require DSL plugin on jenkins and one job which use this code by DSL plugin.
https://wiki.jenkins.io/display/JENKINS/Job+DSL+Plugin

##### Dependencies

  - DSL Plugin version > 1.68

##### Add/remove/update services/environment
  - Update following file to add/remove/update services:
    jenkins/main-seed-job/app-list-input-withPort.txt

  - Update following file to add/remove/update Feature Team environment:
    jenkins/main-seed-job/fearture-team-list.txt

  - Update following folder to add/remove/update build/deploy jobs:
    jenkins/main-seed-job/main-template-code
============================================================================================
