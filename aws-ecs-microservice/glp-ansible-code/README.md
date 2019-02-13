# Ansible PLP

  - Ansible roles for configuring PLP AWS Stack
  - Stack has been deployed via Terraform
  - Post stack deployment, we call Ansible scripts

##### Dependencies

  - AWS Stack deployed
  - Ansible.cfg should exist
  - Dynamic inventory setup
  - Boto
  - Boto3
  - Python > 2.7
  - Ansible. Obviously!!!

### Setup

Ansible for PLP requires you set up your environment before executing the playbooks.

#### Note:
   - ssh config is generated via terraform automatically
   - Ansible configuration needs to be configured before triggering
     * ANSIBLE_CONFIG (an environment variable)
     * ansible.cfg (in the current directory)
     * .ansible.cfg (in the home directory)
     * /etc/ansible/ansible.cfg


```sh
$ export ANSIBLE_HOSTS={path_to_ansible_dynamic_inventory_script}
$ export EC2_INI_PATH={path_to_ansible_dynamic_inventory_script_config.ini}
$ python ${inventory_path}/ec2.py --list # ensure no errors
# Load ssh-keys
eval `ssh-agent`
ssh-add {your_private_key} # ssh ~/.ssh/my-key.pem
ssh-add -L # list the keys for verification
# Test ping
ansible -m ping tag_Role_${your-tags}
```

### Todos
 - Automate keys loading but ensure keys are not exposed
 - Test cases
