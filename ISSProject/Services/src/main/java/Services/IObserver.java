package Services;

import Domain.Bug;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
    void newBug(Bug bug) throws RemoteException;
}
