'use strict';
import { Mapper } from './Mapper.js';

export class RedirectButtons {
    static monitorLogoutButton() {
        const logoutButton = document.getElementById('logoutButton');
        if (!logoutButton) {
            console.log('Could not locate the logout button');
        } else {
            logoutButton.addEventListener('click', () => {
                console.log("logout button");
                window.location.href = contextPath + Mapper.LOGOUT_SERVICE;
            });
        }
    }
}

