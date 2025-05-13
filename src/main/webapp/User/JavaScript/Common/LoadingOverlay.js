'use strict';

class LoadingOverlay {
    constructor() {
        this.overlay = null;
        this.messageElement = null;
    }

    createAndDisplay(message = 'Initial Loading...', parent = document.body) {
        // Create the overlay container
        this.overlay = document.createElement('div');
        this.overlay.className = 'loading-overlay';
        this.overlay.style.position = 'fixed';
        this.overlay.style.top = '0';
        this.overlay.style.left = '0';
        this.overlay.style.width = '100%';
        this.overlay.style.height = '100%';
        this.overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
        this.overlay.style.display = 'flex';
        this.overlay.style.justifyContent = 'center';
        this.overlay.style.alignItems = 'center';
        this.overlay.style.zIndex = '2000';

        // Create the content box
        this.contentBox = document.createElement('div');
        this.contentBox.className = 'loading-content-box';
        this.contentBox.style.backgroundColor = '#ffffff';
        this.contentBox.style.borderRadius = '10px';
        this.contentBox.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.3)';
        this.contentBox.style.padding = '30px';
        this.contentBox.style.display = 'flex';
        this.contentBox.style.flexDirection = 'column';
        this.contentBox.style.alignItems = 'center';
        this.contentBox.style.maxWidth = '80%';

        // Create the loading spinner
        this.spinner = document.createElement('div');
        this.spinner.className = 'loading-spinner';
        this.spinner.style.width = '60px';
        this.spinner.style.height = '60px';
        this.spinner.style.border = '5px solid #f3f3f3';
        this.spinner.style.borderTop = '5px solid #3498db';
        this.spinner.style.borderRadius = '50%';
        this.spinner.style.marginBottom = '20px';

        // Add the spinning animation
        this.spinner.style.animation = 'spin 2s linear infinite';

        // Create keyframes for the spinner animation
        const style = document.createElement('style');
        style.textContent = `
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    `;
        document.head.appendChild(style);

        // Create the message element
        this.messageElement = document.createElement('div');
        this.messageElement.className = 'loading-message';
        this.messageElement.style.color = '#333333';
        this.messageElement.style.fontSize = '22px';
        this.messageElement.style.fontWeight = '500';
        this.messageElement.style.textAlign = 'center';
        this.messageElement.style.fontFamily = 'Arial, sans-serif';
        this.messageElement.textContent = message;

        // Append elements to the content box
        this.contentBox.appendChild(this.spinner);
        this.contentBox.appendChild(this.messageElement);

        // Append the content box to the overlay
        this.overlay.appendChild(this.contentBox);

        // Append the overlay to the parent element
        parent.appendChild(this.overlay);

        // Return the reference to the overlay instance
        return this;
    }

    updateMessage(message) {
        if (this.messageElement) {
            this.messageElement.textContent = message;
        }
    }

    remove() {
        if (this.overlay && this.overlay.parentNode) {
            this.overlay.parentNode.removeChild(this.overlay);
        }
    }
}

export default LoadingOverlay;
