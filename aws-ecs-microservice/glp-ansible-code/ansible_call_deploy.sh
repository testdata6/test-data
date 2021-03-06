#!/bin/bash
## Pre-reqs
# - Ansible > 2.0
# - Botocore
# - Boto3
# - python 2. 7

# Get path of ansible-playbook



ansible_code_dir="../../../plp-ansible-code/"

# For Dynamic inventory
export AWS_REGION=us-west-2
echo $AWS_REGION
# **** Only for localhost***
# **** ALL VM level configuration is done via ansible pull *****#

ansible-playbook -i $ansible_code_dir/hosts/ec2.py $ansible_code_dir/site-ecs-create.yml --extra-vars \
"env=dev
region=us-west-2
route53_domain=gl-poc.com
route53_private_domain=dev-internal.com
"

