'use strict';

import MessagePopup from "../../Common/MessagePopup.js";

export default function parseResponse(rawResponse) {
    try {
        const parsed = JSON.parse(rawResponse);

        // Validate required fields
        if (typeof parsed.success === 'boolean' &&
            typeof parsed.data !== 'undefined') {
            return parsed;
        }

        console.error("Invalid response structure:", parsed);
        MessagePopup.show('Server data parsing issue!', true);
        return null;

    } catch (error) {
        console.error("JSON parsing failed:", error);
        MessagePopup.show('Server data parsing issue!', true);
        return null;
    }
}