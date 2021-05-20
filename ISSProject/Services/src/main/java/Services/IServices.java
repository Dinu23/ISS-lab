package Services;

import Domain.Bug;
import Domain.BugImportance;
import Domain.Employee;

import java.util.Set;

public interface IServices {
    Employee findEmployee(String username,String password) throws ServiceException;
    void addObserver(Employee currentEmployee, IObserver observer) throws  ServiceException;
    Set<Bug> findAllUnresolvedBugs() throws  ServiceException;
    Set<Bug> findAllNewBugs() throws  ServiceException;
    Bug addNewBug(String bugName, BugImportance bugImportance, String bugDescription) throws ServiceException;
}
