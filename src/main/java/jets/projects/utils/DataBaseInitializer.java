package jets.projects.utils;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManager;
import jets.projects.entity.Admin;
import jets.projects.entity.Genre;
import jets.projects.entity.PaymentMethod;

public class DataBaseInitializer 
{

    public static void initializeData() 
    {
        initializeGenres();
        initializeAdmins();
        initializePaymentMethods();
    }

    private static void initializeGenres() 
    {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            List<String> genreNames = Arrays.asList(
                "Fiction",
                "Non-Fiction",
                "Personal Development",
                "Business & Finance",
                "Science & Technology",
                "Health & Wellness",
                "Education & Academic",
                "Biography",
                "Childrens Books",
                "Religion"
            );

            for (String name : genreNames) 
            {
                Genre existingGenre = em.find(Genre.class, name);
                if (existingGenre == null) 
                {
                    Genre genre = new Genre();
                    genre.setName(name);
                    em.persist(genre);
                }
            }

            em.getTransaction().commit();
            System.out.println("Genres initialized successfully.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Failed to initialize genres: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    private static void initializeAdmins() 
    {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            List<Admin> defaultAdmins = Arrays.asList(
                new Admin(1L, "123123", "Ibrahim"),  
                new Admin(2L, "123123", "Leena"), 
                new Admin(3L, "123123", "Mounir"),  
                new Admin(4L, "admin", "admin")
            );

            for (Admin admin : defaultAdmins) 
            {
                Admin existingAdmin = em.find(Admin.class, admin.getAdminId());
                if (existingAdmin == null) 
                    em.persist(admin);
            }

            em.getTransaction().commit();
            System.out.println("Admins initialized successfully.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Failed to initialize admins: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    private static void initializePaymentMethods() 
    {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            List<String> paymentMethods = Arrays.asList(
                "credit_card",
                "account_balance"
            );
            for(String paymentMethod : paymentMethods) {
                PaymentMethod existingPaymentMethod = em.find(PaymentMethod.class, paymentMethod);
                if (existingPaymentMethod == null) {
                    PaymentMethod method = new PaymentMethod();
                    method.setMethod(paymentMethod);
                    em.persist(method); 
                }
            }

            em.getTransaction().commit();
            System.out.println("Payment methods initialized successfully.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Failed to initialize payment methods: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

}

