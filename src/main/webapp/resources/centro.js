/*
 * Centro App JS Main sheet
 *
 */

//--------
// GLOBALS
//--------

// MAP OBJECTS
var map = new google.maps.Map(document.getElementById('map'), {
		zoom: 13,
		center: {lat: 51.5, lng: -0.17},
		scrollwheel: true
	}),
	geocoder = new google.maps.Geocoder(),
	currentInfoWindow = null,
	resMarker = false;

// DOM OBJECTS
var $addressContainer = $('#address-container'),
	$resAddressContainer = $('#res-address-container'),
	$firstDescContainer = $('#first-desc'),
	$sndDescContainer = $('#snd-desc'),
	$submit = $('.submit');

// DATAS AND FLAGS
var directions = [],
	directionsDetails = [],
	times = [],
	markers = [],
	placeMarkers = [],
	resPanelOpened = false;

//-------------
// MAIN PROCESS
//-------------

$('#address-form, #res-address-form').on('submit', function(e){

      e.preventDefault();
      
      var $address = $('#address-input'),
          $container = $addressContainer;
      
      if (resPanelOpened) {
    	  $container = $resAddressContainer;
    	  $address = $('#res-address-input');
      }
      
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
    			                     
    			  addAddress(lat, lng, results.formatted_address);
    			  
    			  if (!resPanelOpened) {
	    			  $firstDescContainer.fadeOut(function(){
	    				  $sndDescContainer.fadeIn();
	    			  });
    			  }
    			  
    			  $address.val(''); 
    		  } else {
    			  alert('Address not found, try to be more precise.')
    		  }
    	  }
     });
});

// Link "Change transportation mode" with tab click
$('#transportation').on('click', function(){
    $("#start-pane-tab").click();
});

//Send form
$submit.on('click', calcCentralPoint);

$('#ResPlaceType').on('change',addPOI);

//----------------------
// FUNCTIONS DECLARATION
//----------------------

function refreshPOV() {
	var bounds = new google.maps.LatLngBounds();
	for (var i = 0; i < markers.length; ++i) {
		bounds.extend(markers[i].position);
	}
	map.fitBounds(bounds);
}

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
                	updateResAddress();
                });
            }
            
            // QUICK FIX
            updateResAddress();
            
            if (!resPanelOpened) {
            	
	            $resAddressContainer.append($addressContainer.find('.address'));
	            $resAddressContainer.find('select').show();
	                     
	            $('.grey-bkg').animate({opacity: 0}, 'fast', function() {
	            	$('.grey-bkg').remove();
	            });
	            $('#map').animate({left: '400px'}, 'slow', function() {
	            	refreshPOV();
	            });
	            $('#res-panel').animate({left: '0px'}, 'slow');
                    
                resPanelOpened = true;
            }
            
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
            	
            	html += '<div style="text-align: right;"><button class="btn btn-success" onclick="newCentro('+place.location.latitude+','+place.location.longitude+')">Go here!</button></div>';
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
	
	if (infoText) {
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
	}
	
	return marker;
}

function newCentro(lat, lng) {
	//resMarker.setPosition({lat:lat,lng:lng});
	//addPOI($('#ResPlaceType').val());
	addRoutes(lat, lng);
}

function freeze() {
	$('#loader').show();
	$('input, select, button').attr('disabled', 'disabled');
	if (resMarker) {
		resMarker.setDraggable(false);
	}
}

function unFreeze() {
	$('#loader').hide();
	$('input, select, button').removeAttr('disabled');
	if (resMarker) {
		resMarker.setDraggable(true);
	}
}

function addAddress(lat, lng, txt) {
	
	var $container = $addressContainer,
		select = $('#mean-select').html();
	
	if (resPanelOpened) {
		$container = $resAddressContainer;
	}
		                     
	$container.append('<div class="address" data-lat="'+lat+'" data-lng="'+lng+'" data-mean="driving" data-marker="'+markers.length+'">'
  					+'<div class="delete" aria-label="delete"><span aria-hidden="true">&times;</span></div><span class="text">'
  					+ txt
					+'</span>'
					+ select
					+ '</div>');
	
	var $addedSelect = $container.find('select').last();
	
	if (resPanelOpened) {
		$addedSelect.show();
	}
	
	$addedSelect.on('change', function() {
		var $this = $(this);
		$this.parent().data('mean', $this.val());
		addRoutes();
		addPOI();
	});				  
					  
	$container.find('.delete').on('click', function() {
		var $div = $(this).parent();
					  
		markers[$div.data('marker')].setMap(null);
		//markers.splice($div.data('marker'), 1);
		$div.remove();
		
		if ($addressContainer.html().trim() == '') {
			$sndDescContainer.fadeOut(function() {
				$firstDescContainer.fadeIn();
			});
		}
		
		if (resMarker) {
			addRoutes();
		}
		
		refreshPOV();
	});                   
				  
	var marker = createMarker({lat:lat,lng:lng}, startMarkerIcon);
	markers.push(marker);
	refreshPOV(); 
}

