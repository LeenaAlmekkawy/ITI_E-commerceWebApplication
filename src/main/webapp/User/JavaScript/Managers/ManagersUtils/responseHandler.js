'use strict';

import parseResponse from "./parsingHandler.js";
import UserAuthTracker from "../../Common/UserAuthTracker.js";

export default function handleResponse(response) {
    const jsonResponse = parseResponse(response);

    if (jsonResponse !== null && !jsonResponse.success
    &&  (jsonResponse.data === 'UNAUTHENTICATED'
    ||  jsonResponse.data === 'You must login in first!')) {
        UserAuthTracker.handleUserInvalidState();
    }

    return jsonResponse;
}