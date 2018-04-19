how to use:

1 - export EAP_HOME to the location of eap server

2 - run configure.sh, it will repare 6 nodes (offset 10-500)

3 - servers could be started, stopped, showed by scripts run*, show*, stop*

4 - in browser open -http://localhost:8080/client-side/ (scenario with -full-ha profile - not working)
http://localhost:8380/client-side/ - standalone profile, working

479 DiscoveryEJBClientInterceptor.doClusterDiscovery

361 DiscoveryEJBClientInterceptor.doAnyDiscovery - projde

kdyz se zavola duClusterDiscovery, tak to najde jiny node a selze

kdyz je afinity cluster: ejb, tak to je clusterem a spadne


WhoAmI from server-side-slave1 returned: WhoAmIBean.whoAmI called on server with port-offset 100
WhoAmI from server-side-slave2 returned: javax.ejb.NoSuchEJBException: EJBCLIENT000079: Unable to discover destination for request for EJB StatelessEJBLocator for "/server-side-slave2/WhoAmIBean", view is interface example.ejb.WhoAmIBeanRemote, affinity is Cluster "ejb"
javax.ejb.NoSuchEJBException: No such EJB: /server-side-slave2/WhoAmIBean @ http-remoting://127.0.0.1:8180

WhoAmI from server-side-slave1 returned: javax.ejb.NoSuchEJBException: EJBCLIENT000079: Unable to discover destination for request for EJB StatelessEJBLocator for "/server-side-slave1/WhoAmIBean", view is interface example.ejb.WhoAmIBeanRemote, affinity is Cluster "ejb"
javax.ejb.NoSuchEJBException: No such EJB: /server-side-slave1/WhoAmIBean @ http-remoting://127.0.0.1:8280

WhoAmI from server-side-slave1 returned: javax.ejb.NoSuchEJBException: EJBCLIENT000079: Unable to discover destination for request for EJB StatelessEJBLocator for "/server-side-slave1/WhoAmIBean", view is interface example.ejb.WhoAmIBeanRemote, affinity is Cluster "ejb"
javax.ejb.NoSuchEJBException: No such EJB: /server-side-slave1/WhoAmIBean @ http-remoting://127.0.0.1:8080

pokud je affinity none
WhoAmI from server-side-slave1 returned: WhoAmIBean.whoAmI called on server with port-offset 100
WhoAmI from server-side-slave2 returned: WhoAmIBean.whoAmI called on server with port-offset 200