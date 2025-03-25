<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag body-content="empty" %>
<%@ attribute name="viewedProducts" required="true" type="java.util.List" %>

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
