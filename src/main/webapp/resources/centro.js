/*
 * Centro App JS Main sheet
 *
 */

var map = new google.maps.Map(document.getElementById('map'), {
	zoom: 13,
	center: {lat: 51.5, lng: -0.17},
	scrollwheel: true
});

var currentInfoWindow = null;
var geocoder = new google.maps.Geocoder();

/* Address addition */

var $address = $('#address-input'),
	$addressContainer = $('#address-container'),
	$resAddressContainer = $('#res-address-container'),
	$firstDescContainer = $('#first-desc'),
	$sndDescContainer = $('#snd-desc'),
	bounds = new google.maps.LatLngBounds(),
	directions = [],
	markers = [];

$('#address-form').on('submit', function(e){
      e.preventDefault();
      
      var address = $address.val(),
      	  url = 'https://maps.googleapis.com/maps/api/geocode/json?address=' + encodeURIComponent(address);
      
      freeze();
      
      $.ajax({
    	  url: url,
    	  success: function(obj){
    		  unFreeze();
    		  
    		  if (obj.status == 'OK') {
    			  var results = obj.results[0],
    			  	  lat = results.geometry.location.lat,
    			  	  lng = results.geometry.location.lng;
    			  
    			  $addressContainer.append('<div class="address" data-lat="'+lat+'" data-lng="'+lng+'" data-mean="driving" data-marker="'+markers.length+'">'
    					  					+'<div class="delete" aria-label="delete"><span aria-hidden="true">&times;</span></div><span class="text">'
    					  					+results.formatted_address
					  						+'</span></div>');
    			  
    			  $firstDescContainer.fadeOut(function(){
    				  $sndDescContainer.fadeIn();
    			  });
    				  
    			  $('.address[data-marker="'+markers.length+'"] .delete').on('click', function() {
    				  var $div = $(this).parent();
    				  
    				  markers[$div.data('marker')].setMap(null);
    				  $div.remove();
    				  if ($addressContainer.html().trim() == '') {
    					  $sndDescContainer.fadeOut(function() {
    						  $firstDescContainer.fadeIn();
    					  });
    				  }
    			  });
    			  
    			  var marker = new google.maps.Marker({
    				  map: map,
    				  position: {lat:lat,lng:lng}
    			  });
    			  
    			  markers.push(marker);
    			  
    			  bounds.extend(marker.position);
    			  map.fitBounds(bounds);
    			  
    			  $address.val(''); 
    		  } else {
    			  alert('Address not found, try to be more precise.')
    		  }
    	  }
     });
});

/* Send Form */

var resMarker = false,
	placeMarkers = [],
	$submit = $('#submit');

$submit.on('click', calcCentralPoint);

$('#ResPlaceType').on('change', function(){	
	addPOI();
});

// Util --------------
var currentInfoWindow = null,
	resPanelOpened = false;

function calcCentralPoint() {
    var json = [];
    
    $('.address').each(function() {
	var $this = $(this);
	json.push({"latitude":$this.data('lat'), "longitude": $this.data('lng')});
    });
	
    freeze();
    
    $.ajax({
	    url: 'api/central',
	  	dataType : 'json',
	    contentType: "application/json;charset=utf-8",
	  	type: 'POST',
	    data: JSON.stringify(json),
		success: function(res){
			unFreeze();
			
			// Central point
            if (resMarker) {
                resMarker.setPosition({lat:res.latitude,lng:res.longitude});
            } else {
                resMarker = createMarker({lat:res.latitude,lng:res.longitude}, resMarkerIcon, "Central Point");
                resMarker.setDraggable(true);
                google.maps.event.addListener(resMarker, 'dragend', function() {
                	addPOI($('#ResPlaceType').val());
                	addRoutes();
                });
            }
            
            $('.res-detail').html('Lat: ' + res.latitude + ', Lng: ' + res.longitude);
            
            if (!resPanelOpened) {
	            // Copy starting points into 
	            var select = $('#mean-select').html();
	            $resAddressContainer.html($addressContainer.html());
	            $resAddressContainer.find('.delete').remove();
	            $resAddressContainer.find('.address').append(select);
	            $resAddressContainer.find('select').on('change', function() {
	            	var $this = $(this);
	            	$this.parent().data('mean', $this.val());
	            	addRoutes();
	            });
	            
	            //end copying/cleaning
	                     
	            $('.grey-bkg').animate({opacity: 0}, 'fast', function() {
	            	$('.grey-bkg').remove();
	            });
	            $('#map').animate({left: '400px'}, 'slow');
	            $('#res-panel').animate({left: '0px'}, 'slow');
            }
            
            map.fitBounds(bounds);
            addRoutes();
		}
    });
}

