class MessagePopup {
    constructor() {
        this.errorContainer = null;
        this.messageText = null;
        this.closeButton = null;
        this.timeout = null;
        this.createComponent();
    }

    createComponent(isError = false) {
        // Create container
        this.errorContainer = document.createElement('div');
        this.errorContainer.classList.add('popup-message-container');

        // Add error class if needed
        if (isError) {
            this.errorContainer.classList.add('popup-message-error');
        } else {
            this.errorContainer.classList.add('popup-message-info');
        }

        // Create message text
        this.messageText = document.createElement('p');
        this.messageText.classList.add('popup-message-text');

        // Create close button
        this.closeButton = document.createElement('button');
        this.closeButton.classList.add('popup-close-button');
        this.closeButton.innerHTML = '&times;';
        this.closeButton.addEventListener('click', () => this.hide());

        // Append elements
        this.errorContainer.appendChild(this.messageText);
        this.errorContainer.appendChild(this.closeButton);
        document.body.appendChild(this.errorContainer);
    }

    show(message, isError = false, duration = 3000) {
        // If container doesn't exist or needs to change type, recreate it
        if (!this.errorContainer ||
            (isError && !this.errorContainer.classList.contains('popup-message-error')) ||
            (!isError && !this.errorContainer.classList.contains('popup-message-info'))) {
            if (this.errorContainer) {
                this.errorContainer.remove();
            }
            this.createComponent(isError);
        }

        this.messageText.textContent = message;
        this.errorContainer.classList.add('show');

        // Only auto-hide if duration is provided and > 0
        if (duration && duration > 0) {
            this.timeout = setTimeout(() => this.hide(), duration);
        } else {
            clearTimeout(this.timeout);
        }
    }

    hide() {
        if (this.errorContainer) {
            this.errorContainer.classList.remove('show');
        }
        clearTimeout(this.timeout);
    }
}

export default new MessagePopup();