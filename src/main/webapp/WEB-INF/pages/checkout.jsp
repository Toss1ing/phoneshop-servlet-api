<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<jsp:useBean id="formData" scope="request" type="java.util.Map" />
<jsp:useBean id="errors" scope="request" type="java.util.Map" />
<jsp:useBean id="paymentMethods" scope="request" type="java.util.ArrayList" />

<tags:master pageTitle="Order">

    <c:if test="${not empty errors}">
        <p class="error">
            There was an error place to order
        </p>
    </c:if>
    <p></p>
    <form>
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>
                    Description
                </td>
                <td class="quantity">
                    Quantity
                </td>
                <td>
                    Price
                </td>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${order.items}">
                <tr>
                    <td>
                        <img class="product-tile" src="${item.product.imageUrl}" alt="${item.product.description}">
                    </td>
                    <td>
                            ${item.product.description}
                    </td>
                    <td class="quantity">
                        <span>
                            <label>
                                ${item.quantity}
                            </label>
                        </span>
                    </td>
                    <td class="price-container">
                        <span class="price">
                            <fmt:formatNumber value="${item.product.price}" type="currency"
                                          currencySymbol="${item.product.currency.symbol}"/>
                        </span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr>
                <td colspan="2" class="total">Total (without delivery):</td>
                <td class="quantity">
                    <span>
                            ${order.totalQuantity}
                    </span>
                </td>
                <td>
                    <span>
                        <fmt:formatNumber value="${order.subtotal}" type="currency" currencySymbol="$"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="3" class="total">Delivery cost:</td>
                <td>
                    <span>
                        <fmt:formatNumber value="${order.deliveryCost}" type="currency" currencySymbol="$"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="3" class="total">Total (include delivery):</td>
                <td>
                    <span>
                        <fmt:formatNumber value="${order.totalPrice}" type="currency" currencySymbol="$"/>
                    </span>
                </td>
            </tr>
            </tfoot>
        </table>
    </form>
    <form method="post">
        <h2>Your details</h2>
        <table>
            <tags:input id="firstName"
                        label="First name"
                        value="${formData.firstName}"
                        error="${errors}"
                        required="true"
            />
            <tags:input id="lastName"
                        label="Last name"
                        value="${formData.lastName}"
                        error="${errors}"
                        required="true"
            />
            <tags:input id="phone"
                        label="phone"
                        value="${formData.phone}"
                        error="${errors}"
                        required="true"
            />
            <tags:input id="deliveryDate"
                        label="Deliveryd date"
                        value="${formData.deliveryDate}"
                        error="${errors}"
                        required="true"
            />
            <tags:input id="deliveryAddress"
                        label="Delivery address"
                        value="${formData.deliveryAddress}"
                        error="${errors}"
                        required="true"
            />
            <tags:selectPaymentMethod id="paymentMethod"
                         label="Payment method"
                         items="${paymentMethods}"
                         selected="${formData.paymentMethod}"
                         error="${errors}"
                         required="true"
            />

        </table>
        <p>
            <button type="submit">Place Order</button>
        </p>
    </form>

</tags:master>
