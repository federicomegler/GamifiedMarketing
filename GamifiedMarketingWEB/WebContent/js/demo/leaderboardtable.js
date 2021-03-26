$.get("GetLeaderboard", function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
        data = responseJson;
		console.log(data);
		$('#leaderboard').DataTable({
			data: data
			});
    });