function addPOI() {
	var gPos = resMarker.getPosition(),
		pType = $('#ResPlaceType').val(),
		$addresses = $('.address');
		res = {};
	
	res.latitude = gPos.lat();
	res.longitude = gPos.lng();
	
	res.startingPoints = [];
	
	for (var i = 0; i < $addresses.length; ++i) {
		res.startingPoints.push({
			latitude: $($addresses[i]).data('lat'),
			longitude: $($addresses[i]).data('lng'),
			mode: $($addresses[i]).data('mean')
		});
	}
	
	if (pType == "") {
		return true;
	}
	
	if (pType != "Any") {
		res.type = pType;
	}
	
	freeze();
	
	$.ajax({
	    url: 'api/places',
	  	dataType : 'json',
	    contentType: "application/json;charset=utf-8",
	  	type: 'POST',
	    data: JSON.stringify(res),
		success: function(places){
			
			unFreeze();
			
			placeMarkers.forEach(function(p){
				 p.setMap(null);
			});
			placeMarkers = [];
            places.forEach(function(place){
            	var html = '<div class="info-window"><h4>'+place.name+'</h4>';
            	if (place.info) {
	            	if (place.info.averageRating != '-') {
	            		var rating = 20 * parseFloat(place.info.averageRating);
	            		html += '<div class="rating">';
	            		html += '<div class="yellow" style="width: '+ parseInt(rating) +'px"></div>';
	            		html += '<img src="' + starsImg + '"  alt="rating_template" />';
	            		html += '<div class="rating-value">(' + place.info.averageRating + ')</div>';
	            		html += '</div>';
	            	}
	            	
	            	var img_len = place.info.imageLinks.length;
	            	
	            	if (img_len > 3) {
	            		img_len = 3;
	            	}

	            	html += '<div class="img-poi">';
	            	
	            	for (var k = 0; k < img_len; ++k) {
	            		html += '<img src="' + place.info.imageLinks[k] + '" alt="image poi" />';
	            	}
	            	
	            	html += '</div>';
	            	
	            	if (place.info.websiteLink != '-') {
	            		html += '<a href="' + place.info.websiteLink + '" target="_blank">Visit its website!</a>';
	            	}	            	
            	}
            	
            	html += '<div style="text-align: right;"><button class="btn btn-success" onclick="newCentro('+place.location.latitude+','+place.location.longitude+')">Make it Centro!</button></div>';
            	html += '</div>';
            	
            	var m = createMarker({lat:place.location.latitude,lng:place.location.longitude}, placeMarkerIcon, html);
            	placeMarkers.push(m);
            });
        }
	 });
}

function createMarker(latLng, icon, infoText) {
	var marker = new google.maps.Marker({
		icon: icon,
		position: latLng,
		map: map
	});
	
	var infowindow = new google.maps.InfoWindow({
		content: infoText 
	});

	google.maps.event.addListener(marker, 'mouseover', function() {
		infowindow.open(map,marker);
	});

	google.maps.event.addListener(marker, 'mouseout', function () {
		if (currentInfoWindow != infowindow) { 
			infowindow.close();
		} 
	});

	google.maps.event.addListener(marker, 'click', function() {
		var needOpen = true;
		
		if (currentInfoWindow != null) {
			needOpen = currentInfoWindow.content != infowindow.content;
			currentInfoWindow.close();
			currentInfoWindow = null;
		}
		if (needOpen) {
			infowindow.open(map, marker); 
			currentInfoWindow = infowindow;
		}
	});
	
	return marker;
}

function newCentro(lat, lng) {
	resMarker.setPosition({lat:lat,lng:lng});
	addPOI($('#ResPlaceType').val());
	addRoutes();
}

function freeze() {
	$('#loader').show();
	$('input, select, button').attr('disabled', 'disabled');
}

function unFreeze() {
	$('#loader').hide();
	$('input, select, button').removeAttr('disabled');
}

function addRoutes() {
	if (typeof(resMarker) == 'null') {
		return;
	}
	
	//Clean
	for (var k = 0; k < directions.length; ++k) {
		directions[k].setMap(null);
	}
	
	directions = [];
	// ---- End Cleaning
	
	var resPos = resMarker.getPosition(),	
		destString = resPos.lat() + ',' + resPos.lng(),
		$addresses = $('.address');
	
	for (var i = 0; i < $addresses.length; ++i) {
		 var directionsService = new google.maps.DirectionsService(),
		 	 //directionsDisplay = new google.maps.DirectionsRenderer(),
		 	 directionsRequest = {
				 origin: $($addresses[i]).data('lat') + ',' + $($addresses[i]).data('lng'),
				 destination: destString,
				 travelMode: google.maps.DirectionsTravelMode[$($addresses[i]).data('mean').toUpperCase()],
				 unitSystem: google.maps.UnitSystem.METRIC
		 	};
		 
		 //directionsDisplay.setMap(map);
		 
		 directionsService.route(directionsRequest, function (response, status) {
			 if (status == google.maps.DirectionsStatus.OK) {
				 //directionsDisplay.setDirections(response);
				 var path = response.routes[0].overview_path,
				 	 direction = new google.maps.Polyline({
					    path: path,
					    geodesic: true,
					    strokeColor: '#FF0000',
					    strokeOpacity: 1.0,
					    strokeWeight: 2
					  });

					  direction.setMap(map);
					  directions.push(direction);
			 }
			 else {
				 //Error has occured
			 }
		 });
	}
}