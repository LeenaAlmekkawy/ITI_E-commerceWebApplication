package jets.projects.utils;

import jets.projects.beans.BookBean;
import jets.projects.client_dto.BookDto;
import jets.projects.client_dto.BookImageDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MapperUtil {

    private static final String BASE_URL = "http://localhost:8080/ITI-Jets-EcommApplication/Admin/Images/";

    public static BookDto convertToBookDto(BookBean bookBean) {
        BookDto dto = new BookDto();
        dto.setBookId((long) bookBean.getId());
        dto.setTitle(bookBean.getTitle());
        dto.setAuthor(bookBean.getAuthor());
        dto.setGenre(bookBean.getGenre());
        dto.setPublisher(bookBean.getPublisher());
        // Parse publicationDate from String to LocalDate
        String publicationDateStr = String.valueOf(bookBean.getPublicationDate());
        if (publicationDateStr != null && !publicationDateStr.trim().isEmpty()) {
            dto.setPublicationDate(LocalDate.parse(publicationDateStr));
        } else {
            dto.setPublicationDate(null);
        }
        dto.setIsbn(bookBean.getIsbn());
        dto.setDescription(bookBean.getDescription());
        dto.setOverview(bookBean.getOverview());
        dto.setNumberOfPages(bookBean.getPages());
        dto.setLanguage(bookBean.getLanguage());
        // Map status to isAvailable (assuming "available" means true)
        dto.setIsAvailable(bookBean.getStatus() != null && bookBean.getStatus().equalsIgnoreCase("available"));
        dto.setStock(bookBean.getQuantity());
        dto.setPrice(bookBean.getPrice());
        dto.setDiscountedPercentage(bookBean.getDiscount());
        dto.setCopiesSold(0); // Default for new books, not present in BookBean

        // Map images
        List<BookImageDto> images = new ArrayList<>();
        // Map mainImage
        if (bookBean.getMainImage() != null && !bookBean.getMainImage().trim().isEmpty()) {
            BookImageDto mainImg = new BookImageDto();
            String mainImageUrl = bookBean.getMainImage();
            // Prepend BASE_URL if not already present
            if (!mainImageUrl.startsWith(BASE_URL)) {
                mainImageUrl = BASE_URL + mainImageUrl;
            }
            mainImg.setUrl(mainImageUrl);
            mainImg.setIsMain(true);
            images.add(mainImg);
        }
        // Map additional images
        List<String> additionalImages = bookBean.getImages();
        if (additionalImages != null) {
            for (String url : additionalImages) {
                if (url != null && !url.trim().isEmpty()) {
                    BookImageDto img = new BookImageDto();
                    // Prepend BASE_URL if not already present
                    if (!url.startsWith(BASE_URL)) {
                        url = BASE_URL + url;
                    }
                    img.setUrl(url);
                    img.setIsMain(false);
                    images.add(img);
                }
            }
        }
        dto.setImages(images);
        return dto;
    }

    public static BookBean convertToBookBean(BookDto bookDto) {
        BookBean bean = new BookBean();
        bean.setId(bookDto.getBookId() != null ? bookDto.getBookId().intValue() : 0);
        bean.setTitle(bookDto.getTitle());
        bean.setAuthor(bookDto.getAuthor());
        bean.setGenre(bookDto.getGenre());
        bean.setPublisher(bookDto.getPublisher());
        bean.setPublicationDate(bookDto.getPublicationDate() != null ? bookDto.getPublicationDate().toString() : null);
        bean.setIsbn(bookDto.getIsbn());
        bean.setDescription(bookDto.getDescription());
        bean.setOverview(bookDto.getOverview());
        bean.setPages(bookDto.getNumberOfPages() != null ? bookDto.getNumberOfPages() : 0);
        bean.setLanguage(bookDto.getLanguage());
        bean.setStatus(bookDto.getIsAvailable() != null && bookDto.getIsAvailable() ? "available" : "unavailable");
        bean.setQuantity(bookDto.getStock() != null ? bookDto.getStock() : 0);
        bean.setPrice(bookDto.getPrice());
        bean.setDiscount(bookDto.getDiscountedPercentage());
        // Map images
        List<String> additionalImages = new ArrayList<>();
        if (bookDto.getImages() != null) {
            for (BookImageDto img : bookDto.getImages()) {
                if (img.getIsMain() != null && img.getIsMain()) {
                    bean.setMainImage(img.getUrl());
                } else {
                    additionalImages.add(img.getUrl());
                }
            }
        }
        bean.setImages(additionalImages);
        return bean;
    }
}
