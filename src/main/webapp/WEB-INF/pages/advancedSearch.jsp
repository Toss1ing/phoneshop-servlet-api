<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>Welcome to Expert-Soft training!</p>
    <c:if test="${not empty success}">
        <div class="success">
                ${success}
        </div>
    </c:if>
    <c:if test="${not empty errors}">
        <div class="error">
            There was an error to advanced search
        </div>
    </c:if>

    <form method="get">
        <div>
            <label>
                Description:
                <input name="description" value="${param.description}">
            </label>
            <label>
                <select name="searchMode">
                    <option value="all" ${param.searchMode == 'all' ? 'selected' : ''}>All words</option>
                    <option value="any" ${param.searchMode == 'any' ? 'selected' : ''}>Any words</option>
                </select>
            </label>
            <c:if test="${not empty errors['description']}">
                <div class="error">${errors['description']}</div>
            </c:if>
        </div>
        <div>
            <label>
                Min Price:
                <input name="minPrice" value="${param.minPrice}">
            </label>
            <c:if test="${not empty errors['minPrice']}">
                <div class="error">${errors['minPrice']}</div>
            </c:if>
        </div>
        <div>
            <label>
                Max Price:
                <input name="maxPrice" value="${param.maxPrice}">
            </label>
            <c:if test="${not empty errors['maxPrice']}">
                <div class="error">${errors['maxPrice']}</div>
            </c:if>
        </div>
        <p>
            <button type="submit">Search</button>
        </p>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
            </td>
            <td>
                Price
            </td>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}" alt="${product.description}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                            ${product.description}
                    </a>
                </td>
                <td class="price-container">
                    <c:set var="priceHistory" value="${product.priceHistory}"/>
                    <span class="price">
                            <fmt:formatNumber value="${product.price}" type="currency"
                                              currencySymbol="${product.currency.symbol}"/>
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
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/styles/productListStyle.css">

</tags:master>
