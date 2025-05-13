'use strict';

import Genres from './Genres.js';
import DataValidator from "../Utils/DataValidator.js";
import BookImage from "./BookImage.js";

export default class Book {
    #bookID = -1; // Will be set when saved
    #title = '';
    #author = '';
    #genre = Genres.UNSPECIFIED;
    #publisher = '';
    #publicationDate = null; // Date.
    #isbn = 'dummy-123';
    #description = '';
    #overview = '';
    #numberOfPages = 0;
    #language = '';
    #isAvailable = true;
    #stock = 0;
    #price = 0;
    #discountedPercentage = 0;
    #images = []; // Array of BookImage objects
    #copiesSold = 0;

    constructor() {
    }

    // Getters and Setters for bookID
    get bookID() {
        return this.#bookID;
    }

    set bookID(value) {
        if (!DataValidator.isIDValid(value)) throw new Error('Invalid Book ID!');
        this.#bookID = value;
    }

    // Getters and Setters for title
    get title() {
        return this.#title;
    }

    set title(value) {
        this.#title = value;
    }

    // Getters and Setters for author
    get author() {
        return this.#author;
    }

    set author(value) {
        this.#author = value;
    }

    // Getters and Setters for genre
    get genre() {
        return this.#genre;
    }

    set genre(value) {
        this.#genre = value;
    }

    // Getters and Setters for publisher
    get publisher() {
        return this.#publisher;
    }

    set publisher(value) {
        this.#publisher = value;
    }

    // Getters and Setters for publicationDate
    get publicationDate() {
        return this.#publicationDate;
    }

    set publicationDate(value) {
        if (!DataValidator.isDateValid(value)) throw new Error('Invalid publication date!');
        this.#publicationDate = value;
    }

    // Getters and Setters for isbn
    get isbn() {
        return this.#isbn;
    }

    set isbn(value) {
        this.#isbn = value;
    }

    // Getters and Setters for description
    get description() {
        return this.#description;
    }

    set description(value) {
        this.#description = value;
    }

    // Getters and Setters for overview
    get overview() {
        return this.#overview;
    }

    set overview(value) {
        this.#overview = value;
    }

    // Getters and Setters for numberOfPages
    get numberOfPages() {
        return this.#numberOfPages;
    }

    set numberOfPages(value) {
        if (!DataValidator.isNonNegativeInteger(value)) {
            throw new Error('Number of pages must be a non-negative number!');
        }
        this.#numberOfPages = value;
    }

    // Getters and Setters for language
    get language() {
        return this.#language;
    }

    set language(value) {
        this.#language = value;
    }

    // Getters and Setters for isAvailable
    get isAvailable() {
        return this.#isAvailable;
    }

    set isAvailable(value) {
        if (typeof value !== 'boolean') {
            throw new Error('isAvailable must be a boolean!');
        }
        this.#isAvailable = value;
    }

    // Getters and Setters for stock
    get stock() {
        return this.#stock;
    }

    set stock(value) {
        if (!DataValidator.isNonNegativeInteger(value)) {
            throw new Error('Stock must be a non-negative integer!');
        }
        this.#stock = value;
    }

    // Getters and Setters for price
    get price() {
        return this.#price;
    }

    set price(value) {
        if (!DataValidator.isNonNegativeFloat(value)) {
            throw new Error('Price must be a non-negative number!');
        }
        this.#price = value;
    }

    // Getters and Setters for discountedPercentage
    get discountedPercentage() {
        return this.#discountedPercentage;
    }

    set discountedPercentage(value) {
        if (!DataValidator.isNonNegativeFloat(value)) {
            throw new Error('Discount percentage must be a non-negative number!');
        }
        this.#discountedPercentage = value;
    }

    // Getters and Setters for images
    get images() {
        return this.#images;
    }

    set images(value) {
        if (!Array.isArray(value)) {
            throw new Error('Images must be an array!');
        }
        this.#images = value;
    }

