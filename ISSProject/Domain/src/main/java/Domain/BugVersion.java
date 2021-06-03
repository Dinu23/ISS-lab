package Domain;/*
* This code has been generated by the Rebel: a code generator for modern Java.
*
* Drop us a line or two at feedback@archetypesoftware.com: we would love to hear from you!
*/

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@javax.persistence.Entity(name = "BugVersion")
@Table(name = "BugVersion")
public class BugVersion extends Entity  implements Serializable {
    private Integer Version;
    private String Description;
    public BugVersion() {
    }

    public BugVersion(Integer version, String description) {
        Version = version;
        Description = description;
    }

    public Integer getVersion() {
        return Version;
    }

    public String getDescription() {
        return Description;
    }


    public void setVersion(Integer Version) {
        this.Version = Version;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }


}