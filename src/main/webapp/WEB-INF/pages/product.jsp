<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Detail">
    <p>
        ${cart}
    </p>
    <c:if test="${not empty param.success}">
        <div class="success">
            ${param.success}
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="error">
            There was an error adding to cart
        </div>
    </c:if>
    <p>
        ${product.description}
    </p>
    <form method="post">
        <table>
            <tr>
                <td>
                    image
                </td>
                <td>
                    <img src="${product.imageUrl}" alt="">
                </td>
            </tr>
            <tr>
                <td>
                    stock
                </td>
                <td class="stock">
                    ${product.stock}
                </td>
            </tr>
            <tr>
                <td>
                    price
                </td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td>
                    code
                </td>
                <td>
                    ${product.code}
                </td>
            </tr>
            <tr>
                <td class="quantity">
                    quantity
                </td>
                <td>
                    <label>
                        <input name="quantity" value="${not empty sessionScope.quantity ? sessionScope.quantity : '1'}" class="quantity">
                        <c:if test="${not empty param.error}">
                            <div class="error">
                                ${param.error}
                            </div>
                        </c:if>
                    </label>
                </td>
            </tr>
        </table>
        <p>
            <button>
                Add to cart
            </button>
        </p>
        <tags:recentlyViewedProducts viewedProducts="${viewedProducts}" />
    </form>
</tags:master>