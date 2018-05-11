package jondruse.JBEAP_133375;

import jondruse.stateless.HiBeanRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class ElytronClientNoAnonymous {

    public static void main(String[] args) throws Exception {

        final Hashtable jndiProperties = new Hashtable();

        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "remote+http://localhost:8180");
        final Context context = new InitialContext(jndiProperties);

        final HiBeanRemote hiBean = (HiBeanRemote) context.lookup("ejb:/server/HiBean!"
                + HiBeanRemote.class.getName());

        System.out.println("Obtained a remote stateless hi bean for invocation");
        System.out.println(hiBean.sayHi());

//        ElytronBeanRemote reference = (ElytronBeanRemote) context.lookup("ejb:/ejb-remote-server/SecuredEJB!"
//                + ElytronBeanRemote.class.getName());
//
//        System.out.println("Successfully called secured bean, caller principal " + reference.sayHi());
//        boolean hasFullPermission = false;
//        try {
//            hasFullPermission = reference.secured();
//        } catch (EJBAccessException e) {
//        }
//        System.out.println("\nPrincipal has admin permission: " + hasFullPermission);

    }
}