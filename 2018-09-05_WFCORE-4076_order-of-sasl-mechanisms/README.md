Reproducer to https://issues.jboss.org/browse/WFCORE-4076

Configuration contains 2 servers:
    - "server" - with configuration of sasl mechanisms as "ANONYMOUS,PLAIN"
    - "reverse"  - with configuration of sasl mechanisms as "PLAIN,ANONYMOUS"

Validation of error is covered in test (HelloClientTest) from module client - there should be no error.


how to run:

- in scripts folder, change init.sh to contain path to unzipped folder
- in scripts folder, execute prepare.sh (configures and starts both servers)

how to test:
- in folder client execute "mvn test"

how to clear:
- in scripts folder run "delete.sh"