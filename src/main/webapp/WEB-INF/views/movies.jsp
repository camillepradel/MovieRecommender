<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="shortcut icon" href="<c:url value='/resources/favicon.ico'/>" />
		<spring:url value="/resources/css/main.css" var="mainCss" />
		<link href="${mainCss}" rel="stylesheet" />
		<title>M2 ICE NoSQL</title>
	</head>
	
	<body>
		<h2>
			<c:choose>
			    <c:when test="${userId==null}">
			        All movies
			    </c:when>    
			    <c:otherwise>
			        User ${userId}'s movies
			    </c:otherwise>
			</c:choose>
		</h2>
		
		<ul>
			<c:forEach items="${movies}" var="movie">
				<li>
					${movie.title}
					<br />
					<c:forEach items="${movie.genres}" var="genre">
						<span class="genre">
							${genre.name}
						</span>
					</c:forEach>
				</li>
			</c:forEach>
		</ul>
	</body>
</html>