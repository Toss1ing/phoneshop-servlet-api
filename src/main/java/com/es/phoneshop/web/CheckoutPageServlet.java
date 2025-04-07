package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.CartServiceImplement;
import com.es.phoneshop.service.impl.OrderServiceImplement;
import com.es.phoneshop.utility.UrlPatterns;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.maven.shared.utils.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutPageServlet extends HttpServlet {
    private static final String CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";

    private static final String FIRST_NAME_ATTR = "firstName";
    private static final String LAST_NAME_ATTR = "lastName";
    private static final String PHONE_ATTR = "phone";
    private static final String DELIVERY_DATE_ATTR = "deliveryDate";
    private static final String DELIVERY_ADDRESS_ATTR = "deliveryAddress";
    private static final String PAYMENT_METHOD = "paymentMethod";

    private static final String LIST_PAYMENT_METHOD_ATTR = "paymentMethods";
    private static final String ORDER_ATTR = "order";
    private static final String FORM_DATA_ATTR = "formData";
    private static final String ERROR_ATTR = "errors";

    private static final String MSG_INVALID_PHONE_NUMBER = "Invalid phone number format.";
    private static final String MSG_EMPTY_INPUT = "must not be empty.";
    private static final String MSG_LETTERS_REQUIRED = " must contain letters";
    private static final String MSG_DELIVERY_DATE_IN_PAST = "Delivery date must be after now";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String PHONE_PATTERN = "^\\+\\d[\\d\\s-]*$";
    private static final String NAME_PATTERN = "^[A-Za-zА-Яа-яЁё\\s-]+$";

    protected OrderService orderService;
    protected CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderService = OrderServiceImplement.getInstance();
        cartService = CartServiceImplement.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Cart cart = cartService.getCart(request.getSession());
        List<String> paymentMethods = orderService.getPaymentMethods();

        request.setAttribute(LIST_PAYMENT_METHOD_ATTR, paymentMethods);
        request.setAttribute(FORM_DATA_ATTR, new HashMap<>());
        request.setAttribute(ERROR_ATTR, new HashMap<>());
        request.setAttribute(ORDER_ATTR, orderService.getOrder(cart, request.getSession()));

        request.getRequestDispatcher(CHECKOUT_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        Order order = orderService.getOrder(cart, request.getSession());
        request.setAttribute(ORDER_ATTR, order);

        Map<String, String> errors = new HashMap<>();
        Map<String, String> formData = getUserInput(request);

        LocalDate deliveryDate = parseDate(formData.get(DELIVERY_DATE_ATTR));

        validateUserInput(errors, formData, deliveryDate);

        if (!errors.isEmpty()) {
            request.setAttribute(ERROR_ATTR, errors);
            List<String> paymentMethods = orderService.getPaymentMethods();

            request.setAttribute(LIST_PAYMENT_METHOD_ATTR, paymentMethods);
            request.setAttribute(FORM_DATA_ATTR, formData);
            request.getRequestDispatcher(CHECKOUT_JSP).forward(request, response);
            return;
        }

        setDataToOrder(order, formData, deliveryDate);

        orderService.placeOrder(order);
        cartService.clear(request.getSession());
        response.sendRedirect(String.format(
                UrlPatterns.OverviewPageUrlPattern.OVERVIEW_PAGE_SUCCESS_URL,
                request.getContextPath(),
                order.getSecureId())
        );
    }

    private Map<String, String> getUserInput(HttpServletRequest request) {
        Map<String, String> formData = new HashMap<>();
        formData.put(FIRST_NAME_ATTR, request.getParameter(FIRST_NAME_ATTR).trim());
        formData.put(LAST_NAME_ATTR, request.getParameter(LAST_NAME_ATTR).trim());
        formData.put(PHONE_ATTR, request.getParameter(PHONE_ATTR).trim());
        formData.put(DELIVERY_DATE_ATTR, request.getParameter(DELIVERY_DATE_ATTR).trim());
        formData.put(DELIVERY_ADDRESS_ATTR, request.getParameter(DELIVERY_ADDRESS_ATTR).trim());
        formData.put(PAYMENT_METHOD, request.getParameter(PAYMENT_METHOD).trim());
        return formData;
    }

    private void validateUserInput(Map<String, String> errors, Map<String, String> formData, LocalDate deliveryDate) {
        validateName(errors, formData.get(FIRST_NAME_ATTR), FIRST_NAME_ATTR);
        validateName(errors, formData.get(LAST_NAME_ATTR), LAST_NAME_ATTR);
        validatePhone(errors, formData.get(PHONE_ATTR));
        validateDeliveryAddress(errors, formData.get(DELIVERY_ADDRESS_ATTR));

        if (deliveryDate == null || !deliveryDate.isAfter(LocalDate.now())) {
            errors.put(DELIVERY_DATE_ATTR, MSG_DELIVERY_DATE_IN_PAST);
        }
    }

    private LocalDate parseDate(String deliveryDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
            return LocalDate.parse(deliveryDateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void setDataToOrder(Order order, Map<String, String> formData, LocalDate deliveryDate) {
        order.setFirstName(formData.get(FIRST_NAME_ATTR));
        order.setLastName(formData.get(LAST_NAME_ATTR));
        order.setPhone(formData.get(PHONE_ATTR));
        order.setDeliveryDate(deliveryDate);
        order.setDeliveryAddress(formData.get(DELIVERY_ADDRESS_ATTR));
        order.setPaymentMethod(PaymentMethod.valueOf(formData.get(PAYMENT_METHOD)));
    }

    private void validateName(Map<String, String> errors, String name, String fieldName) {
        if (name == null || StringUtils.isEmpty(name)) {
            errors.put(fieldName, getReadableFieldName(fieldName) + MSG_EMPTY_INPUT);
            return;
        }

        if (!name.matches(NAME_PATTERN)) {
            errors.put(fieldName, getReadableFieldName(fieldName) + MSG_LETTERS_REQUIRED);
        }
    }

    private void validateDeliveryAddress(Map<String, String> errors, String deliveryAddress) {
        if (deliveryAddress == null || deliveryAddress.isEmpty()) {
            errors.put(DELIVERY_ADDRESS_ATTR, getReadableFieldName(DELIVERY_ADDRESS_ATTR) + MSG_EMPTY_INPUT);
        }
    }

    private String getReadableFieldName(String fieldName) {
        return fieldName.replaceAll("([A-Z])", " $1").trim().toLowerCase();
    }

    private void validatePhone(Map<String, String> errors, String phone) {
        if (!phone.matches(PHONE_PATTERN)) {
            errors.put(PHONE_ATTR, MSG_INVALID_PHONE_NUMBER);
        }
    }

}
