package jondruse.smartFrog;

import org.jboss.ejb.client.EJBClient;
import org.jboss.test.clusterbench.ejb.stateful.RemoteStatefulSB;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of StatefulSBProcessorFactory invoking a SFSB.
 *
 * readsAfterWrites allows to specify how many times the session should be only
 * read, without writing/modifying it thus triggering replication.
 *
 * @author Radoslav Husar
 * @author Jiri Ondrusek
 * @version Aug 2012
 */
public class StatefulSBProcessorFactoryImpl {

    private File wildflyConfig;

    //private String providerURL;
    private boolean writeDebugLog = true;
    private int maxAge;
    private boolean clustered;
    private boolean resetSessionOnError = true;
    private boolean syncOnError;
    private int readsAfterWrites;
    private int errorCountTreshold = 0;
    private int invocationTimeout;
    private boolean partitionHandling;
    public String port;
    public String ejb;


    private class EJB3RequestProcessor  {

        private RemoteStatefulSB session = null;
        private int serial;
        private int readOperationsDone = 0;

        private int errorCount = 0;

        public EJB3RequestProcessor() {
        }

        public void processRequest(boolean write) throws Exception {

            if (session == null) {
//                if (writeDebugLog) {
//                    System.out.println("Creating SFSB.");
//                }

                initSession();
            }

            int gotSerial = 0;

            long time = System.currentTimeMillis();
            try {
//                if (readOperationsDone == readsAfterWrites) {
                if(write) {
                    readOperationsDone = 0;

                    if (writeDebugLog) {
                        System.out.println("Invoking SFSB: getSerialAndIncrement().");
                    }
                    gotSerial = session.getSerialAndIncrement();
                } else {
                    readOperationsDone++;
                    if (writeDebugLog) {
                        System.out.println("Invoking SFSB: getSerial().");
                    }
                    gotSerial = session.getSerial();
                }

                // we've had a valid run, start counting from 0 again
                errorCount = 0;
            } catch (Exception e) {
                if (partitionHandling) {
                    throw new Exception("Network partition? Could not get valid response. Runner: "  + ".", e);
                }

                System.out.println("ERROR"+"Error getting response.");
                errorCount++;

                // So that we get only one error when failover fails.
                if (resetSessionOnError && errorCount >= errorCountTreshold) {
                    System.out.println("Reached treshold of " + errorCountTreshold + " errors, resetting session.");
                    session = null;
                    serial = 0;
                    readOperationsDone = 0;
                    errorCount = 0;
                } else if (resetSessionOnError) {
                    System.out.println("Caught error, we're under error treshold, current error count is " + errorCount);
                }

                throw new Exception("Could not get valid response. Runner: " + ".", e);
            }
            int delay = (int) (System.currentTimeMillis() - time);

            if (writeDebugLog) {
                System.out.println("Response acquired, serial: " + String.valueOf(gotSerial) + " delay: " + delay);
            }

            if (gotSerial != serial) {
                if ((gotSerial < serial) && ((serial - gotSerial) <= maxAge)) {
                    System.out.println("WARN"+"Response serial does not match. Expected: " + serial + ", received: " + gotSerial + ", runner: " + "runnerId" + ".");
                } else {
                    if (syncOnError) {
                        int beforeSyncSerial = serial;
                        serial = gotSerial + 1;
                        System.out.println("WARN"+"Synced next serial to " + serial + " from current " + beforeSyncSerial + ".");
                        throw new Exception("Response serial does not match. Expected: " + beforeSyncSerial + ", received: " + gotSerial + ", runner: " + "runnerId" + ".");
                    } else {
                        throw new Exception("Response serial does not match. Expected: " + serial + ", received: " + gotSerial + ", runner: " + "runnerId" + ".");
                    }
                }
            }

            if (readOperationsDone == 0) {
                // We were writing in the last operation, so expect next one to be incremented:
                serial++;
            }

//            Response ret;
//
//            if (clustered) {
//                ClusteredResponseImpl cret = new ClusteredResponseImpl(0, delay);
//                ret = cret;
//                cret.setNode("unknown-node");
//            } else {
//                ret = new ClusteredResponseImpl(0, delay);
//            }
//
//            return ret;
        }

