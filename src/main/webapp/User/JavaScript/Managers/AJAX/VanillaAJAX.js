import serverContextPath from "./ServerContextPath.js";
import { AjaxRequestError, AjaxRequestErrorType } from './AJAXError.js'

class VanillaAJAX {
    #baseURL = serverContextPath;

    constructor(newURL) {
        if (newURL && typeof newURL === 'string' && newURL.length > 0) {
            this.#baseURL = newURL;
        }
    }

    request(method, endpoint, data = null, headers = {}) {
        return new Promise((resolve, reject) => {
            let ajaxRequest;
            if (XMLHttpRequest) {
                ajaxRequest = new XMLHttpRequest();
            } else if (ActiveXObject) {
                ajaxRequest = new ActiveXObject('Microsoft.XMLHTTP');
            } else {
                reject(new AjaxRequestError(
                    AjaxRequestErrorType.NO_AJAX_SUPPORT,
                    'Browser does not support AJAX requests'
                ));
                return;
            }

            let url = `${this.#baseURL}${endpoint}`;
            ajaxRequest.open(method, url, true);

            // Set default headers
            ajaxRequest.setRequestHeader('Content-Type', 'application/json');

            // Add custom headers
            Object.entries(headers).forEach(([key, value]) => {
                ajaxRequest.setRequestHeader(key, `${value}`);
            });

            ajaxRequest.onload = () => {
                if (ajaxRequest.status >= 200 && ajaxRequest.status < 300) {
                    // Return the raw response text without parsing
                    resolve(ajaxRequest.responseText);
                } else {
                    reject(new AjaxRequestError(
                        AjaxRequestErrorType.HTTP_ERROR,
                        `HTTP error: ${ajaxRequest.status} - ${ajaxRequest.statusText}`
                    ));
                }
            };

            ajaxRequest.onerror = () => {
                console.error('Network error details:', ajaxRequest);
                reject(new AjaxRequestError(
                    AjaxRequestErrorType.NETWORK_ERROR,
                    `Network error: ${ajaxRequest.statusText}`
                ));
            };

            ajaxRequest.ontimeout = () => reject(new AjaxRequestError(
                AjaxRequestErrorType.TIMEOUT_ERROR,
                'Request timed out'
            ));

            // Send data
            ajaxRequest.send(data ? JSON.stringify(data) : null);
        });
    }

    get(endpoint, data, headers) {
        // For GET requests, if there's data, append it as query parameters
        let url = endpoint;
        if (data) {
            const queryParams = new URLSearchParams();
            for (const key in data) {
                if (data.hasOwnProperty(key)) {
                    queryParams.append(key, data[key]);
                }
            }
            const queryString = queryParams.toString();
            if (queryString) {
                url += (url.includes('?') ? '&' : '?') + queryString;
            }
        }

        return this.request('GET', url, null, headers);
    }

    post(endpoint, data, headers) {
        return this.request('POST', endpoint, data, headers);
    }
}

export default VanillaAJAX;