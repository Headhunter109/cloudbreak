base:
  '*':
    - ambari.repo
    - ambari.server
    - nodes.hosts
    - discovery.init
    - recipes.init

  'roles:gateway':
    - match: grain
    - gateway.init

  'roles:kerberos_server':
    - match: grain
    - kerberos.init

  'roles:ambari_server':
    - match: grain
    - ambari.database
    - ambari.credentials
    - prometheus.server

  'roles:knox_gateway':
    - match: grain
    - ldap.init