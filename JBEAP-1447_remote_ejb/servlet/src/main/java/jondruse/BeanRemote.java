package jondruse;

import java.rmi.RemoteException;

public interface BeanRemote {

    String callNext() throws RemoteException;

    String callNext(int i) throws RemoteException;
}
