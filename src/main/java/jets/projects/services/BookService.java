package jets.projects.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import jets.projects.client_dto.BookDto;
import jets.projects.client_dto.BookImageDto;
import jets.projects.dao.BookDao;
import jets.projects.entity.Book;
import jets.projects.entity.BookImage;
import jets.projects.entity.Genre;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OperationFailedException;

public class BookService {

    private final BookDao bookDao;

    public BookService() {
        this.bookDao = new BookDao();
    }

    private BookDto convertToDto(Book book) {
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

    private Book convertToEntity(BookDto dto) {
        Book book = new Book();
        book.setBookId(dto.getBookId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        Genre genre = new Genre();
        genre.setName(dto.getGenre());
        book.setGenre(genre);
        book.setPublisher(dto.getPublisher());
        book.setPublicationDate(dto.getPublicationDate());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setOverview(dto.getOverview());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setLanguage(dto.getLanguage());
        book.setIsAvailable(dto.getIsAvailable());
        book.setStock(dto.getStock());
        book.setPrice(dto.getPrice());
        book.setDiscountPercentage(dto.getDiscountedPercentage());
        book.setSoldCount(dto.getCopiesSold() != null ? dto.getCopiesSold() : 0);
        if (dto.getImages() != null) {
            List<BookImage> images = dto.getImages().stream()
                    .map(imgDto -> {
                        BookImage img = new BookImage();
                        img.setUrl(imgDto.getUrl());
                        img.setIsMain(imgDto.getIsMain());
                        img.setBook(book);
                        return img;
                    })
                    .collect(Collectors.toList());
            book.setImages(images);
        }
        return book;
    }

    public List<BookDto> getTopSellingBooks() {
        List<Book> books = bookDao.findTopSellingBooks(10);
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getTopSellingBooksByGenre() {
        List<Book> books = bookDao.findTopSellingBooksByGenre();

        // Force initialize lazy collections before session closes
        books.forEach(book -> book.getImages().size());
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getAllBooks() {
        List<Book> books = bookDao.findAll();

        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> searchBooks(String keyword) throws NotFoundException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new NotFoundException("Search keyword cannot be empty");
        }

        List<Book> books = bookDao.searchByKeyword(keyword);
        if (books.isEmpty()) {
            throw new NotFoundException("No books found for keyword: " + keyword);
        }

        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookById(Long bookId) throws NotFoundException {
        return bookDao.findById(bookId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
    }


    public void addBook(BookDto bookDto) throws InvalidInputException, OperationFailedException {
        if (bookDto == null || bookDto.getTitle() == null || bookDto.getAuthor() == null || bookDto.getIsbn() == null) {
            throw new InvalidInputException("Book title, author, and ISBN are required");
        }
        if (bookDto.getPrice() == null || bookDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Price must be positive");
        }
        if (bookDto.getStock() != null && bookDto.getStock() < 0) {
            throw new InvalidInputException("Stock cannot be negative");
        }
        try {
            Book book = convertToEntity(bookDto);
            book.setBookId(null); // Ensure new book gets a new ID
            bookDao.save(book);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to add book", e);
        }
    }

    // Edit an existing book
    public boolean editBook(Long bookId, BookDto bookDto) throws InvalidInputException, NotFoundException {
        if (bookId == null || bookId <= 0) {
            throw new InvalidInputException("Invalid book ID");
        }
        if (bookDto == null || bookDto.getTitle() == null || bookDto.getAuthor() == null || bookDto.getIsbn() == null) {
            throw new InvalidInputException("Book title, author, and ISBN are required");
        }
        if (bookDto.getPrice() == null || bookDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Price must be positive");
        }
        if (bookDto.getStock() != null && bookDto.getStock() < 0) {
            throw new InvalidInputException("Stock cannot be negative");
        }
        Book existingBook = bookDao.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
        try {
            Book updatedBook = convertToEntity(bookDto);
            updatedBook.setBookId(bookId); // Retain original ID
            bookDao.update(updatedBook);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Delete a book
    public boolean deleteBook(Long bookId) throws InvalidInputException, NotFoundException {
        if (bookId == null || bookId <= 0) {
            throw new InvalidInputException("Invalid book ID");
        }
        bookDao.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
        return bookDao.deleteById(bookId);
    }
}
