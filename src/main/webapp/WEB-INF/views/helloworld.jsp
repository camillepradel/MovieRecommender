<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="shortcut icon" href="<c:url value='/resources/favicon.ico'/>" />
		<spring:url value="/resources/css/main.css" var="mainCss" />
		<link href="${mainCss}" rel="stylesheet" />
		<title>M2 ICE NoSQL</title>
	</head>
	
	<body>
		<h1> Hello ${name}! </h1>
		<h6> ${message} </h6>
		
		You can browse movies, recommendations or ratings. <br />
		
		<div id="navigation">
			
			<div class="redirect">
				<a href="movies">Browse all movies</a>
			</div>
			
			<div class="redirect">
				<a href="movies?user_id=1">Browse your movies</a>
			</div>
			
			<div class="redirect">
				<a href="movieratings?user_id=1">Browse your ratings</a>
			</div>
			
			<div class="redirect">
				<a href="recommendations?user_id=1">Browse your recommendations</a>
			</div>
		</div>
	</body>
</html>