function addRoutes(lat, lng) {
		
	if (typeof(lat) == 'undefined' || typeof(lng) == 'undefined') {
		if (typeof(resMarker) == 'undefined') {
			// ERROR
			return;
		}
		
		var resPos = resMarker.getPosition();
		lat = resPos.lat();
		lng = resPos.lng();
	}
	
	//Clean
	for (var k = 0; k < directions.length; ++k) {
		directions[k].setMap(null);
		times[k].setMap(null);
	}
	
	directions = [];
	directionsDetails = [];
	times = [];
	// ---- End Cleaning
	
	var destString = lat + ',' + lng,
		$addresses = $('#res-panel .address');
	
	for (var i = 0; i < $addresses.length; ++i) {
		 var directionsService = new google.maps.DirectionsService(),
		 	 directionsRequest = {
				 origin: $($addresses[i]).data('lat') + ',' + $($addresses[i]).data('lng'),
				 destination: destString,
				 travelMode: google.maps.DirectionsTravelMode[$($addresses[i]).data('mean').toUpperCase()],
				 unitSystem: google.maps.UnitSystem.METRIC
		 	};
		 
		 directionsService.route(directionsRequest, function (response, status) {
			 if (status == google.maps.DirectionsStatus.OK) {
				 var directionsDisplay = new google.maps.DirectionsRenderer(),
				 	 path = response.routes[0].overview_path,
				 	 timePosition = {lat: path[Math.floor((path.length - 1)/2)].lat(), lng: path[Math.floor((path.length - 1)/2)].lng()},
				 	 direction = new google.maps.Polyline({
					    path: path,
					    geodesic: true,
					    strokeColor: '#8C8C8C',
					    strokeOpacity: 1.0,
					    strokeWeight: 4,
					    map: map
					  }),
					  time = new google.maps.InfoWindow({
							content: response.routes[0].legs[0].duration.text + ' - ' + response.routes[0].legs[0].distance.text
									 + '<br /><span class="direction-detail-link" onclick="showDirectionDetails(\'' + directionsDetails.length + '\')">More details...</span>',
							position: timePosition,
							map: map
					  });
				 
				  directionsDisplay.setDirections(response);
				  
				  directions.push(direction);
				  times.push(time);
				  directionsDetails.push(directionsDisplay);
			 }
			 else {
				 //Error has occured
			 }
		 });
	}
}

function showDirectionDetails(index) {
	directionsDetails[index].setPanel(document.getElementById('direction-detail'));
	$('#directionModal').modal();
}

function updateResAddress() {
	var resPos = resMarker.getPosition(),
	  lat = resPos.lat(),
	  lng = resPos.lng();
	
	$.ajax({
		  url: 'https://maps.googleapis.com/maps/api/geocode/json?address=' + lat + ',' + lng,
		  success: function(obj){	  
			  if (obj.status == 'OK') {
				  $('.res-detail').html(obj.results[0].formatted_address);
			  } else {
				  $('.res-detail').html('Lat: ' + lat + ', Lng: ' + lng);
			  }
		  }
	});
}

function storeSearch() {
	var $addresses = $('#res-panel .address'),
		startingPoints = "",
		mode = "";
	
		$addresses.each(function(){
			var $this = $(this);
			startingPoints += $this.data('lat') + "!" + $this.data('lng') + "|";
			mode += $this.data('mean') + "|";
		});
		
		if (startingPoints.length > 0) {
			startingPoints = startingPoints.substr(0, startingPoints.length - 1);
		}
		
		if (mode.length > 0) {
			mode = mode.substr(0, mode.length - 1);
		}
		
		var json = {
			startingPoints: startingPoints,
			modes: mode,
			meetingType: $('#ResPlaceType').val()
		};
		
		freeze();

	    $.ajax({
		    url: 'api/query/store',
		  	dataType : 'json',
		    contentType: "application/json;charset=utf-8",
		  	type: 'POST',
		    data: JSON.stringify(json),
			success: function(res){
				unFreeze();
				var currentURL = window.location.href
					paramPos = currentURL.indexOf('?'),
					newURL = currentURL + '?tkn=' + res.id;
				
				if (paramPos > -1) {
					if (paramPos == currentURL.length - 1) {
						newURL = currentURL + 'tkn=' + res.id;
					} else if (currentURL.indexOf('tkn=') > -1) {
						var tknPos = currentURL.indexOf('tkn=') + 4,
							andPos = currentURL.indexOf('&', tknPos);
						
						newURL = currentURL.substr(0, tknPos) + res.id;
						
						if (andPos > -1) {
							newURL += currentURL.substr(andPos);
						}
						
					} else {
						newURL = currentURL + '&tkn=' + res.id;
					}
				}
				
				window.history.pushState('', 'Centro', newURL);
			}
	    });
}