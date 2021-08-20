/**
 * 
 */
var map;
$.get("GetProducts", function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
        if(responseJson == "error"){
			alert("Unable to load products! Server error.")
		}
		else{
			data = responseJson;
		console.log(data);
		var table = $('#products').DataTable({
			data: data,
			pageLength: 5,
			lengthMenu: [[5,10,20], [5,10,20]],
			"columnDefs": [ {
            "targets": -1,
            "data": null,
            "defaultContent": "<a class=\"btn btn-primary btn-circle\"> <i style=\"color:white\" class=\"fas fa-search\"></i> </a>"
        	},
			{
				"targets": 2,
				"type": "date"
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
			if(response == "error"){
				alert("Server error! Unable to load QnA.")
			}
			else{
				map = response;
			questions = Object.keys(map);
			document.getElementById("question_space").innerHTML = "";
			for(let i in questions){
				quest = document.createElement('b');
				quest.innerHTML = questions[i];
				quest.style = "color:black;"
				document.getElementById("question_space").appendChild(quest);
				users = Object.keys(map[questions[i]]);
				
				for(let j in users){
					u_name = document.createElement('p');
					u_name.innerHTML = users[j];
					u_name.style = "color:#4e73df;text-indent:15px;";
					document.getElementById("question_space").appendChild(u_name);
					
					ans = document.createElement("p");
					ans.innerHTML = map[questions[i]][users[j]];
					ans.style = "text-indent:30px;"
					document.getElementById("question_space").appendChild(ans);
				}
			}
			console.log(response);
			}
			
		});
		
    } );
	
	
	}
		
		
});



