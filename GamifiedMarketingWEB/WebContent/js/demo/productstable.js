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
            "defaultContent": "<a class=\"btn btn-primary btn-circle\"> <i style=\"color:white\" class=\"fas fa-search\"></i> </a>"
        	} ]
		});
		
		$('#products tbody').on( 'click', 'a', function () {
        var data = table.row( $(this).parents('tr') ).data();
		var id = data[0];
		$.get("GetInfoUserProduct", {"id" : id} , function(responseJson) {
        data = responseJson;
		console.log(data);
		$('#submit').DataTable().destroy();
		$('#cancelled').DataTable().destroy();
		
		$('#submit').DataTable({
			data: data['submit']
			});
		$('#cancelled').DataTable({
			data: data['cancelled']
		});
		});
		
		$.get("GetQNA", {"id" : id}, function(response){
			console.log(response);
		});
		
    } );
		
});



