'use strict';

const URL_Mapper = Object.freeze({
    // HTML Pages
    WELCOME: `${contextPath}/welcome.html`,
    PRODUCTS: `${contextPath}/products.html`,
    PROFILE: `${contextPath}/profile.html`,
    WISH_LIST: `${contextPath}/wish-list.html`,
    ORDERS: `${contextPath}/orders.html`,
    ORDER_DETAILS: `${contextPath}/order-details.html`,
    PURCHASE_HISTORY: `${contextPath}/purchase-history.html`,
    CART: `${contextPath}/cart.html`,
    CHECKOUT: `${contextPath}/checkout.html`,
    ABOUT: `${contextPath}/About/about.html`,
    ERROR: `${contextPath}/Error/error.html`,

    // Assets
    ASSETS: Object.freeze({
        LOGO: `${contextPath}/Assets/logo.jpeg`,
        FALLBACK_BOOK_IMAGE: `${contextPath}/Assets/fallback_book_image.jpg`,
    }),

    ICONS: Object.freeze({
        CART: `${contextPath}/Assets/Icons/cart-icon.svg`,
        SEARCH: `${contextPath}/Assets/Icons/search-icon.svg`,
        USER: `${contextPath}/Assets/Icons/user-icon.svg`,
        TRASH_CAN_COVERED: `${contextPath}/Assets/Icons/trash-can-icon-covered.svg`,
        TRASH_CAN_UNCOVERED: `${contextPath}/Assets/Icons/trash-can-icon-uncovered.svg`,
        WISH_LIST: `${contextPath}/Assets/Icons/heart-flat-icon.svg`
    })
});

export default URL_Mapper;
