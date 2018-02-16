package jondruse;

import javax.ejb.Stateful;

@Stateful
public class StatefulJvmRoute implements EjbJvmRouteInterface {

    @Override
    public String getJvmroute() {
        return System.getProperty("jboss.mod_cluster.jvmRoute");
    }

}
