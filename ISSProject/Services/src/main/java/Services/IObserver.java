package Services;

import Domain.Bug;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
    void newBug(Bug bug) throws RemoteException;
    void bugToOngoing(Bug bug) throws RemoteException;
    void bugToOnTesting(Bug bug) throws RemoteException;
    void bugToUnresolved(Bug bug) throws RemoteException;
    void bugToFinished(Bug bug) throws RemoteException;
}
