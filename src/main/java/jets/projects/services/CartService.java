package jets.projects.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jets.projects.client_dto.CartItemDto;
import jets.projects.dao.BookDao;
import jets.projects.dao.CartItemDao;
import jets.projects.dao.UserDao;
import jets.projects.entity.Book;
import jets.projects.entity.CartItem;
import jets.projects.entity.User;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OutOfStockException;

public class CartService {

    private final CartItemDao cartItemDao;
    private final BookDao bookDao;
    private final UserDao userDao;

    public CartService() {
        this.cartItemDao = new CartItemDao();
        this.bookDao = new BookDao();
        this.userDao = new UserDao();
    }

    public List<CartItemDto> getUserCart(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<CartItem> cartItems = cartItemDao.findByUserId(userId);
        return cartItems.stream()
                .map(item -> {
                    CartItemDto dto = new CartItemDto();
                    dto.setBookId(item.getBook().getBookId());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public boolean validateCart(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<CartItem> cartItems = cartItemDao.findByUserId(userId);
        for (CartItem item : cartItems) {
            Book book = item.getBook();
            if (book == null || !book.getIsAvailable() || book.getStock() < item.getQuantity()) {
                return false;
            }
        }
        if (cartItems.isEmpty()) {
            throw new NotFoundException("Cart is empty");
        }
        return true;
    }

    public BigDecimal getShippingFee(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<CartItem> cartItems = cartItemDao.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new NotFoundException("Cart is empty");
        }

        return new BigDecimal("28.00");
    }

    public boolean addToCart(Long userId, Long bookId, Integer quantity) throws InvalidInputException, NotFoundException, OutOfStockException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (bookId == null || bookId <= 0) {
            throw new InvalidInputException("Invalid book ID");
        }
        if (quantity == null || quantity <= 0) {
            throw new InvalidInputException("Quantity must be positive");
        }

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));

        if (!book.getIsAvailable() || book.getStock() < quantity) {
            throw new OutOfStockException("Book is out of stock or insufficient quantity");
        }

        Optional<CartItem> existingItem = cartItemDao.findByUserIdAndBookId(userId, bookId);
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (book.getStock() < newQuantity) {
                throw new OutOfStockException("Insufficient stock for updated quantity");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setBook(book);
            cartItem.setQuantity(quantity);
        }

        try {
            cartItemDao.saveOrUpdate(cartItem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateCart(Long userId, Long bookId, Integer newQuantity) throws InvalidInputException, NotFoundException, OutOfStockException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (bookId == null || bookId <= 0) {
            throw new InvalidInputException("Invalid book ID");
        }
        if (newQuantity == null || newQuantity < 0) {
            throw new InvalidInputException("Quantity must be non-negative");
        }

        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));

        Optional<CartItem> cartItemOpt = cartItemDao.findByUserIdAndBookId(userId, bookId);
        if (!cartItemOpt.isPresent()) {
            return false;
        }

        CartItem cartItem = cartItemOpt.get();
        if (newQuantity == 0) {
            return cartItemDao.delete(cartItem);
        }

        if (!book.getIsAvailable() || book.getStock() < newQuantity) {
            throw new OutOfStockException("Book is out of stock or insufficient quantity");
        }

        cartItem.setQuantity(newQuantity);
        try {
            cartItemDao.saveOrUpdate(cartItem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeFromCart(Long userId, Long bookId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (bookId == null || bookId <= 0) {
            throw new InvalidInputException("Invalid book ID");
        }

        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Optional<CartItem> cartItemOpt = cartItemDao.findByUserIdAndBookId(userId, bookId);
        if (!cartItemOpt.isPresent()) {
            return false;
        }

        return cartItemDao.delete(cartItemOpt.get());
    }

    public boolean truncateCart(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        return cartItemDao.deleteAllByUserId(userId);
    }

    public BigDecimal getCartSubtotal(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        BigDecimal subtotal = cartItemDao.calculateCartSubtotal(userId);
        return subtotal != null ? subtotal : BigDecimal.ZERO;
    }

}
