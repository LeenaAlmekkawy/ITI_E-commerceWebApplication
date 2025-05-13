package jets.projects.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jets.projects.entity.CartItem;
import jets.projects.utils.JpaUtil;

public class CartItemDao {

    private final EntityManagerFactory emf;

    public CartItemDao() {
        this.emf = JpaUtil.getEntityManagerFactory();
    }

    // Find cart items by user ID
    public List<CartItem> findByUserId(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<CartItem> query = em.createQuery(
                    "SELECT DISTINCT c FROM CartItem c LEFT JOIN FETCH c.book b LEFT JOIN FETCH b.images WHERE c.user.userId = :userId",
                    CartItem.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Find specific cart item by user ID and book ID
    public Optional<CartItem> findByUserIdAndBookId(Long userId, Long bookId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<CartItem> query = em.createQuery(
                    "SELECT c FROM CartItem c LEFT JOIN FETCH c.book b LEFT JOIN FETCH b.images WHERE c.user.userId = :userId AND c.book.bookId = :bookId",
                    CartItem.class
            );
            query.setParameter("userId", userId);
            query.setParameter("bookId", bookId);
            List<CartItem> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    // Save or update cart item
    public void saveOrUpdate(CartItem cartItem) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (cartItem.getCartItemId() == null) {
                em.persist(cartItem);
            } else {
                em.merge(cartItem);
            }
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

    // Delete cart item
    public boolean delete(CartItem cartItem) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CartItem managedItem = em.find(CartItem.class, cartItem.getCartItemId());
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

    // Delete all cart items for a user
    public boolean deleteAllByUserId(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<CartItem> query = em.createQuery(
                    "SELECT c FROM CartItem c WHERE c.user.userId = :userId",
                    CartItem.class
            );
            query.setParameter("userId", userId);
            List<CartItem> items = query.getResultList();
            for (CartItem item : items) {
                em.remove(em.merge(item));
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    public BigDecimal calculateCartSubtotal(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT SUM(c.quantity * (b.price - (b.price * b.discountPercentage / 100))) "
                    + "FROM CartItem c "
                    + "JOIN c.book b "
                    + "WHERE c.user.userId = :userId",
                    BigDecimal.class
            );
            query.setParameter("userId", userId);
            BigDecimal subtotal = query.getSingleResult();
            return subtotal != null ? subtotal : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
}
