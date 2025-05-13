import checkForErrorMessageParameter from "./Common/checkForError.js"
import URL_Mapper from "./Utils/URL_Mapper.js"
import OrdersManager from "./Managers/OrdersManager.js"
import UserAuthTracker from "./Common/UserAuthTracker.js"
import Order from "./Models/Order.js"

import { addOrderDateTimeAddress } from "./Utils/UICommonFunctions.js"
import MessagePopup from "./Common/MessagePopup.js"
import LoadingOverlay from "./Common/LoadingOverlay.js"

document.addEventListener("DOMContentLoaded", async () => {
  checkForErrorMessageParameter()

  const backToProfileButton = document.getElementById("backToProfile")
  if (!backToProfileButton) {
    console.log("Could not locate the back to profile button.")
  } else {
    backToProfileButton.addEventListener("click", () => {
      window.location.href = URL_Mapper.PROFILE
    })
  }

  const ordersList = document.getElementById("ordersList")
  if (!ordersList) {
    console.error("Could not locate the orders list component!")
    return
  }

  ordersList.innerHTML = '<div class="loading-data">Loading your Orders...</div>'

  const userObject = UserAuthTracker.userObject
  if (!userObject) {
    UserAuthTracker.handleUserInvalidState()
    return
  }

  const loadingOverlay = new LoadingOverlay()
  loadingOverlay.createAndDisplay("Loading Orders List...")

  const response = await OrdersManager.getOrdersList(userObject.userID)
  loadingOverlay.remove()

  if (!response) {
    MessagePopup.show("Unknown error, could not load the list of orders!")
    return
  }

  if (!response.success) {
    MessagePopup.show(response.data)
    return
  }

  renderOrders(response.data)

  function renderOrders(orders) {
    const parsedOrders = orders
      .map((order) => {
        try {
          return Order.fromJSON(order)
        } catch (_) {
          console.error("Could not parse an order from the orders list!")
          return null
        }
      })
      .filter((order) => order !== null)

    ordersList.innerHTML = ""

    if (parsedOrders.length === 0) {
      ordersList.innerHTML = '<div class="no-data-found">No orders found.</div>'
    }

    parsedOrders.forEach((order) => {
      const orderItem = document.createElement("div")
      orderItem.classList.add("order-item")

      // Create the order details container
      const orderDetails = document.createElement("div")
      orderDetails.className = "order-details"

      const orderIdHeading = document.createElement("h2")
      orderIdHeading.textContent = `Order #${order.orderID}`
      orderDetails.appendChild(orderIdHeading)

      addOrderDateTimeAddress(orderDetails, order)

      const orderItems = order.orderItems
      let numberOfDifferentBooks = "Unknown"
      if (orderItems) {
        numberOfDifferentBooks = orderItems.length
      }
      const differentBooksCountParagraph = document.createElement("p")
      differentBooksCountParagraph.innerHTML = `
                <strong>Number of Different Books:</strong> ${numberOfDifferentBooks}
            `
      orderDetails.appendChild(differentBooksCountParagraph)

      // Append the order details container to the order item
      orderItem.appendChild(orderDetails)

      // Add order status badge
      if (order.status) {
        const statusElement = document.createElement("div")
        statusElement.className = `order-status ${order.status.toLowerCase()}`
        statusElement.textContent = order.status
        orderItem.appendChild(statusElement)
      }

      // Create and append the view order button
      const viewOrderButton = document.createElement("button")
      viewOrderButton.className = "view-order-button"
      viewOrderButton.textContent = "View Order"
      orderItem.appendChild(viewOrderButton)

      // Add event listener to the View Order button
      viewOrderButton.addEventListener("click", () => {
        window.location.href = URL_Mapper.ORDER_DETAILS + `?orderID=${order.orderID}`
      })

      ordersList.appendChild(orderItem)
    })
  }
})
