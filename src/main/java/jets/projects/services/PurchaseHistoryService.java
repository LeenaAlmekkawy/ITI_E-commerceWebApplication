package jets.projects.services;

import jets.projects.dao.PurchaseHistoryDao;
import jets.projects.dao.UserDao;
import jets.projects.client_dto.PurchaseHistoryDto;
import jets.projects.dto.PurchaseDTO;

import jets.projects.entity.PurchaseHistory;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PurchaseHistoryService {
    private static final Logger LOGGER = Logger.getLogger(PurchaseHistoryService.class.getName());
    private final PurchaseHistoryDao purchaseDao;
    private final UserDao userDao;

    public PurchaseHistoryService() {
        this.purchaseDao = new PurchaseHistoryDao();
        this.userDao = new UserDao();
    }

    public List<PurchaseDTO> findAll(int page, int size, String search, LocalDate dateFrom, LocalDate dateTo) {
        try {
            return purchaseDao.findAll(page, size, search, dateFrom, dateTo).stream()
                    .map(this::convertToDTOForAdmin)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.severe("Error in findAll: " + e.getMessage());
            throw new RuntimeException("Failed to fetch purchases", e);
        }
    }

    public Optional<PurchaseDTO> findById(Long id) {
        try {
            return purchaseDao.findById(id).map(this::convertToDTOForAdmin);
        } catch (Exception e) {
            LOGGER.severe("Error finding purchase by ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to fetch purchase", e);
        }
    }

    public long countFiltered(String search, LocalDate dateFrom, LocalDate dateTo) {
        try {
            return purchaseDao.countFiltered(search, dateFrom, dateTo);
        } catch (Exception e) {
            LOGGER.severe("Error in countFiltered: " + e.getMessage());
            throw new RuntimeException("Failed to count purchases", e);
        }
    }

    private PurchaseDTO convertToDTOForAdmin(PurchaseHistory purchase) {
        PurchaseDTO dto = new PurchaseDTO();
        dto.setId(purchase.getReceiptId());
        dto.setUserName(purchase.getUser() != null ? purchase.getUser().getUsername() : "Unknown");
        dto.setUserEmail(purchase.getUser() != null ? purchase.getUser().getEmail() : "N/A");
        dto.setDate(purchase.getPurchaseDatetime().toString());
        dto.setTotalPaid(purchase.getTotalPaid());
        return dto;
    }

    // 1. Get all purchase history for a user
    public List<PurchaseHistoryDto> getPurchaseHistory(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<PurchaseHistory> purchaseHistory = purchaseDao.findByUserId(userId);
        return purchaseHistory.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 2. Get specific purchase history item
    public PurchaseHistoryDto getPurchaseItem(Long userId, Long itemId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (itemId == null || itemId <= 0) {
            throw new InvalidInputException("Invalid item ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        PurchaseHistory purchase = purchaseDao.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new NotFoundException("Purchase history item not found with ID: " + itemId + " for user ID: " + userId));
        return convertToDto(purchase);
    }

    // Convert PurchaseHistory to PurchaseHistoryDto
    private PurchaseHistoryDto convertToDto(PurchaseHistory purchase) {
        PurchaseHistoryDto dto = new PurchaseHistoryDto();
        dto.setItemId(purchase.getReceiptId());
        dto.setUserId(purchase.getUser().getUserId());
        dto.setDate(purchase.getPurchaseDatetime());
        dto.setTotalPaid(purchase.getTotalPaid());
        dto.setReceiptFileUrl(purchase.getReceiptFileUrl());
        return dto;
    }


}