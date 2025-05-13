package jets.projects.services;

import jets.projects.dao.BookDao;
import jets.projects.dao.UserDao;
import jets.projects.dao.WishlistDao;
import jets.projects.client_dto.BookDto;
import jets.projects.client_dto.BookImageDto;
import jets.projects.client_dto.WishlistItemDto;
import jets.projects.entity.Book;
import jets.projects.entity.User;
import jets.projects.entity.Wishlist;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OperationFailedException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WishlistService {
    private final WishlistDao wishlistDao;
    private final UserDao userDao;
    private final BookDao bookDao;

    public WishlistService() {
        this.wishlistDao = new WishlistDao();
        this.userDao = new UserDao();
        this.bookDao = new BookDao();
    }

    // Convert Book to BookDto
    private BookDto convertToBookDto(Book book) {
        BookDto dto = new BookDto();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenre(book.getGenre().getName());
        dto.setPublisher(book.getPublisher());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setOverview(book.getOverview());
        dto.setNumberOfPages(book.getNumberOfPages());
        dto.setLanguage(book.getLanguage());
        dto.setIsAvailable(book.getIsAvailable());
        dto.setStock(book.getStock());
        dto.setPrice(book.getPrice());
        dto.setDiscountedPercentage(book.getDiscountPercentage());
        dto.setCopiesSold(book.getSoldCount());
        List<BookImageDto> imageDtos = book.getImages().stream()
                .map(img -> {
                    BookImageDto imgDto = new BookImageDto();
                    imgDto.setUrl(img.getUrl());
                    imgDto.setIsMain(img.getIsMain());
                    return imgDto;
                })
                .collect(Collectors.toList());
        dto.setImages(imageDtos);
        return dto;
    }

    // 1. Get all wishlist items for a user
    public List<WishlistItemDto> getWishlistItems(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<Wishlist> wishlistItems = wishlistDao.findByUserId(userId);
        return wishlistItems.stream()
                .map(item -> {
                    WishlistItemDto dto = new WishlistItemDto();
                    dto.setUserId(item.getUser().getUserId());
                    dto.setBookId(item.getBook().getBookId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 2. Get all books in the wishlist
    public List<BookDto> getWishlistBooks(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<Wishlist> wishlistItems = wishlistDao.findByUserId(userId);
        return wishlistItems.stream()
                .map(item -> convertToBookDto(item.getBook()))
                .collect(Collectors.toList());
    }

    // 3. Add book to wishlist
    public boolean addToWishlist(Long userId, Long bookId) throws InvalidInputException, NotFoundException, OperationFailedException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (bookId == null || bookId <= 0) {
            throw new InvalidInputException("Invalid book ID");
        }

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));

        if (wishlistDao.findByUserIdAndBookId(userId, bookId).isPresent()) {
            throw new OperationFailedException("Book is already in the wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setBook(book);

        try {
            wishlistDao.save(wishlist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 4. Remove book from wishlist
    public boolean removeFromWishlist(Long userId, Long bookId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (bookId == null || bookId <= 0) {
            throw new InvalidInputException("Invalid book ID");
        }

        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Optional<Wishlist> wishlistItem = wishlistDao.findByUserIdAndBookId(userId, bookId);
        if (!wishlistItem.isPresent()) {
            return false;
        }

        return wishlistDao.delete(wishlistItem.get());
    }
}