package jondruse;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Logger;

public class Test {

    private static int port = 8080;
  private static final Logger LOG = Logger.getLogger(Test.class.getName());


	public static void main(String[] args) throws Exception {
		System.out.println("testValidateAnonymous: " + testValidateAnonymous());
        System.out.println("testValidateUser: " + testValidateAuthenticated("user", "W3lcome!"));
	}

  private static String testValidateAuthenticated(String user, String pass) throws Exception {
    Properties env = new Properties();
    env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");
    env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

    LOG.info("validating initial Context....");
    return validateConnectionInfo("localhost", port, user, pass, env);
  }

  private static String testValidateAnonymous() throws Exception {
    Properties env = new Properties();
    env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
    env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

    LOG.info("validating initial Context....");
    return validateConnectionInfo("localhost", port, null, null, env);
  }

  private static String validateConnectionInfo(String host, Integer port, String username, String password, Properties env) {

    try {
      Context ctx = getInitialContext(host, port, username, password, env);
      NamingEnumeration en = ctx.list("/");
    } catch(javax.naming.CommunicationException ce) {
       if(ce.getCause() instanceof javax.security.sasl.SaslException)
          return "wrong username or password";
       else if(ce.getCause() instanceof java.net.ConnectException)
          return "wrong host or port";
    } catch(NamingException ne) { // some other naming exception
      return ne.getClass().getName() + ": " + ne.getMessage(); 
    }   
    return "valid";
  }

  private static Context getInitialContext(String host, Integer port, String username, String password, Properties env) throws NamingException {
   Properties props = new Properties();
   props.put(Context.INITIAL_CONTEXT_FACTORY,  "org.wildfly.naming.client.WildFlyInitialContextFactory");
   props.put(Context.PROVIDER_URL, String.format("%s://%s:%d", "remote+http", host, port));
   props.putAll(env);
   if(username != null && password != null) {
      props.put(Context.SECURITY_PRINCIPAL, username);
      props.put(Context.SECURITY_CREDENTIALS, password);
    }   
   return new InitialContext(props);
  }
}
