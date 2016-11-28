<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Recommandations</title>
</head>
<body>

<h1>
	Vos recommandations
</h1>
<ul>
	<c:forEach items="${recommendations}" var="recommendation">
		<li>
			${recommendation.getMovie().title}
		</li>
	</c:forEach>
</ul>

</body>
</html>