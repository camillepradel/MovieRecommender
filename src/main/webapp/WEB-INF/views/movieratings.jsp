<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Films</title>
</head>
<body>

<h1>
	Vos films notés
</h1>
<ul>
	<c:forEach items="${ratings}" var="rating">
		<li>
			${rating.getMovie().id} - ${rating.score}
		</li>
	</c:forEach>
</ul>

<h1>
	Ajouter une note
</h1>
<ul>
	<c:forEach items="${allMovies}" var="movie">
		<!-- get movie rating if any -->
		<c:set var="score" value="0" />
		<c:forEach items="${ratings}" var="rating">
			<c:if test="${rating.getMovie().id eq movie.id}">
				<c:set var="score" value="${rating.score}" />
			</c:if>
		</c:forEach>
			
		<li>
			${movie.title} - ${score} (user id: ${userId}, movie id: ${movie.id})
			<form method="post" action="/MovieRecommender/movieratings">
				<input type="number" name="userId" value="${userId}" hidden="hidden">
				<input type="number" name="movieId" value="${movie.id}" hidden="hidden">
				<input type="radio" name="score" onclick="this.form.submit();" value="1" <c:if test="${score eq 1}">checked</c:if>>
				<input type="radio" name="score" onclick="this.form.submit();" value="2" <c:if test="${score eq 2}">checked</c:if>>
				<input type="radio" name="score" onclick="this.form.submit();" value="3" <c:if test="${score eq 3}">checked</c:if>>
				<input type="radio" name="score" onclick="this.form.submit();" value="4" <c:if test="${score eq 4}">checked</c:if>>
				<input type="radio" name="score" onclick="this.form.submit();" value="5" <c:if test="${score eq 5}">checked</c:if>>
			</form>
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