    // Getters and Setters for copiesSold
    get copiesSold() {
        return this.#copiesSold;
    }

    set copiesSold(value) {
        if (!DataValidator.isNonNegativeInteger(value)) {
            throw new Error('Copies sold must be a non-negative integer!');
        }
        this.#copiesSold = value;
    }

    toJSON() {
        return {
            bookID: this.#bookID,
            title: this.#title,
            author: this.#author,
            genre: this.#genre,
            publisher: this.#publisher,
            publicationDate: this.#publicationDate,
            isbn: this.#isbn,
            description: this.#description,
            overview: this.#overview,
            numberOfPages: this.#numberOfPages,
            language: this.#language,
            isAvailable: this.#isAvailable,
            stock: this.#stock,
            price: this.#price,
            discountedPercentage: this.#discountedPercentage,
            images: this.#images.map(img => img.toJSON()),
            copiesSold: this.#copiesSold,
        };
    }

    static fromJSON(json) {
        if (typeof json !== 'object' || json === null) {
            throw new Error('Invalid JSON object!');
        }

        const {
            bookID,
            title,
            author,
            genre,
            publisher,
            publicationDate,
            isbn,
            description,
            overview,
            numberOfPages,
            language,
            isAvailable,
            stock,
            price,
            discountedPercentage,
            images,
            copiesSold,
        } = json;

        if (!DataValidator.isIDValid(bookID)) {
            throw new Error('Invalid Book ID!');
        }
        if (typeof title !== 'string' || title.trim() === '') {
            throw new Error('Invalid title!');
        }
        if (typeof author !== 'string') {
            throw new Error('Invalid author!');
        }
        if (typeof genre !== 'string') {
            throw new Error('Invalid genre!');
        }
        if (typeof publisher !== 'string') {
            throw new Error('Invalid publisher!');
        }
        if (publicationDate !== null && isNaN(new Date(publicationDate).getTime())) {
            throw new Error('Invalid publication date!');
        }
        if (typeof isbn !== 'string') {
            throw new Error('Invalid ISBN!');
        }
        if (typeof description !== 'string') {
            throw new Error('Invalid description!');
        }
        if (typeof overview !== 'string') {
            throw new Error('Invalid overview!');
        }
        if (!DataValidator.isNonNegativeInteger(numberOfPages)) {
            throw new Error('Invalid number of pages!');
        }
        if (typeof language !== 'string') {
            throw new Error('Invalid language!');
        }
        if (typeof isAvailable !== 'boolean') {
            throw new Error('Invalid isAvailable value!');
        }
        if (!DataValidator.isNonNegativeInteger(stock)) {
            throw new Error('Invalid stock!');
        }
        if (!DataValidator.isNonNegativeFloat(price)) {
            throw new Error('Invalid price!');
        }
        if (!DataValidator.isNonNegativeFloat(discountedPercentage)) {
            throw new Error('Invalid discounted percentage!');
        }
        if (!Array.isArray(images)) {
            throw new Error('Invalid images!');
        }
        if (!DataValidator.isNonNegativeInteger(copiesSold)) {
            throw new Error('Invalid copies sold!');
        }
        
        const book = new Book();
        book.bookID = bookID || -1;
        book.title = title || '';
        book.author = json.author || '';
        book.genre = json.genre || Genres.UNSPECIFIED;
        book.publisher = json.publisher || '';
        book.publicationDate = json.publicationDate || null;
        book.isbn = json.isbn || 'dummy-123';
        book.description = json.description || '';
        book.overview = json.overview || '';
        book.numberOfPages = json.numberOfPages || 0;
        book.language = json.language || '';
        book.isAvailable = json.isAvailable !== undefined ? json.isAvailable : true;
        book.stock = json.stock || 0;
        book.price = json.price || 0;
        book.discountedPercentage = json.discountedPercentage || 0;
        book.images = (json.images || []).map(imgJson => BookImage.fromJSON(imgJson));
        book.copiesSold = json.copiesSold || 0;
        return book;
    }
}