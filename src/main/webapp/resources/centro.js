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
	$firstDescContainer = $('#first-desc'),
	$sndDescContainer = $('#snd-desc'),
	bounds = new google.maps.LatLngBounds(),
	markers = [];

$('#address-form').on('submit', function(e){
      e.preventDefault();
      
      var address = $address.val(),
      	  url = 'https://maps.googleapis.com/maps/api/geocode/json?address=' + encodeURIComponent(address);

      $.ajax({
    	  url: url,
    	  success: function(obj){
    		  if (obj.status == 'OK') {
    			  var results = obj.results[0],
    			  	  lat = results.geometry.location.lat,
    			  	  lng = results.geometry.location.lng;
    			  
    			  $addressContainer.append('<div class="address" data-lat="'+lat+'" data-lng="'+lng+'" data-marker="'+markers.length+'">'
    					  					+'<div class="delete" aria-label="delete"><span aria-hidden="true">&times;</span></div>'
    					  					+results.formatted_address
					  						+'</div>');
    			  
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

var $submit = $('#submit');

$submit.on('click', function() {
	var json = [];
	$('.address').each(function() {
		var $this = $(this);
		json.push({'latitude':$this.data('lat'), 'longitude': $this.data('lng')});
	});
	
	$.ajax({
  	  url: 'api/central',
  	  dataType : 'json',
  	  type: 'POST',
	  success: function(obj){
		 // PROCESS RESULT HERE
		  alert();
		  console.log(obj);
	  }
	});
});
