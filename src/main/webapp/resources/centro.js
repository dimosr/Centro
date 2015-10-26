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
	$addressContainer = $('#address-container');

$('#address-form').on('submit', function(e){
      e.preventDefault();
      
      var address = $address.val(),
      	  url = 'https://maps.googleapis.com/maps/api/geocode/json?address=' + encodeURIComponent(address);

      $.ajax({
    	  url: url,
    	  success: function(obj){
    		  if (obj.status == 'OK') {
    			  var results = obj.results[0];
    			  $addressContainer.append('<div class="alert alert-info alert-dismissible address" data-lat="'+results.geometry.location.lat+'" data-lng="'+results.geometry.location.lng+'">'
    					  					+'<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
    					  					+results.formatted_address
					  						+'</div>')
    			  $address.val(''); 
    		  } else {
    			  alert('Address not found, try to be more precise.')
    		  }
    	  }
      });
});