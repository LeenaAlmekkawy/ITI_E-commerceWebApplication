'use strict';

class AjaxRequestError {
    #errorType
    #errorMessage

    constructor(errorType, errorMessage) {
        this.#errorType = errorType;
        this.#errorMessage = errorMessage;
    }

    get errorType() {
        return this.#errorType;
    }

    get errorMessage() {
        return this.#errorMessage;
    }
}

const AjaxRequestErrorType = Object.freeze({
    NETWORK_ERROR: 'Network Error',
    TIMEOUT_ERROR: 'Request Timeout',
    PARSING_ERROR: 'Response Parsing Error',
    HTTP_ERROR: 'HTTP Error',
    NO_AJAX_SUPPORT: 'No AJAX Support'
})

export { AjaxRequestError, AjaxRequestErrorType };