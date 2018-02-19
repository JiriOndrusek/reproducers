package jondruse.JBEAP_133375;

import jondruse.EjbJvmRouteInterface;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class Client {

    public final static int TIMES = 10;


    public static void main(String[] args) throws NamingException {
        final String ADDR = System.getProperty("remote.server.address", "127.0.0.1:8180");
        final String PATH = System.getProperty("remote.endpoint.path", "/wildfly-services");
        final String URL = "http://" + ADDR + PATH;
        InitialContext iniCtx = new InitialContext(getCtxProperties(URL));
        String lookupName = "ejb:/JBEAP-13375-server-side-1.0.0-SNAPSHOT/StatelessJvmRoute!jondruse.EjbJvmRouteInterface";
//        lookupName = "java:app/JBEAP-13375-server-side-1.0.0-SNAPSHOT/StatefulJvmRoute";/
        EjbJvmRouteInterface bean = (EjbJvmRouteInterface)iniCtx.lookup(lookupName);
        try {
            for (int i = 0; i < TIMES; i++) {
                System.out.print(bean.getJvmroute() + ";");
            }
        } finally {
            iniCtx.close();
        }
    }

    public static Properties getCtxProperties(String URL) {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
//        props.put(Context.PROVIDER_URL, URL);
        props.put(Context.SECURITY_CREDENTIALS,"w3lcome!");

////        jndiProperties.put("remote.connection.main.port", Integer.toString(8180));

        props.put(Context.SECURITY_PRINCIPAL, "user");
//        props.put("remote.clusters", "ejb");

//        props.put("remote.connection.main.port", 8180);
//        props.put("remote.connection.main.host", "127.0.0.1");
//                props.put(Context.PROVIDER_URL,"http://localhost:8180");
                props.put(Context.PROVIDER_URL, URL);
        return props;
    }
}
