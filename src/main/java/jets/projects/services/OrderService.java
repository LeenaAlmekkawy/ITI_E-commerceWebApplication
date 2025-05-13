package jets.projects.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jets.projects.client_dto.ClientOrderDto;
import jets.projects.dao.BookDao;
import jets.projects.dao.CartItemDao;
import jets.projects.dao.OrderDao;
import jets.projects.dao.PurchaseHistoryDao;
import jets.projects.dao.UserDao;
import jets.projects.client_dto.CreditCardDetailsDto;
import jets.projects.client_dto.OrderDto;
import jets.projects.entity.Book;
import jets.projects.entity.BookOrder;
import jets.projects.entity.CartItem;
import jets.projects.entity.OrderItem;
import jets.projects.entity.PaymentMethod;
import jets.projects.entity.PurchaseHistory;
import jets.projects.entity.Status;
import jets.projects.entity.User;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OperationFailedException;
import jets.projects.exceptions.OutOfStockException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.File;

public class OrderService {
    private final OrderDao orderDao;
    private final CartItemDao cartItemDao;
    private final CartService cartService;
    private final BookDao bookDao;
    private final UserDao userDao;
    private final PurchaseHistoryDao purchaseHistoryDao;
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$");
    private static final Pattern CVC_PATTERN = Pattern.compile("^\\d{3}$");
    private static final BigDecimal FIXED_SHIPPING_COST = new BigDecimal("28.00");
    private static final String RECEIPT_DIR = "C:\\Users\\ibrah\\OneDrive\\Desktop\\Tools\\apache-tomcat-10.1.39\\webapps\\ITI-Jets-EcommApplication\\receipts\\";
    private static final String RECEIPT_URL_PREFIX = "/ITI-Jets-EcommApplication/receipts/";

    public OrderService() {
        this.orderDao = new OrderDao();
        this.cartService = new CartService();
        this.bookDao = new BookDao();
        this.userDao = new UserDao();
        this.cartItemDao = new CartItemDao();
        this.purchaseHistoryDao = new PurchaseHistoryDao();
        new File(RECEIPT_DIR).mkdirs();
    }

