package jondruse;

import example.ejb.WhoAmIBean;
import example.ejb.WhoAmIBeanRemote;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.rmi.RemoteException;

@Stateless
public class Bean implements BeanRemote {
    @EJB(lookup = "ejb:/server-side-slave1/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
    private WhoAmIBeanRemote remoteBean1ByInjection;

    @EJB(lookup = "ejb:/server-side-slave2/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
    private WhoAmIBeanRemote remoteBean2ByInjection;


    @Override
    public String callNext() throws RemoteException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("WhoAmI from server-side-slave1 returned: ").append(remoteBean1ByInjection.whoAmI()).append("\n");
            sb.append("WhoAmI from server-side-slave2 returned: ").append(remoteBean2ByInjection.whoAmI()).append("\n");
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
