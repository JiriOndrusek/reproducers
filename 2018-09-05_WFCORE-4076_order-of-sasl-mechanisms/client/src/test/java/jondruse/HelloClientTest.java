package jondruse;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.Context;

import jboss.example.ejb.GoodBye;
import jboss.example.ejb.Hello;

import org.junit.Assert;
import org.junit.Test;



public class HelloClientTest
{

    @Test
    public void testAnonymousAccessInPlanAnonymous() throws Exception {
        testEJBClient("8180");
    }


    @Test
    public void testAnonymousAccessInAnonymousPlain() throws Exception {
        testEJBClient("8080");
    }

    @Test
    public void testAuthenticatedAccessInPlanAnonymous() throws Exception {
        testSecuredEJBClient("8180");
    }


    @Test
    public void testAuthenticatedAccessInAnonymousPlain() throws Exception {
        testSecuredEJBClient("8080");
    }

    private void testEJBClient(String port) throws Exception {
        System.out.println("*** Anonymous EJB Client API ***");
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        env.put(Context.PROVIDER_URL, "remote+http://localhost:" + port);

        InitialContext ctx = new InitialContext(env);

        Object obj = ctx.lookup("ejb:/server/Hello!jboss.example.ejb.Hello");

        Hello ejbObject = (Hello) obj;
        System.out.println(ejbObject.sayHello());

        System.out.println("*** Anonymous EJB Client API ***");
    }

    private void testSecuredEJBClient(String port) throws Exception
    {

        System.out.println("*** Secured EJB Client API ***");


        java.util.Properties p = new java.util.Properties();

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        env.put(Context.PROVIDER_URL, "remote+http://localhost:"+port);
        env.put(Context.SECURITY_PRINCIPAL, "admin");
        env.put(Context.SECURITY_CREDENTIALS, "admin");

        InitialContext ctx = new InitialContext(env);

        Object obj = ctx.lookup("ejb:/server/GoodBye!jboss.example.ejb.GoodBye");

        GoodBye goodByeObject = (GoodBye) obj;
        System.out.println(goodByeObject.sayGoodBye());

        System.out.println("*** Secured EJB Client API ***");
    }
}
