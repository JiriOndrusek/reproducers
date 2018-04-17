package jondruse;

import java.rmi.RemoteException;

public interface BeanRemote {

    String callNext() throws RemoteException;
}
