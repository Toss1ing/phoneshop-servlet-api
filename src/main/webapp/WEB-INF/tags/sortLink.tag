<%@ tag body-content="empty" %>
<%@ attribute name="sort" required="true" %>
<%@ attribute name="order" required="true" %>
<%@ attribute name="label" required="true" %>

<a href="?query=${param.query}&sort=${sort}&order=${order}"
   style="${sort eq param.sort and order eq param.order ? 'font-weight: bold; border-bottom: 2px solid black' : ''}">
    ${label}
</a>

