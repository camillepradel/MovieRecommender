<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="shortcut icon" href="<c:url value='/resources/favicon.ico'/>" />
		<spring:url value="/resources/css/main.css" var="mainCss" />
		<link href="${mainCss}" rel="stylesheet" />
		<title>M2 ICE NoSQL</title>
	</head>
	
	<body>
	
		<h1> Your recommendations </h1>
		<h6>Movies you should like. Don't forget to rate them afterwards !</h6>
		
		<ul>
			<c:forEach items="${recommendations}" var="recommendation">
				<li>
					${recommendation.getMovie().title}
				</li>
			</c:forEach>
		</ul>
	
	</body>
</html>