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
//
//    @EJB(lookup = "ejb:/server-side-slave3/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
//    private WhoAmIBeanRemote remoteBean3ByInjection;
//
//    @EJB(lookup = "ejb:/server-side-slave4/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
//    private WhoAmIBeanRemote remoteBean4ByInjection;
//
//    @EJB(lookup = "ejb:/server-side-slave5/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
//    private WhoAmIBeanRemote remoteBean5ByInjection;


    @Override
    public String callNext() throws RemoteException {
        StringBuilder sb = new StringBuilder();
        try {



            sb.append("WhoAmI from server-side-slave1 returned: ").append(remoteBean1ByInjection.whoAmI()).append("\n");
//            sb.append("WhoAmI from server-side-slave3 returned: ").append(remoteBean3ByInjection.whoAmI()).append("\n");
            sb.append("WhoAmI from server-side-slave2 returned: ").append(remoteBean2ByInjection.whoAmI()).append("\n");
//            sb.append("WhoAmI from server-side-slave2 returned: ").append(remoteBean2ByInjection.whoAmI()).append("\n");
            return sb.toString();
        } catch (Exception e) {
            sb.append(e.toString()).append("\n");
            if(e.getSuppressed() != null && e.getSuppressed().length > 0) {
                sb.append(e.getSuppressed()[0]).append("\n");
            }
            return sb.toString();
        }
    }

    @Override
    public String callNext(int i) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        try {
            WhoAmIBean whoAmIBean = null;
            switch (i) {
                case 1:
                    sb.append("WhoAmI from server-side-slave1 returned: ").append(remoteBean1ByInjection.whoAmI()).append("\n");
                    break;
                case 2:
                    sb.append("WhoAmI from server-side-slave2 returned: ").append(remoteBean2ByInjection.whoAmI()).append("\n");
                    break;
                default:
                    sb.append("none");
            }



//            sb.append("WhoAmI from server-side-slave1 returned: ").append(remoteBean1ByInjection.whoAmI()).append("\n");
//            sb.append("WhoAmI from server-side-slave3 returned: ").append(remoteBean3ByInjection.whoAmI()).append("\n");

            return sb.toString();
        } catch (Exception e) {
            sb.append(e.toString()).append("\n");
            if(e.getSuppressed() != null && e.getSuppressed().length > 0) {
                sb.append(e.getSuppressed()[0]).append("\n");
            }
            return sb.toString();
        }
    }
}
