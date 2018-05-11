package jondruse.JBEAP_133375;

import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.Context;

import jondruse.stateless.HiBeanRemote;
import org.jboss.ejb.client.EJBClientContext;

public class HiClient8080
{

    public static void main(String[] args) throws Exception
    {
//        PropertyConfigurator.configure("log4j.properties");

        testEJBClient();
    }

    private static void testEJBClient() throws Exception
    {
        System.out.println("*** EJB Client API ***");
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");

        InitialContext ctx = new InitialContext(env);

        Object obj = ctx.lookup("ejb:/server/HiBean!"
                + HiBeanRemote.class.getName());

        HiBeanRemote ejbObject = (HiBeanRemote) obj;
        System.out.println(ejbObject.sayHi());

        System.out.println("*** EJB Client API ***");
    }
}

