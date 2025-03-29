<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
    <p>
        ${cart}
    </p>
    <c:if test="${not empty param.success}">
        <div class="success">
            ${param.success}
        </div>
    </c:if>
    <c:if test="${not empty cartErrors}">
        <div class="error">
            There was an error adding to cart
        </div>
    </c:if>

    <form method="post" action="${pageContext.servletContext.contextPath}/cart">
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
                <td>
                    Action
                </td>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${cart.items}">
                <tr>
                    <td>
                        <img class="product-tile" src="${item.product.imageUrl}" alt="${item.product.description}">
                    </td>
                    <td>
                            ${item.product.description}
                    </td>
                    <td class="quantity">
                    <span>
                        <fmt:formatNumber value="${item.quantity}" var="quantity"/>
                        <label>
                             <input name="quantity"
                                    class="quantity"
                                    value="${(not empty cartErrors[item.product.id]) ? cartQuantities[item.product.id] : quantity}">
                            <input name="productId" type="hidden" value="${item.product.id}">
                        </label>
                        <c:if test="${not empty cartErrors[item.product.id]}">
                            <div class="error">${cartErrors[item.product.id]}</div>
                        </c:if>
                    </span>
                    </td>
                    <td class="price-container">
                    <span class="price">
                        <fmt:formatNumber value="${item.product.price}" type="currency"
                                          currencySymbol="${item.product.currency.symbol}"/>
                    </span>
                    </td>
                    <td>
                        <button form="deleteCartItem"
                                formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <p>
            <button>Update</button>
        </p>
    </form>
    <form id="deleteCartItem" method="post">
    </form>

</tags:master>
