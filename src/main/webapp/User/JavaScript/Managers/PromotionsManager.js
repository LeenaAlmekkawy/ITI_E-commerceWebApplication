'use strict';

import MessagePopup from "../Common/MessagePopup.js";
import handleResponse from "./ManagersUtils/responseHandler.js";

import VanillaAJAX from "./AJAX/VanillaAJAX.js";
import ServerURLMapper from "./AJAX/ServerURLMapper.js";

// Create an instance of VanillaAJAX to use for all requests.
const ajaxClient = new VanillaAJAX();

const PromotionsManager = {
    async getBannersList() {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetEventBannersList, '{}');
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('event banners list request failed:', error);
            MessagePopup.show('Unknown error while loading banners!', true);
        }
    }
}

export default PromotionsManager;