package jets.projects.beans;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BookBean {

    private int id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String genre;
    private BigDecimal price;
    private BigDecimal discount;
    private int quantity;
    private String status;
    private String mainImage;
    private List<String> images;
    private String publicationDate;
    private String language;
    private int pages;
    private String overview;
    private String description;

    // Constructor
    public BookBean(int id, String title, String author, String publisher,
            String isbn, String genre, BigDecimal price, BigDecimal discount, int quantity,
            String status, String mainImage, List<String> images,
            String publicationDate, String language, int pages, String overview, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.genre = genre;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.status = status;
        this.mainImage = mainImage;
        this.images = images;
        this.publicationDate = publicationDate;
        this.language = language;
        this.pages = pages;
        this.overview = overview;
        this.description = description;
    }

    public BookBean() {

    }

    public int getId() {
        return id;
    }

    public List<String> getImages() {
        return images;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}