'use strict';
import { ErrorHandler } from './Modules/ErrorHandler.js';

window.addEventListener('load', () => {
    const errorDiv = document.querySelector('.error-div');
    let errorHandler = null;
    if (errorDiv) {
        errorHandler = new ErrorHandler(errorDiv);
    }
    
    const form = document.getElementById('form');
    const adminIDInput = document.getElementById('adminID');
    const passwordInput = document.getElementById('password');
    
    const urlParams = new URLSearchParams(window.location.search);
    const errorMessage = urlParams.get("errorMessage");
    if (errorMessage && errorDiv && errorHandler) {
        errorHandler.putError(errorMessage);
    }
    
    let isElementsRead = true;
    if (!form || !errorDiv
    || !adminIDInput || !passwordInput) {
        console.log('Could not load some of the form inputs.');
        isElementsRead = false;
    }
    
    if (form) {
        form.addEventListener('submit', handleSubmit);
    }
    
    function handleSubmit(event) {
        if (!isElementsRead || !errorHandler) {
            console.log('Form will not submit.');
            event.preventDefault();
        }
        errorHandler.clearError();
        
        const adminID = adminIDInput.value;
        const password = passwordInput.value;
        
        if (adminID === '') {
            errorHandler.putError('ID Required!');
            event.preventDefault();
            return;
        }
        
        if (isNaN(parseInt(adminID))) {
            errorHandler.putError('ID must be an Integer!');
            event.preventDefault();
            return;
        }
        
        if (password === '') {
            errorHandler.putError('Password Required!');
            event.preventDefault();
            return;
        }
    }
});