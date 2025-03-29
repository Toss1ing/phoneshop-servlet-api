<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>Welcome to Expert-Soft training!</p>
    <p>
        ${cart}
    </p>
    <c:if test="${not empty param.success}">
        <p class="success">
            ${param.success}
        </p>
    </c:if>
    <c:if test="${not empty param.error}">
        <p class="error">
            There was an error adding to cart
        </p>
    </c:if>
    <form method="get">
        <label>
            <input name="query" value="${param.query}">
        </label>
        <button type="submit">Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
                <tags:sortLink sort="description" order="asc" label="↑"/>
                <tags:sortLink sort="description" order="desc" label="↓"/>
            </td>
            <td>
                Quantity
            </td>
            <td>
                Price
                <tags:sortLink sort="price" order="asc" label="↑"/>
                <tags:sortLink sort="price" order="desc" label="↓"/>
            </td>
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="product" items="${products}">
            <form method="post" action="${pageContext.servletContext.contextPath}/cart/add">
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}" alt="${product.description}">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                ${product.description}
                        </a>
                    </td>
                    <td>
                        <label>
                            <input name="quantity"
                                   value="${(not empty param.error and param.productId == product.id) ? param.quantity : '1'}"
                                   class="quantity">
                            <input type="hidden" name="productId" value="${product.id}"/>
                            <c:if test="${not empty param.error and param.productId == product.id}">
                                <div class="error">
                                        ${param.error}
                                </div>
                            </c:if>
                        </label>
                    </td>
                    <td class="price-container">
                        <c:set var="priceHistory" value="${product.priceHistory}"/>
                        <span class="price">
                            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                        </span>
                        <c:if test="${not empty priceHistory}">
                            <div class="tooltip">
                                <h2>Price history:</h2>
                                <ul>
                                    <c:forEach var="price" items="${priceHistory}">
                                        <li>
                                            <fmt:formatDate value="${price.date}" pattern="yyyy-MM-dd"/>
                                            -
                                            <fmt:formatNumber value="${price.price}" type="currency"
                                                              currencySymbol="${product.currency.symbol}"/>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </td>
                    <td>
                        <button>
                            Add to cart
                        </button>
                    </td>
                </tr>
            </form>
        </c:forEach>
        </tbody>
    </table>

    <tags:recentlyViewedProducts viewedProducts="${viewedProducts}"/>

    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/styles/productListStyle.css">

</tags:master>
