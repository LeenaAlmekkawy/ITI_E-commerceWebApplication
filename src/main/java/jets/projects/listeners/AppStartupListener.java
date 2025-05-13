package jets.projects.listeners;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jets.projects.utils.DataBaseInitializer;
import jets.projects.utils.JpaUtil;

public class AppStartupListener implements ServletContextListener 
{
    @Override
    public void contextInitialized(ServletContextEvent event) 
    {
        try {
            EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                em.getTransaction().commit();
            } finally {
                em.close();
            }

            System.out.println("Database connection established successfully.");
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw new RuntimeException("Application will not start due to database connection failure.", e);
        }

        // Initialize database
        DataBaseInitializer.initializeData();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) 
    {
        JpaUtil.closeEntityManagerFactory();
        System.out.println("EntityManagerFactory closed.");

        // Deregister MySQL driver and cleanup
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();

            // Deregister JDBC drivers
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                    System.out.println("Deregistered JDBC driver: " + driver);
                } catch (SQLException ex) {
                    System.err.println("Error deregistering JDBC driver: " + driver);
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error shutting down MySQL cleanup thread or deregistering drivers.");
            e.printStackTrace();
        }
    }
}