
var data

$.get("GetReviews", function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
        data = responseJson;
		console.log(data);
		$('#reviewtable').DataTable({
			data: data,
			pageLength: 5,
			lengthMenu: [[5,10,20], [5,10,20]]
			});
    });
