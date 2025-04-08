<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order overview">
    <h1>
        Order overview
    </h1>
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
    <h2>Your details</h2>
    <table>
        <tags:input id="firstName"
                    label="First name"
                    value="${order.firstName}"
                    readonly="true"
        />
        <tags:input id="lastName"
                    label="Last name"
                    value="${order.lastName}"
                    readonly="true"
        />
        <tags:input id="phone"
                    label="phone"
                    value="${order.phone}"
                    readonly="true"
        />
        <tags:input id="deliveryDate"
                    label="Deliveryd date"
                    value="${order.deliveryDate}"
                    readonly="true"
        />
        <tags:input id="deliveryAddress"
                    label="Delivery address"
                    value="${order.deliveryAddress}"
                    readonly="true"
        />
        <tags:input id="paymentMethod"
                    label="Payment method"
                    value="${order.paymentMethod.toString()}"
                    readonly="true"
        />

    </table>

</tags:master>
