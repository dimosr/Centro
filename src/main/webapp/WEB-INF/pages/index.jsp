<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="utf-8">
<title>Basic Bootstrap Template</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="<c:url value='/resources/bootstrap/css/bootstrap.min.css'/>">
<!-- Optional Bootstrap theme -->
<link rel="stylesheet" href="<c:url value='/resources/bootstrap/css/bootstrap-theme.min.css'/>">
</head>
<body>
	<h1>Hello, world!</h1>
	<form role="form">
		<div class="form-group">
			<label for="email">Email address:</label> <input type="email"
				class="form-control" id="email">
		</div>
		<div class="form-group">
			<label for="pwd">Password:</label> <input type="password"
				class="form-control" id="pwd">
		</div>
		<div class="checkbox">
			<label><input type="checkbox"> Remember me</label>
		</div>
		<button type="submit" class="btn btn-default">Submit</button>
	</form>
        <script src="<c:url value='/resources/jquery/jquery.min.js'/>" />
        <script src="<c:url value='/resources/bootstrap/js/bootstrap.min.js'/>" />
</body>
</html>