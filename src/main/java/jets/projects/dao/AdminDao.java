package jets.projects.dao;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jets.projects.entity.Admin;
import jets.projects.utils.JpaUtil;

public class AdminDao 
{
    private final EntityManagerFactory emf;

    public AdminDao() {
        this.emf = JpaUtil.getEntityManagerFactory();
    }


    public Optional<Admin> findById(Long adminId) 
    {
        EntityManager em = emf.createEntityManager();
        try {
            Admin admin = em.find(Admin.class, adminId);
            return Optional.ofNullable(admin);
        } finally {
            em.close();
        }
    }


    public Optional<Admin> findByUsername(String username) 
    {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Admin> query = em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
                    .setParameter("username", username);
            return query.getResultList().stream().findFirst();
        } finally {
            em.close();
        }
    }

}