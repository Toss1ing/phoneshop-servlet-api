<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="productId" scope="request" type="java.lang.Long"/>
<tags:master pageTitle="Product not found">

    <h1>
        Sorry, the product with ID ${productId} was not found.
    </h1>
    <p>
        We couldn't locate the product you're looking for.
    </p>

</tags:master>
