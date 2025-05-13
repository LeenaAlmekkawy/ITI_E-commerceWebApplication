'use strict';

import MessagePopup from "./MessagePopup.js";

const checkForErrorMessageParameter = function() {
    // Check if the errorMessage parameter exists in the URL
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('errorMessage')) {
        const errorMessage = urlParams.get('errorMessage');
        MessagePopup.show(errorMessage, true);
    }
}

export default checkForErrorMessageParameter;