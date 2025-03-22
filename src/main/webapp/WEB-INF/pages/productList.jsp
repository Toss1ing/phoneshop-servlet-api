<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <p>Welcome to Expert-Soft training!</p>
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
        Price
        <tags:sortLink sort="price" order="asc" label="↑"/>
        <tags:sortLink sort="price" order="desc" label="↓"/>
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
          <c:set var="priceHistory" value="${product.priceHistory}" />
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
                    <fmt:formatNumber value="${price.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
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
  <c:if test="${not empty viewedProducts}">
    <h2>Recently Viewed Products</h2>
    <div class="viewed-products-container">
      <c:forEach var="viewedProduct" items="${viewedProducts}">
        <div class="viewed-product">
          <a href="${pageContext.request.contextPath}/products/${viewedProduct.id}">
            <img src="${viewedProduct.imageUrl}" alt="${viewedProduct.description}" class="viewed-product-image"/>
            <p class="viewed-product-description">${viewedProduct.description}</p>
          </a>
        </div>
      </c:forEach>
    </div>
  </c:if>

  <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/styles/productListStyle.css">

</tags:master>
