package jboss.example.ejb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
//import org.jboss.annotation.ejb.PoolClass;
//import org.jboss.annotation.ejb.RemoteBinding;

// Security related imports
import javax.annotation.security.RolesAllowed;
import org.jboss.ejb3.annotation.SecurityDomain;
import java.security.Principal;

//@RolesAllowed({})
@Stateless(name="Hello")
@Remote(Hello.class)
//@Local(Hello.class)
//@RemoteBinding(jndiBinding = "hello/remote")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//@PoolClass(value=org.jboss.ejb3.StrictMaxPool.class, maxSize=1000,
//timeout=10000)
public class HelloBean implements Hello
{
   @Resource
   private SessionContext context;

//    @RolesAllowed("JBossAdmin")
    public String sayHello()
    {
       System.out.println("*** Entering Hello Bean!");
       // String name = (String) context.lookup("myName");
       // System.out.print("name is " + name);

       System.out.println("session context: "+context);
       Principal principal = context.getCallerPrincipal();
       System.out.println("Principal: "+principal);
       System.out.println("Principal.getClass(): "+principal.getClass());
       System.out.println("caller name: "+context.getCallerPrincipal().getName());

       if( context.isCallerInRole("JBossAdmin") ) {
         System.out.println("User is in role!!");
       }

       String name = context.getCallerPrincipal().getName();
       System.out.println("*** Leaving Hello Bean!");
       return "Hello " + name + "!";
    }
}
