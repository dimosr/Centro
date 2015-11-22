<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.centro.util.PlaceType" %>
<%@ page import="com.centro.util.TransportationMode" %>
<%  pageContext.setAttribute("placeTypes", PlaceType.values()); %>
<%  pageContext.setAttribute("transportationModes", TransportationMode.values()); %>
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
        <script src="<c:url value='/resources/analytics/ga.js'/>"></script>
</head>
<body>
	<!-- Map Container -->
	<div id="map"></div>
	
	<div class="grey-bkg">
		<div class="logo-container">
			<img src="<c:url value='/img/logo.png'/>" />
			<p id="first-desc">
				Centro helps you to find the middle point between different locations. 
				It's easy, start by typing your address.
			</p>
			<p id="snd-desc">
				Now add the remaining addresses, when you're done click "Go". <br />
			</p>
		</div>
		
		<div style="padding: 0px 25px;">
			<form id="address-form">
				<div class="form-group">
					<div id="home-addon"><span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span></div>
					<input type="text" class="form-control" placeholder="Type your address.." id="address-input"><!--
				 --><button class="btn btn-success" type="submit" id="add-button"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
				 </div>
			</form>
			
			<div id="address-container"></div>
			<button class="btn btn-default" id="submit">Go!</button>
		</div>
	</div>
	
	<div id="loader" style="display:none;">
		<img src="<c:url value='/img/loader.gif'/>" alt="loading" />
	</div>

	<div id="res-panel">
		<img src="<c:url value='/img/logo.png'/>" class="logo" />
		<h1>Middle point:</h1>
		<div class="res-detail">
		</div>
		
		<!--h1>Filters</h1-->
		<ul class="nav nav-tabs nav-justified filter-pills" role="tablist">
		    <li role="presentation" class="active"><a href="#dest-pane" aria-controls="dest-pane" role="tab" data-toggle="tab">Destination</a></li>
		    <li role="presentation"><a href="#start-pane" aria-controls="start-pane" role="tab" data-toggle="tab">Starting Points</a></li>
		</ul>

	  	<div class="tab-content filter-content">
		    <div role="tabpanel" class="tab-pane fade in active" id="dest-pane">
		    	<h2>Destination category</h2>
				<div class="form-group">
		            <select class="form-control" id="ResPlaceType">
		            	<option value="">Nothing specific</option>
		            	<option value="any">Any point of interest</option>
		                <c:forEach var="placeType" items="${placeTypes}">
		                    <option value="<c:out value="${placeType.getGoogleApiName()}"/>"><c:out value="${placeType.getFrontEndName()}"/></option>
		                </c:forEach>
		            </select>
				</div>
                    </div>
                    <div role="tabpanel" class="tab-pane fade" id="start-pane">
                            <h2>Addresses</h2>
                            <div id="res-address-container"></div>
                    </div>
		</div>
	</div>
	<div style="display:none;" id="mean-select">
		<select class="form-control">
            <c:forEach var="transportationMode" items="${transportationModes}">
                <option value="<c:out value="${transportationMode.getMapsFormat()}"/>"><c:out value="${transportationMode.getFrontEndName()}"/></option>
            </c:forEach>
        </select>
	</div>
	
	<div class="modal fade" id="directionModal" tabindex="-1" role="dialog" aria-labelledby="directionModalLabel">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-body" id="direction-detail">
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<script src="<c:url value='/resources/jquery/jquery.min.js'/>"></script>
	<script src="<c:url value='/resources/bootstrap/js/bootstrap.min.js'/>"></script>
	<script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
	<script src="<c:url value='/resources/centro.js'/>"></script>
	<script>
		var resMarkerIcon = "<c:url value='/img/marker.png'/>";
		var placeMarkerIcon = "<c:url value='/img/placeMarker.png'/>";
		var starsImg = "<c:url value='/img/stars.png'/>";
	</script>
</body>
</html>