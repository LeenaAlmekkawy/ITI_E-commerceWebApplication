package jets.projects.client_dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BookDto {

    private Long bookID;
    private String title;
    private String author;
    private String genre;
    private String publisher;
    private LocalDate publicationDate;
    private String isbn;
    private String description;
    private String overview;
    private Integer numberOfPages;
    private String language;
    private Boolean isAvailable;
    private Integer stock;
    private BigDecimal price;
    private BigDecimal discountedPercentage;
    private List<BookImageDto> images;
    private Integer copiesSold;

    public Long getBookId() {
        return bookID;
    }

    public void setBookId(Long bookId) {
        this.bookID = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountedPercentage() {
        return discountedPercentage;
    }

    public void setDiscountedPercentage(BigDecimal discountedPercentage) {
        this.discountedPercentage = discountedPercentage;
    }

    public List<BookImageDto> getImages() {
        return images;
    }

    public void setImages(List<BookImageDto> images) {
        this.images = images;
    }

    public Integer getCopiesSold() {
        return copiesSold;
    }

    public void setCopiesSold(Integer copiesSold) {
        this.copiesSold = copiesSold;
    }
}
