<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Films</title>
</head>
<body>
<h1>
	<c:choose>
	    <c:when test="${userId==null}">
	        Tous les films
	    </c:when>    
	    <c:otherwise>
	        Films de l'utilisateur ${userId}
	    </c:otherwise>
	</c:choose>
</h1>
<ul>
	<c:forEach items="${movies}" var="movie">
		<li>
			${movie.title}
			<ul>
				<c:forEach items="${movie.genres}" var="genre">
					<li>
						${genre.name}
					</li>
				</c:forEach>
			</ul>
		</li>
	</c:forEach>
</ul>
</body>
</html>