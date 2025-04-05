<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="error" required="false" type="java.util.Map" %>
<%@ attribute name="required" required="false" %>
<%@ attribute name="readonly" required="false" %>

<tr>
    <td><label for="${id}">${label}:</label></td>
    <td>
        <input id="${id}"
               name="${id}"
               value="${value}"
               <c:if test="${required}">required</c:if>
               <c:if test="${readonly}">readonly</c:if>
        />
        <c:if test="${not empty error}">
            <div class="error">${error[id]}</div>
        </c:if>
    </td>
</tr>
