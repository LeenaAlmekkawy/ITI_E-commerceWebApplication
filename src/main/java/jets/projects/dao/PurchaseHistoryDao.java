package jets.projects.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jets.projects.entity.PurchaseHistory;
import jets.projects.utils.JpaUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class PurchaseHistoryDao {
    private static final Logger LOGGER = Logger.getLogger(PurchaseHistoryDao.class.getName());
    private final EntityManagerFactory emf;

    public PurchaseHistoryDao() {
        this.emf = JpaUtil.getEntityManagerFactory();
    }

    public List<PurchaseHistory> findByUserId(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PurchaseHistory> query = em.createQuery(
                    "SELECT p FROM PurchaseHistory p JOIN FETCH p.user WHERE p.user.userId = :userId",
                    PurchaseHistory.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<PurchaseHistory> findByUserIdAndItemId(Long userId, Long itemId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PurchaseHistory> query = em.createQuery(
                    "SELECT p FROM PurchaseHistory p JOIN FETCH p.user WHERE p.user.userId = :userId AND p.itemId = :itemId",
                    PurchaseHistory.class
            );
            query.setParameter("userId", userId);
            query.setParameter("itemId", itemId);
            List<PurchaseHistory> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public void save(PurchaseHistory purchase) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(purchase);
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

    public List<PurchaseHistory> findAll(int page, int size, String search, LocalDate dateFrom, LocalDate dateTo) {
        EntityManager em = emf.createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT p FROM PurchaseHistory p JOIN FETCH p.user u WHERE 1=1"
            );
            List<String> conditions = new ArrayList<>();
            if (search != null && !search.trim().isEmpty()) {
                conditions.add("(LOWER(u.username) LIKE :search OR CONCAT(p.id, '') LIKE :search)");
            }
            if (dateFrom != null) {
                conditions.add("p.purchaseDate >= :dateFrom");
            }
            if (dateTo != null) {
                conditions.add("p.purchaseDate <= :dateTo");
            }
            if (!conditions.isEmpty()) {
                jpql.append(" AND ").append(String.join(" AND ", conditions));
            }
            LOGGER.info("Executing JPQL: " + jpql);
            TypedQuery<PurchaseHistory> query = em.createQuery(jpql.toString(), PurchaseHistory.class);
            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.toLowerCase() + "%");
            }
            if (dateFrom != null) {
                query.setParameter("dateFrom", dateFrom);
            }
            if (dateTo != null) {
                query.setParameter("dateTo", dateTo);
            }
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.severe("Error in findAll: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public long countFiltered(String search, LocalDate dateFrom, LocalDate dateTo) {
        EntityManager em = emf.createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT COUNT(p) FROM PurchaseHistory p JOIN p.user u WHERE 1=1"
            );
            List<String> conditions = new ArrayList<>();
            if (search != null && !search.trim().isEmpty()) {
                conditions.add("(LOWER(u.username) LIKE :search OR CONCAT(p.id, '') LIKE :search)");
            }
            if (dateFrom != null) {
                conditions.add("p.purchaseDate >= :dateFrom");
            }
            if (dateTo != null) {
                conditions.add("p.purchaseDate <= :dateTo");
            }
            if (!conditions.isEmpty()) {
                jpql.append(" AND ").append(String.join(" AND ", conditions));
            }
            LOGGER.info("Executing count JPQL: " + jpql);
            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.toLowerCase() + "%");
            }
            if (dateFrom != null) {
                query.setParameter("dateFrom", dateFrom);
            }
            if (dateTo != null) {
                query.setParameter("dateTo", dateTo);
            }
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.severe("Error in countFiltered: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<PurchaseHistory> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PurchaseHistory> query = em.createQuery(
                    "SELECT p FROM PurchaseHistory p JOIN FETCH p.user WHERE p.id = :id",
                    PurchaseHistory.class
            );
            query.setParameter("id", id);
            List<PurchaseHistory> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public long countTotalPurchases() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM PurchaseHistory p", Long.class
            );
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public BigDecimal sumTotalRevenue() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT SUM(p.totalPaid) FROM PurchaseHistory p", BigDecimal.class
            );
            BigDecimal result = query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public long countUniqueCustomers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(DISTINCT p.user) FROM PurchaseHistory p", Long.class
            );
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Object[]> sumSalesByMonth(LocalDateTime startDate) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT YEAR(p.purchaseDatetime), MONTH(p.purchaseDatetime), SUM(p.totalPaid) " +
                            "FROM PurchaseHistory p " +
                            "WHERE p.purchaseDatetime >= :startDate " +
                            "GROUP BY YEAR(p.purchaseDatetime), MONTH(p.purchaseDatetime) " +
                            "ORDER BY YEAR(p.purchaseDatetime), MONTH(p.purchaseDatetime)",
                    Object[].class
            );
            query.setParameter("startDate", startDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}