/**
 * 
 */
$.get("GetProducts", function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
        data = responseJson;
		console.log(data);
		var table = $('#products').DataTable({
			data: data,
			"columnDefs": [ {
            "targets": -1,
            "data": null,
            "defaultContent": "<i class=\"fas fa-search\"></i>"
        	} ]
		});
		
		$('#products tbody').on( 'click', 'i', function () {
        var data = table.row( $(this).parents('tr') ).data();
        alert( data[0] +"'s salary is: "+ data[2] );
    } );
		
    });
