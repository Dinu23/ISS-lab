package Domain;/*
* This code has been generated by the Rebel: a code generator for modern Java.
*
* Drop us a line or two at feedback@archetypesoftware.com: we would love to hear from you!
*/

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@javax.persistence.Entity(name ="Employee")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type",discriminatorType = DiscriminatorType.INTEGER)
public class Employee extends Entity implements Serializable {
    private String username;
    private String password;
    private String name;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private Set<Bug> workingBugs;

    public Employee(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public Employee() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Bug> getWorkingBugs() {
        return workingBugs;
    }
    public void addBug(Bug bug){
        workingBugs.add(bug);
    }

    public void removeBug(Bug bug){
        workingBugs.removeIf(x->{
            return  x.getID().equals(bug.getID());
        });
    }
}