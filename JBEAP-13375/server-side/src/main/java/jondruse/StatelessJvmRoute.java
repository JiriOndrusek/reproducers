package jondruse;

import javax.ejb.Stateless;

@Stateless
public class StatelessJvmRoute implements EjbJvmRouteInterface {

    @Override
    public String getJvmroute() {
        System.out.println("<><><><><><><><><><><><><><>");
        return System.getProperty("jboss.mod_cluster.jvmRoute");
    }

}
