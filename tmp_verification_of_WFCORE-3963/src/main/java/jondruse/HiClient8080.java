package jondruse;

import jondruse.stateless.HiBeanRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class HiClient8080
{

    public static void main(String[] args) throws Exception
    {
        testEJBClient();
    }

    private static void testEJBClient() throws Exception
    {
        System.out.println("*** EJB Client API ***");
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        env.put(Context.SECURITY_PRINCIPAL, "user");
        env.put(Context.SECURITY_CREDENTIALS, "W3lcome!");
        env.put("remote.connections", "default");
        env.put("endpoint.name","client-endpoint");
        env.put("remote.connection.default.port","8080");
        env.put("remote.connection.default.host","localhost");
        env.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED","false");
        env.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT","false");
        env.put("remote.connection.default.protocol","http-remoting");

        InitialContext ctx = new InitialContext(env);

        Object obj = ctx.lookup("ejb:/server/HiBean!"
                + HiBeanRemote.class.getName());

        HiBeanRemote ejbObject = (HiBeanRemote) obj;
        System.out.println(ejbObject.sayHi());

        System.out.println("*** EJB Client API ***");
    }
}

