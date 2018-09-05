How to execute:

- change paths defined in scripts/init.sh
    EAP_HOME path to unzipped folder with wfly server (example: /home/jondruse/work/2018-08-28_JBEAP-10751_error-response-Security/wfly/01)
    JMETER path to bin folder in jmeter instalation (example: /home/jondruse/dev/apache-jmeter-4.0/bin)
    TMP_FOLDER= path to folder, where results from test will be saved (example /home/jondruse/work/2018-08-28_JBEAP-10751_error-response-Security/testResults)


- build RemotreEjbClint and copy final jar + all dependencies (from RemoteEjbClient/target/dependency-jars) into jmeter/lib/junit

- /jmeter/jmeter.jmx contains test project for jmeter - currently it defines 2000 clients executing ejb remote calls to node on port 8380 - forwarding node on cluster
  (it is only execution of EjbSBProcessorTest)

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------------------------

automatic testing

execution: in/scripts/ execute scenario.sh <folder name>  (you have to confirm to delete all configuration at start with 'y')
results after tests will be placed in $ TMP_FOLDER/<folder name>

----------------------------------------------------------------------------------------------------------------------------------------------------------------

manual testing

cd to /scripts

- execute ./prepare.sh -> this will start cluster (containing node p18 and p19, second cluster with p20)
 (you can use commands like ./stop.sh <node>, ./kill.sh <node>, ./run.sh <node>, ./show.sh <node>)

    - cluster is started after execution of prepare.sh and clusterbench is deployed to nodes

    - you can execte client test for validation EjbSBProcessorTest


- jmeter test (2000 clients) is executed via command ./client.sh <folder name>


    - during execution you can use stop, run, ... command to create failover



