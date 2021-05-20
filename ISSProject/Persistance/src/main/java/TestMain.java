import Domain.*;
import Persistance.BugsRepository;
import Persistance.EmployeeRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestMain {


    public static void main(String[] args) {
        try {

            initialize();
            Bug b = new Bug("b3", BugImportance.CRITICAL,BugStatus.NEW,"Dasdasda");

            BugVersion bg = new BugVersion(1,"dasdasda");
            b.addBugVersion(bg);
            BugsRepository bugsRepository = new BugsRepository();
            Bug bb =        bugsRepository.addBug(b);
            System.out.println(bb.getID());

        }catch (Exception e){
            System.err.println("Exception "+e);
            e.printStackTrace();
        }finally {
            close();
        }
    }


    static SessionFactory sessionFactory;
    static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Exception "+e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    static void close(){
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }

    }


}
