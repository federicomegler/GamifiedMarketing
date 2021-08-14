$.get("GetMyProducts", function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
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
        	} ]
		});
		
		$('#products tbody').on( 'click', 'a', function () {
        var data = table.row( $(this).parents('tr') ).data();
		var id = data[0];
		
		$.get("GetMyQNA", {"id" : id}, function(response){
			map = response;
			questions = Object.keys(map);
			document.getElementById("question_space").innerHTML = "";
			btn = document.getElementById("btn-delete");
			if(questions.length == 0){
				btn.style = "display:none;";
				btn.href = "#";
			}
			else{
				
				for(let i in questions){
					quest = document.createElement('b');
					quest.innerHTML = questions[i];
					document.getElementById("question_space").appendChild(quest);
					document.getElementById("question_space").appendChild(document.createElement('br'));
					answer = document.createElement('p');
					answer.innerHTML = map[questions[i]];
					document.getElementById("question_space").appendChild(answer);
					document.getElementById("question_space").appendChild(document.createElement('br'));
				}
				
				btn.style = "dysplay:block;";
				btn.href = "DeleteMyQuestionnaire?id=" + id;
			}
			
			
			console.log(response);
		});
		
    } );
		
});