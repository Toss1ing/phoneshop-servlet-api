<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="true" %>

<html>
<head>
  <title>${pageTitle}</title>
  <link href='http://fonts.googleapis.com/css?family=Lobster+Two' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/styles/main.css">
</head>
<body class="product-list">
  <header class="header-container">
    <div class="header-info">
      <a href="${pageContext.servletContext.contextPath}">
        <div class="header-logo">
          <img src="${pageContext.servletContext.contextPath}/images/logo.svg"/>
          PhoneShop
        </div>
      </a>
      <a href="${pageContext.servletContext.contextPath}/search" class="advanced-search-link">
        search
      </a>
      <jsp:include page="/miniCart" />
    </div>
  </header>
  <main>
    <jsp:doBody/>
  </main>
  <footer>
    <div class="footer-content">
      <p>&copy; 2025 ExpertSoft!</p>
      <nav>
      </nav>
    </div>
  </footer>
</body>
</html>