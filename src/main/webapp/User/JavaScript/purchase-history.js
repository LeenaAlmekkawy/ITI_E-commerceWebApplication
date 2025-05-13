'use strict';

import checkForErrorMessageParameter from "./Common/checkForError.js";
import URL_Mapper from './Utils/URL_Mapper.js';
import UserAuthTracker from "./Common/UserAuthTracker.js";
import PurchaseHistoryManager from "./Managers/PurchaseHistoryManager.js";
import HistoryItem from "./Models/HistoryItem.js";
import MessagePopup from "./Common/MessagePopup.js";
import LoadingOverlay from "./Common/LoadingOverlay.js";

document.addEventListener('DOMContentLoaded', async function () {
    checkForErrorMessageParameter();

    // DOM Elements
    const elements = {
        historyList: document.getElementById('historyList'),
        backToProfileButton: document.getElementById('backToProfile')
    };

    // Validate DOM elements
    if (!elements.historyList) {
        console.error('Could not locate the history list component!');
        MessagePopup.show('Error: Page layout is broken.', true);
        return;
    }

    if (!elements.backToProfileButton) {
        console.error('Could not locate the backToProfile button.');
    }

    // Check user authentication
    const userObject = UserAuthTracker.userObject;
    if (!userObject) {
        UserAuthTracker.handleUserInvalidState();
        return;
    }

    // Initialize
    showLoadingState();
    setupEventListeners();
    await loadPurchaseHistory();

    function showLoadingState() {
        elements.historyList.innerHTML = '<div class="loading-data">Loading your purchase history...</div>';
    }

    function setupEventListeners() {
        if (elements.backToProfileButton) {
            elements.backToProfileButton.addEventListener('click', () => {
                window.location.href = URL_Mapper.PROFILE;
            });
        }
    }

    async function loadPurchaseHistory() {
        const loadingOverlay = new LoadingOverlay();
        loadingOverlay.createAndDisplay('Loading Purchase History...');

        try {
            const response = await PurchaseHistoryManager.getAllPurchaseHistory(userObject.userID);
            loadingOverlay.remove();

            if (!response) {
                showNoHistoryMessage();
                MessagePopup.show('Unknown error: Could not load purchase history.', true);
                return;
            }

            if (!response.success) {
                showNoHistoryMessage();
                MessagePopup.show(response.data || 'Failed to load purchase history.', true);
                return;
            }

            renderHistoryList(response.data);
        } catch (error) {
            loadingOverlay.remove();
            showNoHistoryMessage();
            console.error('Error loading purchase history:', error);
            MessagePopup.show('Failed to load purchase history due to a network error.', true);
        }
    }

    function renderHistoryList(historyItemList) {
        // Clear existing content
        elements.historyList.innerHTML = '';

        // Parse and filter valid items
        const parsedList = historyItemList
            .map(item => {
                try {
                    return HistoryItem.fromJSON(item);
                } catch (error) {
                    console.error('Could not parse a history item:', error);
                    return null;
                }
            })
            .filter(item => item !== null);

        if (parsedList.length === 0) {
            showNoHistoryMessage();
            return;
        }

        // Create and append history items
        parsedList.forEach(item => {
            elements.historyList.appendChild(createHistoryItemElement(item));
        });
    }

    function createHistoryItemElement(item) {
        const itemDiv = document.createElement('div');
        itemDiv.classList.add('history-item');

        const date = new Date(item.date);
        const formattedDate = date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        const formattedTime = date.toLocaleTimeString('en-US', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
        const totalPaid = item.totalPaid.toFixed(2);

        itemDiv.innerHTML = `
            <div class="history-details">
                <h3>Receipt #${item.itemID}</h3>
                <p><strong>Date:</strong> ${formattedDate}</p>
                <p><strong>Time:</strong> ${formattedTime}</p>
                <p><strong>Total:</strong> $${totalPaid}</p>
            </div>
            <button class="download-button">Download Receipt</button>
        `;

        const downloadButton = itemDiv.querySelector('.download-button');
        downloadButton.addEventListener('click', () => {
            if (item.receiptFileUrl) {
                window.open(item.receiptFileUrl, '_blank');
            } else {
                MessagePopup.show('Receipt file is unavailable.', true);
            }
        });

        return itemDiv;
    }

    function showNoHistoryMessage() {
        elements.historyList.innerHTML = '<div class="no-data-found">No purchase history found.</div>';
    }
});