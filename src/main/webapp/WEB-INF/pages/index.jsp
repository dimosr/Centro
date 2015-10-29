<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta charset="utf-8">
	<title>Centro</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<!-- Fav Icon -->
	<link rel="shortcut icon" href="<c:url value='/img/favicon.ico'/>" type="image/x-icon">
	<link rel="icon" href="<c:url value='/img/favicon.ico'/>" type="image/x-icon">

	<link rel="stylesheet" href="<c:url value='/resources/bootstrap/css/bootstrap.min.css'/>">
	<link href='https://fonts.googleapis.com/css?family=Fira+Sans' rel='stylesheet' type='text/css'>
	
	<!-- Centro theme -->
	<link rel="stylesheet" href="<c:url value='/resources/centro.css'/>">
</head>
<body>
	<!-- Map Container -->
	<div id="map"></div>
	
	<div class="grey-bkg">
		<div class="logo-container">
			<img src="<c:url value='/img/logo.png'/>" />
			<p id="first-desc">
				Centro helps you find the best meeting point for you and your friends.<br />
				It's easy, start by typing your address:
			</p>
			<p id="snd-desc">
				You can add more addresses, when you're done click <button class="btn btn-default" id="submit">I'm done!</button>
			</p>
		</div>
		
		<form id="address-form">
			<div class="form-group">
				<div id="home-addon"><span class="glyphicon glyphicon-home" aria-hidden="true"></span></div>
				<input type="text" class="form-control" placeholder="My address" id="address-input"><!--
			 --><button class="btn btn-success" type="submit" id="add-button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
			</div>
		</form>
		
		<div id="address-container">
		
		</div>
	</div>
	
	<script src="<c:url value='/resources/jquery/jquery.min.js'/>"></script>
	<script src="<c:url value='/resources/bootstrap/js/bootstrap.min.js'/>"></script>
	<script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
	<script src="<c:url value='/resources/centro.js'/>"></script>
</body>
</html>