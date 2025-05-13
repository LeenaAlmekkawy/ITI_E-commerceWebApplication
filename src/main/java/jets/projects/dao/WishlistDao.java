package jets.projects.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jets.projects.entity.Wishlist;
import jets.projects.utils.JpaUtil;

import java.util.List;
import java.util.Optional;

public class WishlistDao {
    private final EntityManagerFactory emf;

    public WishlistDao() {
        this.emf = JpaUtil.getEntityManagerFactory();
    }

    // Find wishlist items by user ID
    public List<Wishlist> findByUserId(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Wishlist> query = em.createQuery(
                    "SELECT DISTINCT w FROM Wishlist w LEFT JOIN FETCH w.book b LEFT JOIN FETCH b.images WHERE w.user.userId = :userId",
                    Wishlist.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Find wishlist item by user ID and book ID
    public Optional<Wishlist> findByUserIdAndBookId(Long userId, Long bookId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Wishlist> query = em.createQuery(
                    "SELECT w FROM Wishlist w LEFT JOIN FETCH w.book b LEFT JOIN FETCH b.images WHERE w.user.userId = :userId AND w.book.bookId = :bookId",
                    Wishlist.class
            );
            query.setParameter("userId", userId);
            query.setParameter("bookId", bookId);
            List<Wishlist> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    // Save wishlist item
    public void save(Wishlist wishlist) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(wishlist);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Delete wishlist item
    public boolean delete(Wishlist wishlist) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Wishlist managedItem = em.find(Wishlist.class, wishlist.getWishlistId());
            if (managedItem != null) {
                em.remove(managedItem);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().commit();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }
}