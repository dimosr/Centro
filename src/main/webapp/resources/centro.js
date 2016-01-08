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
	$errorModal = $('#errorModal'),
	$POIType = $('#ResPlaceType'),
	$saveLink = $('#save-link'),
	$fbShare = $('#fb-share'),
	$twShare = $('#tw-share'),
	$submit = $('.submit');

// DATAS AND FLAGS
var directions = [],
	directionsDetails = [],
	times = [],
	markers = [],
	placeMarkers = [],
	resPanelOpened = false;

// SOCIAL MEDIA
var fbShare = "https://www.facebook.com/sharer/sharer.php?u=",
	twShare = "https://twitter.com/home?status=";

//AUTOCOMPLETE
var autocomplete = new google.maps.places.Autocomplete((document.getElementById('address-input')));
var autocompleteRes = new google.maps.places.Autocomplete((document.getElementById('res-address-input')));
//-------------
// MAIN PROCESS
//-------------

//IF TOKEN
if (window.location.href.indexOf('tkn=') > -1) {
	
	var tknPos = window.location.href.indexOf('tkn=') + 4,
		andPos = window.location.href.indexOf('&', tknPos),
		tkn = window.location.href.substr(tknPos);
	
	if (andPos > -1) {
		tkn = window.location.href.substr(tknPos, andPos - tknPos)
	}
	
	freeze();
    
    $.ajax({
	    url: 'api/query/get?id=' + tkn,
	  	dataType : 'json',
	    contentType: "application/json;charset=utf-8",
	  	type: 'GET',
		success: function(res){
			if (!res.id) {
				unFreeze();
				return;
			}
			
			var locations = res.startingPoints.split('|'),
				modes = res.modes.split('|');
			
			$POIType.val(res.meetingType);
			
			$.each(locations, function(index, loc) {
				var latLng = loc.split('!'),
					lat = latLng[0],
					lng = latLng[1];

				$.ajax({
					  url: 'https://maps.googleapis.com/maps/api/geocode/json?address=' + lat + ',' + lng,
					  success: function(obj){	  
						  if (obj.status == 'OK') {
							  addAddress(lat, lng, obj.results[0].formatted_address, modes[index]);
						  }
						  
						  if (index == locations.length - 1) {
							  unFreeze();
							  $('.submit').first().click(); //TO COMMENT IF NEEDED
						  }
					  }
				});
			});
		}
    });
}

// Main form sent, calc mid
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

//$POIType.on('change',addPOI);

$('#save-button').on('click', function() {
	storeSearch(function(url) {
		$saveLink.attr('href', url);
		$fbShare.attr('href', fbShare + encodeURIComponent(url));
		$twShare.attr('href', twShare + encodeURIComponent(url));
		$saveLink.html(url);
		$('#saveModal').modal();
	});
});

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
	
    if ($('.address').length == 0) {
    	return;
    }
    
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
                	addPOI($POIType.val());
                	addRoutes();
                	updateResAddress();
                });
            }
            
            updateResAddress();
            
            if (!resPanelOpened) {
	            openResPanel();
            }
            
            addRoutes();
            addPOI();
		},
		statusCode: {
		    500: serviceUnavailable,
		    503: serviceUnavailable
		}
    });
}

function openResPanel() {
	$resAddressContainer.append($addressContainer.find('.address'));
    $resAddressContainer.find('table').show();
             
    $('.grey-bkg').animate({opacity: 0}, 'fast', function() {
    	$('.grey-bkg').remove();
    });
    
    //Check responsiveness here
    $('#map').animate({left: '400px'}, 'slow', function() {
    	refreshPOV();
    });
    $('#res-panel').animate({left: '0px'}, 'slow');
    // -- 
    
    resPanelOpened = true;
    addPOI();
}

