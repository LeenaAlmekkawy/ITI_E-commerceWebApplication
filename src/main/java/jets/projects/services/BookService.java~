package jets.projects.services;

import jets.projects.dao.BookDao;
import jets.projects.client_dto.BookDto;
import jets.projects.client_dto.BookImageDto;
import jets.projects.entity.Book;
import jets.projects.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private final BookDao bookDao;

    public BookService() {
        this.bookDao = new BookDao();
    }

    // Utility method to convert Book entity to BookDto
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

    // 1. Get top 10 selling books
    public List<BookDto> getTopSellingBooks() {
        List<Book> books = bookDao.findTopSellingBooks(10);
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 2. Get top selling books by genre
    public List<BookDto> getTopSellingBooksByGenre() {
        List<Book> books = bookDao.findTopSellingBooksByGenre();
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 3. Get all books
    public List<BookDto> getAllBooks() {
        List<Book> books = bookDao.findAll();
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 4. Search books by keyword
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

    // 5. Search book by ID
    public BookDto getBookById(Long bookId) throws NotFoundException {
        return bookDao.findById(bookId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
    }
}