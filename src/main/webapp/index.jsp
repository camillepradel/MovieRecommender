<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
%>
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
		<h1>M2 ICE - NoSQL project</h1>
		<h6>by Emmanuel Chebbi and Mathilde Lannes</h6>
		
		<div id="project-speech">
		
			Vous travaillez pour une start-up qui gère une application de recommandations de films.
			Celle-ci est victime de son succès et l'augmentation du nombre d'utilisateurs rend votre 
			base de données relationnelle difficile et coûteuse à gérer.
			Vous êtes missionné-e pour étudier les possibilités de migration vers une base de données NoSQL.
			Vous décidez d'adapter une partie de l'application pour exploiter deux bases de données NoSQL 
			que vous connaissez bien (Neo4j et MongoDB) et de comparer les performances de chacune.
		</div>
		
		<div class="redirect">
			<a href="hello?name=UT2">Browse through movies</a>
		</div>
	</body>

</html>