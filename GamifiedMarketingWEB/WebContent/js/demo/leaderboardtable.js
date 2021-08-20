$.get("GetLeaderboard", function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
		
		data = responseJson;        
		if(responseJson == "error"){
			alert("Unable to load leaderboard!");
			data = [];
		}
		
		
		console.log(data);
		$('#leaderboard').DataTable({
			data: data
		});
		
		
    });
