package Service;

import Domain.*;
import Persistance.BugsRepository;
import Persistance.EmployeeRepository;
import Services.IObserver;
import Services.IServices;
import Services.ServiceException;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Service implements IServices {

    BugsRepository repoBugs;
    EmployeeRepository repoEmployee;
    private Map<Employee, IObserver> observers;

    public Service(BugsRepository repoBugs, EmployeeRepository repoEmployee) {
        this.repoBugs = repoBugs;
        this.repoEmployee = repoEmployee;
        observers = new ConcurrentHashMap<>();
    }

    public Service() {
    }


    @Override
    public synchronized Employee findEmployee(String username, String password) throws ServiceException {
        Employee currentEmployee = repoEmployee.findEmployee(username, password);
        return currentEmployee;
    }

    @Override
    public synchronized void addObserver(Employee currentEmployee, IObserver observer) throws ServiceException {
        if (observers.get(currentEmployee) != null) {
            throw new ServiceException();
        }
        observers.put(currentEmployee, observer);
    }

    @Override
    public synchronized Set<Bug> findAllUnresolvedBugs() throws ServiceException {
        return repoBugs.findAllBugs(BugStatus.UN_RESOLVED);
    }

    @Override
    public synchronized Set<Bug> findAllNewBugs() throws ServiceException {
        return repoBugs.findAllBugs(BugStatus.NEW);
    }

    @Override
    public Set<Bug> findAllOnTestingBugs() throws ServiceException {
        return repoBugs.findAllBugs(BugStatus.ON_TESTING);
    }

    @Override
    public Set<Bug> findAllFinishedBugs() throws ServiceException {
        return  repoBugs.findAllBugs(BugStatus.FINISHED);
    }


    @Override
    public synchronized Bug addNewBug(String bugName, BugImportance bugImportance, String bugDescription) throws ServiceException {
        Bug bug = new Bug(bugName, bugImportance, BugStatus.NEW, bugDescription);
        BugVersion bugVersion = new BugVersion(1, bugDescription);
        bug.addBugVersion(bugVersion);
        Bug newBug = repoBugs.addBug(bug);
        if (newBug != null) {
            notifyNewBug(newBug);
        }
        return newBug;
    }

    @Override
    public synchronized Bug updateBugToOngoing(Bug bug) throws ServiceException {
        notifyBugToOngoing(bug);
        bug.setBugStatus(BugStatus.ON_GOING);
        bug = repoBugs.updateBug(bug);
        return bug;
    }

    @Override
    public synchronized Employee addBugToList(Developer dev, Bug bug) throws ServiceException {
        dev.addBug(bug);
        return repoEmployee.updateEmployee(dev);
    }

    @Override
    public synchronized Bug sendBugForTesting(Bug bug) throws ServiceException {
        notifyBugToOnTesting(bug);
        bug.setBugStatus(BugStatus.ON_TESTING);
        bug = repoBugs.updateBug(bug);
        return bug;
    }

    @Override
    public synchronized Employee removeBugFromList(Developer dev, Bug bug) throws ServiceException {
        dev.removeBug(bug);
        return repoEmployee.updateEmployee(dev);
    }

    @Override
    public Bug resendBug(Bug bug) throws ServiceException {
        bug.setBugStatus(BugStatus.UN_RESOLVED);
        bug = repoBugs.updateBug(bug);
        notifyBugToUnresolved(bug);
        return bug;
    }

    @Override
    public Bug finishBug(Bug bug) throws ServiceException {
        bug.setBugStatus(BugStatus.FINISHED);
        bug = repoBugs.updateBug(bug);
        notifyBugToFinished(bug);
        return bug;
    }


    private final int defaultThreadsNo = 5;

    private void notifyNewBug(Bug bug) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        observers.forEach((x, y) -> {
            if (x instanceof Developer) {
                executor.execute(() -> {
                    try {
                        y.newBug(bug);
                    } catch (RemoteException e) {

                    }
                });
            }
        });
        executor.shutdown();
    }

    private void notifyBugToOngoing(Bug bug){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        observers.forEach((x, y) -> {
            if (x instanceof Developer) {
                executor.execute(() -> {
                    try {
                        y.bugToOngoing(bug);
                    } catch (RemoteException e) {

                    }
                });
            }
        });
        executor.shutdown();
    }

    private void notifyBugToOnTesting(Bug bug){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        observers.forEach((x, y) -> {
            if (x instanceof Tester) {
                executor.execute(() -> {
                    try {
                        y.bugToOnTesting(bug);
                    } catch (RemoteException e) {

                    }
                });
            }
        });
        executor.shutdown();
    }

    private void notifyBugToUnresolved(Bug bug){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        observers.forEach((x, y) -> {
            executor.execute(() -> {
                try {
                    y.bugToUnresolved(bug);
                } catch (RemoteException e) {

                }
            });

        });
        executor.shutdown();
    }

    private void notifyBugToFinished(Bug bug){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        observers.forEach((x, y) -> {
            executor.execute(() -> {
                try {
                    y.bugToFinished(bug);
                } catch (RemoteException e) {

                }
            });

        });
        executor.shutdown();
    }
}