function addPOI() {
	if(!resMarker) {
		return;
	}
	
	var gPos = resMarker.getPosition(),
		pType = $POIType.val(),
		$addresses = $('.address');
		res = {};
	
	res.latitude = gPos.lat();
	res.longitude = gPos.lng();
	
	res.startingPoints = [];
	
	for (var i = 0; i < $addresses.length; ++i) {
		var toPush = {
			latitude: $($addresses[i]).data('lat'),
			longitude: $($addresses[i]).data('lng'),
			mode: $($addresses[i]).data('mean')
		};
		
		if ($($addresses[i]).data('max-time')) {
			toPush['maxTime'] = $($addresses[i]).data('max-time') * 60;
		}
		
		res.startingPoints.push(toPush);
	}
	
	if (pType == "") {
		placeMarkers.forEach(function(p){
			 p.setMap(null);
		});
		placeMarkers = [];
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
        },
		statusCode: {
		    500: serviceUnavailable,
		    503: serviceUnavailable
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
	//addPOI($POIType.val());
	addRoutes(lat, lng);
}

function freeze() {
	var phrases = ['Crunching data for you...', 'Checking north and south pole.', 'Looking for santa\'s house...', 'Checking water level in oceans.'];
	
	phrase = phrases[Math.floor(Math.random()*phrases.length)];
	$('#load-phrase').html(phrase);
	$('#overlay').show();
	$('#loader').show();
	$('#load-phrase').show();
	$('input, select, button').attr('disabled', 'disabled');
	if (resMarker) {
		resMarker.setDraggable(false);
	}
}

function unFreeze() {
	$('#overlay').hide();
	$('#loader').hide();
	$('#load-phrase').hide();
	$('input, select, button').removeAttr('disabled');
	if (resMarker) {
		resMarker.setDraggable(true);
	}
}

function addAddress(lat, lng, txt, mode) {
	
	var $container = $addressContainer,
		select = $('#mean-select').html();
	
	if (resPanelOpened) {
		$container = $resAddressContainer;
	}
		                     
	$container.append('<div class="address" data-lat="'+lat+'" data-lng="'+lng+'" data-mean="driving" data-marker="'+markers.length+'">'
  					+'<div class="delete" aria-label="delete"><span aria-hidden="true">&times;</span></div><span class="text">'
  					+ txt
					+'</span>'
					+'<table style="display:none;">'
					+'	<tr>'
					+'		<td>Mean of transportation</td>'
					+'		<td class="middle"></td>'
					+'		<td>Max-time</td>'
					+'	</tr>'
					+'	<tr>'
					+'		<td>'
					+ 			select
					+'		</td>'
					+'		<td class="middle"></td>'
					+'		<td>'
					+'			<select class="form-control max-time">'
					+'				<option value="">---</option>'
					+'				<option value="15">15min</option>'
					+'				<option value="30">30min</option>'
					+'				<option value="45">45min</option>'
					+'				<option value="60">1h</option>'
					+'				<option value="90">1h30</option>'
					+'				<option value="120">2h</option>'
					+'			</select>'
					+'		</td>'
					+'	</tr>'
					+'</table>'
					+'</div>');
	
	
	var $addedTable = $container.find('table').last(),
		$addedMode = $container.find('.mode').last(),
		$addedMaxTime = $container.find('.max-time').last();
	
	if (resPanelOpened) {
		$addedTable.show();
	}
	
	if (mode) {
		$addedMode.val(mode);
	}
	
	$addedMode.on('change', function() {
		var $this = $(this);
		$this.closest('.address').data('mean', $this.val());
		//addRoutes();
		//addPOI();
	});
	
	$addedMaxTime.on('change', function() {
		var $this = $(this);
		$this.closest('.address').data('max-time', $this.val());
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
				  
	var marker = createMarker({lat:lat*1,lng:lng*1}, startMarkerIcon);
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
		$addresses = $('#res-panel .address'),
		hour = $('#hour').val(),
		min = $('#min').val();
	
	for (var i = 0; i < $addresses.length; ++i) {
		 var directionsService = new google.maps.DirectionsService(),
		 	 directionsRequest = {
				 origin: $($addresses[i]).data('lat') + ',' + $($addresses[i]).data('lng'),
				 destination: destString,
				 travelMode: google.maps.DirectionsTravelMode[$($addresses[i]).data('mean').toUpperCase()],
				 unitSystem: google.maps.UnitSystem.METRIC
		 	};
		 
		 if (hour != "" && min != "") {
			 var d = new Date();
			 d.setHours(hour, min);
			 //directionsRequest.arrival_time = d.getTime()/1000;
			 // TODO
		 }
		 
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

function storeSearch(callback) {
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
			meetingType: $POIType.val()
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
				if (callback) {
					callback(newURL);
				}
			}
	    });
}

function serviceUnavailable () {
    unFreeze();
    $errorModal.modal();
}