        /**
         * Looks up and returns the proxy to remote SFSB.
         *
         * @throws NamingException
         */
        @SuppressWarnings("unchecked")
        private void initSession() throws Exception {
            if (wildflyConfig != null) {
                try {
                    System.setProperty("wildfly.config.url", wildflyConfig.getAbsolutePath());
                    if (writeDebugLog) {
                        System.out.println("Setting system property wildfly.config.url to: " +  wildflyConfig.getAbsolutePath());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (writeDebugLog) {
//                    System.out.println("wildfly-config temporary file has not been created. Setting configuration path to /home/jondruse/git/projects/reproducers/2018-08-28_JBEAP-10751_error-response-Security-Exception/EjbClient/src/main/resources/wildfly-config"+port+".xml");
                }
                //lets assume that there is a custom wildfly-config.xml prepared
                System.setProperty("wildfly.config.url", this.getClass().getResource("/wildfly-config.xml").getPath());
            }

            try {
                Hashtable jndiProperties = new Hashtable();
                jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());

                Context context = new InitialContext(jndiProperties);
                // The app name is the application name of the deployed EJBs. This is typically the ear name
                // without the .ear suffix. However, the application name could be overridden in the application.xml of the
                // EJB deployment on the server.
                // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
                // String appName = "";
                // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
                // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
                // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
                // jboss-as-ejb-remote-app
                // String moduleName = "jboss-as-ejb-remote-app";
                // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
                // our EJB deployment, so this is an empty string
                // Lets not use this --rhusar
                // String distinctName = "";
                // The EJB name which by default is the simple class name of the bean implementation class
//                String beanName = RemoteStatefulSBImpl.class.getSimpleName();
//                String beanName = "ejb:clusterbench-ee7/clusterbench-ee7-ejb/ForwardingStatefulSBImpl";
                String beanName = "ejb:clusterbench-ee7/clusterbench-ee7-ejb/"+ejb;

                // the remote view fully qualified class name
                String viewClassName = RemoteStatefulSB.class.getName();

                // let's do the lookup (notice the ?stateful string as the last part of the jndi name for stateful bean lookup)

                session = (RemoteStatefulSB) context.lookup(beanName + "!" + viewClassName + "?stateful");
                if (invocationTimeout > 0) {
                    //EJB Client internal API usage, should not collide with the old EJB client v2.x + v3.x as they use StatefulSBProcessorFactoryImplLegacyClient
                    System.out.println("Setting the invocation timeout for EJB client proxy to: " + invocationTimeout + " seconds");
                    EJBClient.setInvocationTimeout(session, invocationTimeout, TimeUnit.SECONDS);
                }

                // TODO: Will we need this?
                // session.initialize(String.valueOf(runnerId));
                // if (writeDebugLog) {
                //    System.out.println("New SFSB created.");
                // }
            } catch (NamingException ne) {
                System.out.println("ERROR"+"Lookup failed: " + ne);
                throw new Exception("Could not lookup session bean.", ne);
            }
        }
    }

    public StatefulSBProcessorFactoryImpl() throws RemoteException {
        // Required by Remote.
    }
//
//    @Override
//    public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
//        super.sfDeploy();
//
//        try {
//            wildflyConfig = (TempFile) sfResolve(ATTR_WILDFLY_CONFIG, (Object) null, false);
//
//            // EJB Lookup
//            appName = sfResolve(ATTR_APP_NAME, "", true);
//            moduleName = sfResolve(ATTR_MODULE_NAME, "", true);
//            distinctName = sfResolve(ATTR_DISTINCT_NAME, "", false);
//            beanName = sfResolve(ATTR_BEAN_NAME, "", false);
//
//            readsAfterWrites = sfResolve(ATTR_READS_AFTER_WRITES, 0, false);
//
//            // Properties
////         providerURL = sfResolve(ATTR_PROVIDER_URL, (String) null, false);
////
////         if (providerURL == null) {
////            Vector servers = (Vector) sfResolve(ATTR_SERVERS);
////            Integer port = (Integer) sfResolve(ATTR_PORT, 1100, false);
////            StringBuilder sb = new StringBuilder();
////            for (Object s : servers) {
////               sb.append(s.toString());
////               sb.append(":");
////               sb.append(port);
////               sb.append(",");
////            }
////            providerURL = sb.substring(0, sb.length() - 1);
////         }
//
//            //jndiName = (String) sfResolve(ATTR_JNDI_NAME);
//
//            // SF Request processor
//            writeDebugLog = (Boolean) sfResolve(ATTR_WRITE_DEBUG_LOG, Boolean.TRUE, false);
//            resetSessionOnError = (Boolean) sfResolve(ATTR_RESET_SESSION, Boolean.FALSE, false);
//            maxAge = (Integer) sfResolve(ATTR_MAX_AGE, 0, false);
//            clustered = (Boolean) sfResolve(ATTR_CLUSTERED, Boolean.FALSE, false);
//            // Default should be maybe true, but for backwards compatibility set to default.
//            syncOnError = sfResolve(ATTR_SYNC_ON_ERROR, false, false);
//            partitionHandling = sfResolve(ATTR_PARTITION_HANDLING, false, false);
//            errorCountTreshold = (Integer) sfResolve(ATTR_ERROR_TRESHOLD, new Integer(1), false);
//            invocationTimeout = (Integer) sfResolve(ATTR_INVOCATION_TIMEOUT, new Integer(0), false);
//        } catch (Exception e) {
//            throw new SmartFrogException("Could not get provider URL.", e);
//        }
//    }
//
//    //   private InitialContext getInitialContext() throws NamingException {
//    //      Properties p = new Properties();
//    //      p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
//    //      p.put(Context.URL_PKG_PREFIXES, "jboss.naming:org.jnp.interfaces");
//    //      p.put(Context.PROVIDER_URL, providerURL); // HA-JNDI port.
//    //      return new InitialContext(p);
//    //   }
//    @Override
    public EJB3RequestProcessor getProcessor() throws RemoteException {
        return new EJB3RequestProcessor();
    }



    public static void test(String port, String ejb, int count, boolean write) throws Exception {
        StatefulSBProcessorFactoryImpl factory = new StatefulSBProcessorFactoryImpl();

        factory.port = port;
        factory.ejb = ejb;
        EJB3RequestProcessor processor = factory.getProcessor();

        if (count < 0) {
            while(true) {
                processor.processRequest(write);
            }
        } else {

            for (int i = 0; i < count; i++) {
                processor.processRequest(write);
            }
        }
    }
}