    private OrderDto convertToDto(BookOrder order) {
        OrderDto dto = new OrderDto();
        dto.setId(String.valueOf(order.getOrderId()));
        dto.setDate(order.getOrderDate().toString());

        OrderDto.CustomerDto customer = new OrderDto.CustomerDto();
        customer.setName(order.getUser().getUsername());
        customer.setEmail(order.getUser().getEmail());
        customer.setPhone(order.getUser().getPhoneNumber());
        customer.setAddress(order.getAddress());
        dto.setCustomer(customer);

        List<OrderDto.OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    OrderDto.OrderItemDto itemDto = new OrderDto.OrderItemDto();
                    itemDto.setBook(item.getBook().getTitle());
                    itemDto.setPrice(item.getPrice());
                    itemDto.setQuantity(item.getQuantity().intValue());
                    itemDto.setTotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                    return itemDto;
                })
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        BigDecimal subtotal = itemDtos.stream()
                .map(OrderDto.OrderItemDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setSubtotal(subtotal);

        dto.setShipping(FIXED_SHIPPING_COST);

        BigDecimal total = subtotal.add(FIXED_SHIPPING_COST);
        dto.setTotal(total);

        dto.setStatus(order.getStatus().name());
        return dto;
    }

    public List<OrderDto> getOrders(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<BookOrder> orders = orderDao.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderDetails(Long userId, Long orderId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (orderId == null || orderId <= 0) {
            throw new InvalidInputException("Invalid order ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        BookOrder order = orderDao.findByUserIdAndOrderId(userId, orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId + " for user ID: " + userId));
        return convertToDto(order);
    }

    public Long checkoutWithBalance(Long userId, String address) throws InvalidInputException, NotFoundException, OutOfStockException, OperationFailedException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new InvalidInputException("Address cannot be empty");
        }

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<CartItem> cartItems = cartItemDao.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new NotFoundException("Cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Book book = item.getBook();
            if (!book.getIsAvailable() || book.getStock() < item.getQuantity()) {
                throw new OutOfStockException("Book ID " + book.getBookId() + " is out of stock or insufficient quantity");
            }
            BigDecimal itemTotal = book.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .multiply(BigDecimal.ONE.subtract(book.getDiscountPercentage().divide(BigDecimal.valueOf(100))));
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal total = subtotal.add(FIXED_SHIPPING_COST);

        if (user.getBalance().compareTo(total) < 0) {
            throw new OperationFailedException("Insufficient account balance");
        }

        BookOrder order = createOrder(user, cartItems, address, "ACCOUNT_BALANCE", FIXED_SHIPPING_COST);
        try {
            for (CartItem item : cartItems) {
                Book book = item.getBook();
                book.setStock(book.getStock() - item.getQuantity());
                book.setSoldCount(book.getSoldCount() + item.getQuantity());
                bookDao.update(book);
            }
            user.setBalance(user.getBalance().subtract(total));
            userDao.update(user);
            orderDao.save(order);
            cartService.truncateCart(userId);
            return order.getOrderId();
        } catch (Exception e) {
            throw new OperationFailedException("Checkout failed", e);
        }
    }

    public Long checkoutWithCreditCard(Long userId, CreditCardDetailsDto creditCardDetails, String address)
            throws InvalidInputException, NotFoundException, OutOfStockException, OperationFailedException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new InvalidInputException("Address cannot be empty");
        }
        validateCreditCardDetails(creditCardDetails);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<CartItem> cartItems = cartItemDao.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new NotFoundException("Cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Book book = item.getBook();
            if (!book.getIsAvailable() || book.getStock() < item.getQuantity()) {
                throw new OutOfStockException("Book ID " + book.getBookId() + " is out of stock or insufficient quantity");
            }
            BigDecimal itemTotal = book.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .multiply(BigDecimal.ONE.subtract(book.getDiscountPercentage().divide(BigDecimal.valueOf(100))));
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal total = subtotal.add(FIXED_SHIPPING_COST);

        BookOrder order = createOrder(user, cartItems, address, "CREDIT_CARD", FIXED_SHIPPING_COST);
        try {
            for (CartItem item : cartItems) {
                Book book = item.getBook();
                book.setStock(book.getStock() - item.getQuantity());
                book.setSoldCount(book.getSoldCount() + item.getQuantity());
                bookDao.update(book);
            }
            orderDao.save(order);
            cartService.truncateCart(userId);
            return order.getOrderId();
        } catch (Exception e) {
            throw new OperationFailedException("Checkout failed", e);
        }
    }

    private BookOrder createOrder(User user, List<CartItem> cartItems, String address, String paymentMethod, BigDecimal shippingFee) {
        BookOrder order = new BookOrder();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setAddress(address);
        PaymentMethod method = new PaymentMethod();
        method.setMethod(paymentMethod);
        order.setMethod(method);
        order.setShippingCost(shippingFee);
        order.setStatus(Status.PENDING);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(item.getBook());
                    orderItem.setQuantity((long) item.getQuantity());
                    orderItem.setPrice(item.getBook().getPrice()
                            .multiply(BigDecimal.ONE.subtract(item.getBook().getDiscountPercentage().divide(BigDecimal.valueOf(100)))));
                    orderItem.setDiscountPercentage(item.getBook().getDiscountPercentage());
                    return orderItem;
                })
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);
        return order;
    }

    private void validateCreditCardDetails(CreditCardDetailsDto details) throws InvalidInputException {
        if (details.getNameOnCard() == null || details.getNameOnCard().trim().isEmpty()) {
            throw new InvalidInputException("Name on card cannot be empty");
        }
        if (details.getCardNumber() == null || !CARD_NUMBER_PATTERN.matcher(details.getCardNumber()).matches()) {
            throw new InvalidInputException("Invalid card number format (expected: xxxx-xxxx-xxxx-xxxx)");
        }
        if (details.getExpiryDate() == null) {
            throw new InvalidInputException("Expiry date cannot be null");
        }
        LocalDate now = LocalDate.now();
        if (details.getExpiryDate().isBefore(now) || details.getExpiryDate().equals(now)) {
            throw new InvalidInputException("Expiry date must be in the future");
        }
        int month = details.getExpiryDate().getMonthValue();
        if (month < 1 || month > 12) {
            throw new InvalidInputException("Expiry month must be between 1 and 12");
        }
        if (details.getCvc() == null || !CVC_PATTERN.matcher(details.getCvc()).matches()) {
            throw new InvalidInputException("CVC must be a 3-digit number");
        }
    }

    public List<OrderDto> getAllOrders() {
        List<BookOrder> orders = orderDao.findAll();
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

//    private String generateReceiptPdf(BookOrder order, BigDecimal totalPaid) throws OperationFailedException {
//        String fileName = "receipt_" + order.getOrderId() + "_" + System.currentTimeMillis() + ".pdf";
//        String filePath = RECEIPT_DIR + fileName;
//        String urlPath = RECEIPT_URL_PREFIX + fileName;
//
//        try {
//            PdfWriter writer = new PdfWriter(filePath);
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf);
//
//            document.add(new Paragraph("Receipt for Order #" + order.getOrderId()));
//            document.add(new Paragraph("Customer: " + order.getUser().getUsername()));
//            document.add(new Paragraph("Date: " + LocalDateTime.now()));
//            document.add(new Paragraph("Items:"));
//            for (OrderItem item : order.getOrderItems()) {
//                document.add(new Paragraph(
//                        item.getBook().getTitle() + " - Quantity: " + item.getQuantity() +
//                                " - Price: $" + item.getPrice() +
//                                " - Total: $" + item.getPrice().multiply(new BigDecimal(item.getQuantity()))
//                ));
//            }
//            document.add(new Paragraph("Subtotal: $" + totalPaid.subtract(FIXED_SHIPPING_COST)));
//            document.add(new Paragraph("Shipping: $" + FIXED_SHIPPING_COST));
//            document.add(new Paragraph("Total Paid: $" + totalPaid));
//
//            document.close();
//            return urlPath;
//        } catch (Exception e) {
//            throw new OperationFailedException("Failed to generate PDF for order ID: " + order.getOrderId(), e);
//        }
//    }

    public boolean updateOrderStatus(Long orderId, String status) throws InvalidInputException, NotFoundException, OperationFailedException {
        if (orderId == null || orderId <= 0) {
            throw new InvalidInputException("Invalid order ID");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidInputException("Status cannot be empty");
        }

        BookOrder order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        try {
            Status newStatus;
            try {
                String normalizedStatus = status.trim().toUpperCase();
                if ("CANCELED".equals(normalizedStatus)) {
                    normalizedStatus = "CANCELLED";
                }
                newStatus = Status.valueOf(normalizedStatus);
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid status: " + status);
            }

            order.setStatus(newStatus);
            System.out.println("Order ID: " + orderId + ", Setting status: " + newStatus);

            if ("DELIVERED".equalsIgnoreCase(status)) {
                User user = order.getUser();
                if (user == null) {
                    throw new OperationFailedException("User not associated with order ID: " + orderId);
                }
                BigDecimal subtotal = order.getOrderItems().stream()
                        .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (subtotal == null) {
                    throw new OperationFailedException("Unable to calculate subtotal for order ID: " + orderId);
                }
                BigDecimal totalPaid = subtotal.add(FIXED_SHIPPING_COST);

                PurchaseHistory purchase = new PurchaseHistory();
                purchase.setUser(user);
                purchase.setPurchaseDatetime(LocalDateTime.now());
                purchase.setTotalPaid(totalPaid);
                String receiptUrl = generateReceiptPdf(order, totalPaid);
                purchase.setReceiptFileUrl(receiptUrl);
                System.out.println("Saving purchase history for order ID: " + orderId + ", Receipt URL: " + receiptUrl);
                try {
                    purchaseHistoryDao.save(purchase);
                } catch (Exception e) {
                    throw new OperationFailedException("Failed to save purchase history for order ID: " + orderId, e);
                }

                System.out.println("Deleting order ID: " + orderId);
                try {
                    orderDao.deleteById(orderId);
                } catch (Exception e) {
                    throw new OperationFailedException("Failed to delete order ID: " + orderId, e);
                }
            } else {
                System.out.println("Updating order status to " + newStatus + " for order ID: " + orderId);
                try {
                    orderDao.update(order);
                } catch (Exception e) {
                    throw new OperationFailedException("Failed to update order status to " + newStatus + " for order ID: " + orderId, e);
                }
            }
            return true;
        } catch (Exception e) {
            throw new OperationFailedException("Failed to process status update for order ID: " + orderId + ": " + e.getMessage(), e);
        }
    }

    private ClientOrderDto convertToClientDto(BookOrder order) {
        ClientOrderDto dto = new ClientOrderDto();
        dto.setOrderID(order.getOrderId());
        dto.setUserID(order.getUser().getUserId());
        dto.setDate(order.getOrderDate());
        dto.setAddress(order.getAddress());
        dto.setPaymentMethod(order.getMethod().getMethod());
        dto.setShippingFee(order.getShippingCost());
        dto.setStatus(order.getStatus().name());

        List<ClientOrderDto.OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    ClientOrderDto.OrderItemDto itemDto = new ClientOrderDto.OrderItemDto();
                    itemDto.setBookID(item.getBook().getBookId());
                    itemDto.setQuantity(item.getQuantity().intValue());
                    itemDto.setPriceAtPurchase(item.getPrice());
                    return itemDto;
                })
                .collect(Collectors.toList());
        dto.setOrderItems(itemDtos);

        return dto;
    }

    public ClientOrderDto getOrderDetailsForClient(Long userId, Long orderId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (orderId == null || orderId <= 0) {
            throw new InvalidInputException("Invalid order ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        BookOrder order = orderDao.findByUserIdAndOrderId(userId, orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId + " for user ID: " + userId));
        return convertToClientDto(order);
    }

    public List<ClientOrderDto> getAllOrdersForClient(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<BookOrder> orders = orderDao.findByUserId(userId);
        return orders.stream()
                .map(this::convertToClientDto)
                .collect(Collectors.toList());
    }







    private String generateReceiptPdf(BookOrder order, BigDecimal totalPaid) throws OperationFailedException {
        // Generate unique filename with order ID and timestamp
        String fileName = String.format("receipt_%s_%d.pdf", order.getOrderId(), System.currentTimeMillis());
        String filePath = RECEIPT_DIR + fileName;
        String urlPath = RECEIPT_URL_PREFIX + fileName;

        try {
            // Initialize PDF document with A4 page size
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36); // 0.5 inch margins

            // Set up fonts and colors
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Color primaryColor = new DeviceRgb(41, 128, 185); // Professional blue
            Color secondaryColor = new DeviceRgb(52, 73, 94); // Dark slate

            // Company header and logo
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // Company info and logo
            Cell companyCell = new Cell();
            companyCell.setBorder(Border.NO_BORDER);

            // Add company logo
            try {
                ImageData logoData = ImageDataFactory.create(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("logo.jpeg")).readAllBytes());
                Image logo = new Image(logoData);
                logo.scaleToFit(50, 50); // Scale down the logo to fit within 50x50 dimensions
                companyCell.add(logo);
            } catch (Exception e) {
                System.out.println("Could not load company logo: {}"+ e.getMessage());
            }

            Paragraph companyName = new Paragraph("BOOK ALLEY")
                    .setFont(headerFont)
                    .setFontSize(18)
                    .setFontColor(primaryColor);
            Paragraph companyDetails = new Paragraph("Smart village, Cairo, Egypt\nEmail: support@bookalley.com\nPhone: 01063253263")
                    .setFont(regularFont)
                    .setFontSize(10);
            companyCell.add(companyName);
            companyCell.add(companyDetails);
            headerTable.addCell(companyCell);

            // Receipt title
            Cell receiptTitleCell = new Cell();
            receiptTitleCell.setBorder(Border.NO_BORDER);
            receiptTitleCell.setTextAlignment(TextAlignment.RIGHT);
            Paragraph receiptTitle = new Paragraph("RECEIPT")
                    .setFont(headerFont)
                    .setFontSize(24)
                    .setFontColor(secondaryColor);
            Paragraph receiptNumber = new Paragraph("Order #" + order.getOrderId())
                    .setFont(boldFont)
                    .setFontSize(12);
            receiptTitleCell.add(receiptTitle);
            receiptTitleCell.add(receiptNumber);
            headerTable.addCell(receiptTitleCell);

            document.add(headerTable);
            document.add(new Paragraph("\n"));

            // Customer and order info section
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            infoTable.setWidth(UnitValue.createPercentValue(100));

            // Customer info
            Cell customerCell = new Cell();
            customerCell.setBorder(Border.NO_BORDER);
            customerCell.add(new Paragraph("BILLED TO:")
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setFontColor(secondaryColor));
            customerCell.add(new Paragraph(order.getUser().getUsername())
                    .setFont(boldFont)
                    .setFontSize(11));
            customerCell.add(new Paragraph(order.getUser().getEmail())
                    .setFont(regularFont)
                    .setFontSize(10));
            customerCell.add(new Paragraph(order.getAddress())
                    .setFont(regularFont)
                    .setFontSize(10));
            infoTable.addCell(customerCell);

            // Order info
            Cell orderInfoCell = new Cell();
            orderInfoCell.setBorder(Border.NO_BORDER);
            orderInfoCell.setTextAlignment(TextAlignment.RIGHT);
            orderInfoCell.add(new Paragraph("ORDER DETAILS:")
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setFontColor(secondaryColor));
            orderInfoCell.add(new Paragraph("Date: " + formatDateTime(order.getOrderDate()))
                    .setFont(regularFont)
                    .setFontSize(10));
            orderInfoCell.add(new Paragraph("Payment Method: Account Balance")
                    .setFont(regularFont)
                    .setFontSize(10));
            orderInfoCell.add(new Paragraph("Delivered successfully")
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GREEN));
            infoTable.addCell(orderInfoCell);

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Horizontal line separator
            SolidLine line = new SolidLine(1f);
            line.setColor(primaryColor);
            LineSeparator ls = new LineSeparator(line);
            document.add(ls);
            document.add(new Paragraph("\n"));

            // Items table
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{40, 15, 15, 15, 15}));
            itemsTable.setWidth(UnitValue.createPercentValue(100));

            // Table header
            String[] headers = {"Product", "Price", "Quantity", "Discount", "Total"};
            for (String header : headers) {
                Cell headerCell = new Cell();
                headerCell.setBackgroundColor(secondaryColor);
                headerCell.setPadding(5);
                Paragraph headerText = new Paragraph(header)
                        .setFont(boldFont)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.WHITE);
                headerCell.add(headerText);
                itemsTable.addHeaderCell(headerCell);
            }

            // Order items
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;

            for (OrderItem item : order.getOrderItems()) {
                // Calculate item totals
                BigDecimal itemPrice = item.getPrice();
                BigDecimal quantity = new BigDecimal(item.getQuantity());
                BigDecimal discount = item.getDiscountPercentage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal discountAmount = itemPrice.multiply(quantity).multiply(discount).setScale(2, RoundingMode.HALF_UP);
                BigDecimal itemTotal = itemPrice.multiply(quantity).subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

                subtotal = subtotal.add(itemPrice.multiply(quantity));
                totalDiscount = totalDiscount.add(discountAmount);

                // Product name
                Cell productCell = new Cell();
                productCell.setPadding(5);
                productCell.add(new Paragraph(item.getBook().getTitle())
                        .setFont(regularFont)
                        .setFontSize(10));
                if (item.getBook().getAuthor() != null) {
                    productCell.add(new Paragraph("by " + item.getBook().getAuthor())
                            .setFont(regularFont)
                            .setFontSize(8)
                            .setFontColor(ColorConstants.DARK_GRAY));
                }
                itemsTable.addCell(productCell);

                // Price
                Cell priceCell = new Cell();
                priceCell.setPadding(5);
                priceCell.add(new Paragraph(formatCurrency(itemPrice))
                        .setFont(regularFont)
                        .setFontSize(10));
                itemsTable.addCell(priceCell);

                // Quantity
                Cell quantityCell = new Cell();
                quantityCell.setPadding(5);
                quantityCell.add(new Paragraph(item.getQuantity().toString())
                        .setFont(regularFont)
                        .setFontSize(10));
                itemsTable.addCell(quantityCell);

                // Discount
                Cell discountCell = new Cell();
                discountCell.setPadding(5);
                discountCell.add(new Paragraph(item.getDiscountPercentage() + "%")
                        .setFont(regularFont)
                        .setFontSize(10));
                itemsTable.addCell(discountCell);

                // Total
                Cell totalCell = new Cell();
                totalCell.setPadding(5);
                totalCell.add(new Paragraph(formatCurrency(itemTotal))
                        .setFont(regularFont)
                        .setFontSize(10));
                itemsTable.addCell(totalCell);
            }

            document.add(itemsTable);
            document.add(new Paragraph("\n"));

            // Order summary
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            // Empty cell
            Cell emptyCell = new Cell();
            emptyCell.setBorder(Border.NO_BORDER);
            summaryTable.addCell(emptyCell);

            // Summary values
            Cell summaryValuesCell = new Cell();
            summaryValuesCell.setBorder(Border.NO_BORDER);

            // Create inner table for alignment
            Table innerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            innerTable.setWidth(UnitValue.createPercentValue(100));

            // Subtotal
            Cell subtotalLabelCell = new Cell();
            subtotalLabelCell.setBorder(Border.NO_BORDER);
            subtotalLabelCell.setPadding(2);
            subtotalLabelCell.add(new Paragraph("Subtotal:")
                    .setFont(regularFont)
                    .setFontSize(10));
            innerTable.addCell(subtotalLabelCell);

            Cell subtotalValueCell = new Cell();
            subtotalValueCell.setBorder(Border.NO_BORDER);
            subtotalValueCell.setPadding(2);
            subtotalValueCell.setTextAlignment(TextAlignment.RIGHT);
            subtotalValueCell.add(new Paragraph(formatCurrency(subtotal))
                    .setFont(regularFont)
                    .setFontSize(10));
            innerTable.addCell(subtotalValueCell);

            // Total Discount
            if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
                Cell discountLabelCell = new Cell();
                discountLabelCell.setBorder(Border.NO_BORDER);
                discountLabelCell.setPadding(2);
                discountLabelCell.add(new Paragraph("Discount:")
                        .setFont(regularFont)
                        .setFontSize(10));
                innerTable.addCell(discountLabelCell);

                Cell discountValueCell = new Cell();
                discountValueCell.setBorder(Border.NO_BORDER);
                discountValueCell.setPadding(2);
                discountValueCell.setTextAlignment(TextAlignment.RIGHT);
                discountValueCell.add(new Paragraph("-" + formatCurrency(totalDiscount))
                        .setFont(regularFont)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.RED));
                innerTable.addCell(discountValueCell);
            }

            // Shipping
            Cell shippingLabelCell = new Cell();
            shippingLabelCell.setBorder(Border.NO_BORDER);
            shippingLabelCell.setPadding(2);
            shippingLabelCell.add(new Paragraph("Shipping:")
                    .setFont(regularFont)
                    .setFontSize(10));
            innerTable.addCell(shippingLabelCell);

            Cell shippingValueCell = new Cell();
            shippingValueCell.setBorder(Border.NO_BORDER);
            shippingValueCell.setPadding(2);
            shippingValueCell.setTextAlignment(TextAlignment.RIGHT);
            shippingValueCell.add(new Paragraph(formatCurrency(order.getShippingCost()))
                    .setFont(regularFont)
                    .setFontSize(10));
            innerTable.addCell(shippingValueCell);

            // Add a line separator before total
            Cell separatorCell = new Cell(1, 2);
            separatorCell.setBorder(Border.NO_BORDER);
            separatorCell.setPadding(2);
            SolidLine totalLine = new SolidLine(1f);
            totalLine.setColor(secondaryColor);
            LineSeparator totalSeparator = new LineSeparator(totalLine);
            separatorCell.add(totalSeparator);
            innerTable.addCell(separatorCell);

            // Total
            Cell totalLabelCell = new Cell();
            totalLabelCell.setBorder(Border.NO_BORDER);
            totalLabelCell.setPadding(2);
            totalLabelCell.add(new Paragraph("TOTAL:")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setFontColor(secondaryColor));
            innerTable.addCell(totalLabelCell);

            Cell totalValueCell = new Cell();
            totalValueCell.setBorder(Border.NO_BORDER);
            totalValueCell.setPadding(2);
            totalValueCell.setTextAlignment(TextAlignment.RIGHT);
            totalValueCell.add(new Paragraph(formatCurrency(totalPaid))
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setFontColor(secondaryColor));
            innerTable.addCell(totalValueCell);

            summaryValuesCell.add(innerTable);
            summaryTable.addCell(summaryValuesCell);

            document.add(summaryTable);

            // Footer
            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Thank you for your purchase!")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(primaryColor);
            document.add(footer);

            Paragraph terms = new Paragraph("All sales are final. For questions or concerns about your order, " +
                    "please contact customer service at support@bookalley.com")
                    .setFont(regularFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(terms);

            document.close();
            return urlPath;
        } catch (Exception e) {
            System.out.println("Failed to generate receipt PDF for order "+ order.getOrderId()+ e.getMessage()+ e);
            throw new OperationFailedException("Failed to generate PDF receipt for order ID: " + order.getOrderId(), e);
        }
    }

    /**
     * Formats a BigDecimal currency value to a proper currency string with dollar sign.
     *
     * @param amount The amount to format
     * @return Formatted currency string
     */
    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(amount);
    }


    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
        return dateTime.format(formatter);
    }
}