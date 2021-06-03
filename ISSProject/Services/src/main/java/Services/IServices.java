package Services;

import Domain.*;

import java.util.Set;

public interface IServices {
    Employee findEmployee(String username,String password) throws ServiceException;
    void addObserver(Employee currentEmployee, IObserver observer) throws  ServiceException;
    Set<Bug> findAllUnresolvedBugs() throws  ServiceException;
    Set<Bug> findAllNewBugs() throws  ServiceException;
    Set<Bug> findAllOnTestingBugs() throws  ServiceException;
    Set<Bug> findAllFinishedBugs() throws  ServiceException;
    Bug addNewBug(String bugName, BugImportance bugImportance, String bugDescription) throws ServiceException;
    Bug updateBugToOngoing(Bug bug) throws ServiceException;
    Employee addBugToList(Developer dev, Bug bug) throws ServiceException;
    Bug sendBugForTesting(Bug bug) throws ServiceException;
    Employee removeBugFromList(Developer tester, Bug bug) throws ServiceException;
    Bug resendBug(Bug bug) throws  ServiceException;
    Bug finishBug(Bug bug) throws ServiceException;
}
