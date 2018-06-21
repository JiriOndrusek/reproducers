package client;

import example.ejb.WhoAmIBeanRemote;
import org.jboss.ejb.client.EJBClientConnection;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.protocol.remote.RemoteTransportProvider;
import org.wildfly.naming.client.WildFlyInitialContextFactory;
import org.wildfly.security.WildFlyElytronProvider;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;
import org.wildfly.security.credential.BearerTokenCredential;
import org.wildfly.security.sasl.SaslMechanismSelector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URI;
import java.security.PrivilegedActionException;
import java.security.Provider;
import java.util.Properties;

/**
 * @author jmartisk, olukas
 */
public class Client_server {

    public static void main(String[] args) throws NamingException, PrivilegedActionException {
        AuthenticationConfiguration common = AuthenticationConfiguration.empty()
                .useProviders(() -> new Provider[]{new WildFlyElytronProvider()})
                .useBearerTokenCredential(new BearerTokenCredential("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpc3N1ZXIud2lsZGZseS5vcmciLCJzdWIiOiJlbHl0cm9uQHdpbGRmbHkub3JnIiwiZXhwIjoyMDUxMjIyMzk5LCJhdWQiOiJqd3QifQ.jOcMdBLdI7HMuW_VsoGD_7LqeX6M14_wV5ebP2S4tOM"))
                .setSaslMechanismSelector(SaslMechanismSelector.fromString("OAUTHBEARER"));
        AuthenticationContext authCtxEmpty = AuthenticationContext.empty();
        final AuthenticationContext authCtx = authCtxEmpty.with(MatchRule.ALL, common);

        final EJBClientContext.Builder ejbClientBuilder = new EJBClientContext.Builder();
        ejbClientBuilder.addTransportProvider(new RemoteTransportProvider());
        final EJBClientConnection.Builder connBuilder = new EJBClientConnection.Builder();
        connBuilder.setDestination(URI.create("remote+http://172.18.0.22:8080"));
        ejbClientBuilder.addClientConnection(connBuilder.build());
        final EJBClientContext ejbCtx = ejbClientBuilder.build();

        AuthenticationContext.getContextManager().setThreadDefault(authCtx);
        EJBClientContext.getContextManager().setThreadDefault(ejbCtx);

        InitialContext ctx = new InitialContext(getCtxProperties());
        String lookupName = "ejb:/server/WhoAmIBean!example.ejb.WhoAmIBeanRemote";
        WhoAmIBeanRemote bean = (WhoAmIBeanRemote) ctx.lookup(lookupName);
        System.out.println(bean.whoAmI());
        ctx.close();
    }

    public static Properties getCtxProperties() {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
        return props;
    }

}
