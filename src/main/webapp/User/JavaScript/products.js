'use strict';

import checkForErrorMessageParameter from "./Common/checkForError.js";
import BooksManager from './Managers/BooksManager.js';
import Genres from './Models/Genres.js';
import MessagePopup from "./Common/MessagePopup.js";
import { createBookCard } from './Utils/bookUIFunctions.js';
import { parseBooksFromData } from "./Utils/UICommonFunctions.js";
import LoadingOverlay from "./Common/LoadingOverlay.js";

document.addEventListener("DOMContentLoaded", async function() {
    checkForErrorMessageParameter();

    // Constants
    const BOOKS_PER_PAGE = 12;

    // DOM Elements
    const elements = {
        booksList: document.getElementById('booksList'),
        searchBar: document.getElementById('searchBar'),
        searchButton: document.getElementById('searchButton'),
        unavailableToggle: document.getElementById('unavailableToggle'),
        genreFilter: document.getElementById('genreFilter'),
        prevPageButton: document.getElementById('prevPage'),
        nextPageButton: document.getElementById('nextPage'),
        pageInput: document.getElementById('pageInput'),
        pageIndicator: document.getElementById('pageIndicator')
    };

    // Validate DOM elements
    const missingElements = Object.entries(elements)
        .filter(([key, value]) => !value)
        .map(([key]) => key);
    if (missingElements.length > 0) {
        console.error(`Missing DOM elements: ${missingElements.join(", ")}`);
        MessagePopup.show("Error: Page layout is broken. Some components are missing.", true);
        return;
    }

    // State variables
    let allBooksPossiblySearched = null;
    const currentDisplayedMap = new Map();
    let currentPage = 1;
    let totalPages = 1;

    // Initialize
    populateGenreFilter();
    await loadInitialBooks();

    function populateGenreFilter() {
        // Clear existing options
        elements.genreFilter.innerHTML = '';

        // Add "All Genres" option with value 'all'
        const allGenresOption = document.createElement('option');
        allGenresOption.value = 'all';
        allGenresOption.textContent = 'All Genres';
        elements.genreFilter.appendChild(allGenresOption);

        // Add other genres, excluding "Unspecified"
        const addedGenres = new Set(['All Genres']); // Track added genres to avoid duplicates
        Object.values(Genres).forEach(genre => {
            if (genre === "Unspecified" || genre === "All Genres") return; // Skip "Unspecified" and "All Genres"
            if (addedGenres.has(genre)) return; // Skip duplicates
            addedGenres.add(genre);

            const option = document.createElement('option');
            option.value = genre;
            option.textContent = genre;
            elements.genreFilter.appendChild(option);
        });

        // Set "All Genres" as default
        allGenresOption.selected = true;
    }

    function handleIncomingResponse(response) {
        if (!response) {
            MessagePopup.show("Unknown error: Could not load books.", true);
            return;
        }
        if (!response.success) {
            MessagePopup.show(response.data || "Failed to load books.", true);
            return;
        }

        allBooksPossiblySearched = parseBooksFromData(response.data);
        filterBooks();
        renderBooks();
    }

    async function loadInitialBooks() {
        const loadingOverlay = new LoadingOverlay();
        loadingOverlay.createAndDisplay('Loading Books List...');

        const response = await BooksManager.getAllBooks();
        loadingOverlay.remove();
        handleIncomingResponse(response);
    }

    function filterBooks() {
        currentDisplayedMap.clear();
        const { genreFilter, unavailableToggle } = elements;
        const selectedGenre = genreFilter.value;

        allBooksPossiblySearched?.forEach(book => {
            if (selectedGenre !== 'all' && book.genre !== selectedGenre) return;
            if (unavailableToggle && !unavailableToggle.checked && (book.stock === 0 || !book.isAvailable)) return;
            currentDisplayedMap.set(book.bookID, book);
        });

        currentPage = 1;
        updatePagination();
    }

    function renderBooks() {
        elements.booksList.innerHTML = '';

        if (currentDisplayedMap.size === 0) {
            const emptyElement = document.createElement('div');
            emptyElement.textContent = 'No books found matching your criteria!';
            emptyElement.classList.add('no-data-found');
            elements.booksList.appendChild(emptyElement);

            currentPage = 0;
            totalPages = 0;
            updatePagination();
            return;
        }

        const booksArray = Array.from(currentDisplayedMap.values());
        totalPages = Math.ceil(booksArray.length / BOOKS_PER_PAGE);
        updatePagination();

        const startIndex = (currentPage - 1) * BOOKS_PER_PAGE;
        const booksToDisplay = booksArray.slice(startIndex, startIndex + BOOKS_PER_PAGE);

        booksToDisplay.forEach(book => {
            const bookCard = createBookCard(book);
            elements.booksList.appendChild(bookCard);
        });

        // Scroll to the top of the books list
        elements.booksList.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    function updatePagination() {
        const { pageIndicator, pageInput, prevPageButton, nextPageButton } = elements;
        pageIndicator.textContent = `Page ${currentPage} of ${totalPages}`;
        pageInput.value = currentPage;
        prevPageButton.disabled = currentPage <= 1;
        nextPageButton.disabled = currentPage >= totalPages;
    }

    function goToPage(page) {
        const pageNum = parseInt(page);
        if (isNaN(pageNum)) return;

        currentPage = Math.max(1, Math.min(pageNum, totalPages));
        renderBooks();
    }

    // Event Listeners
    elements.searchBar.addEventListener('input', () => {
        const searchBarValue = elements.searchBar.value.trim().toLowerCase();
        if (searchBarValue === '') {
            elements.searchButton.value = 'Refresh';
        } else {
            elements.searchButton.value = 'Search';
        }
    });

    elements.searchButton.addEventListener('click', async (e) => {
        e.preventDefault();
        const searchingLoadingOverlay = new LoadingOverlay();
        searchingLoadingOverlay.createAndDisplay('Searching...');

        const searchTerm = elements.searchBar.value.trim().toLowerCase();
        if (!searchTerm) {
            searchingLoadingOverlay.updateMessage('Loading Books List...');
            await loadInitialBooks();
            searchingLoadingOverlay.remove();
            return;
        }

        const response = await BooksManager.search(searchTerm);
        searchingLoadingOverlay.remove();

        handleIncomingResponse(response);
    });

    elements.unavailableToggle.addEventListener("change", () => {
        filterBooks();
        renderBooks();
    });

    elements.genreFilter.addEventListener("change", () => {
        filterBooks();
        renderBooks();
    });

    elements.prevPageButton.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            renderBooks();
        }
    });

    elements.nextPageButton.addEventListener('click', () => {
        if (currentPage < totalPages) {
            currentPage++;
            renderBooks();
        }
    });

    elements.pageInput.addEventListener('change', (e) => goToPage(e.target.value));
    elements.pageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') goToPage(e.target.value);
    });

    // Prevent any global click handlers from interfering with book cards
    document.addEventListener('click', (e) => {
        // Only log for debugging purposes; no action needed
        console.log("Global click event captured on:", e.target);
    }, { capture: true });
});