<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="items" required="true" type="java.util.ArrayList" %>
<%@ attribute name="selected" required="false" type="java.lang.String" %>
<%@ attribute name="error" required="false" type="java.util.Map" %>
<%@ attribute name="required" required="false" %>


<tr>
    <td><label for="${id}">${label}:</label></td>
    <td>
        <select id="${id}" name="${id}"
                <c:if test="${required}">required</c:if>
        >
            <c:forEach var="item" items="${items}">
                <option value="${item}" <c:if test="${item == selected}">selected</c:if>>${item}</option>
            </c:forEach>
        </select>
        <c:if test="${not empty error[id]}">
            <div class="error">${error[id]}</div>
        </c:if>
    </td>
</tr>
