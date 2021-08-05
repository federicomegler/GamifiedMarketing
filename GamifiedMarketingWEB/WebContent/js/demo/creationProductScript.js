var x=1;


var addQuestion= function()
{
	var form= document.getElementById("productForm");
	var newInput=document.createElement("input");
	newInput.type="text";
	newInput.classList.add("form-control")
	newInput.classList.add("form-control-user")
	newInput.placeholder="Question"+x
	newInput.id="Question"+x;
	newInput.name="Question"+x;
	
	
	form.appendChild(newInput);
	x=x+1;
}

var removeQuestion= function()
{
	if(x>1)
	{
		var elem= document.getElementById("Question"+(x-1));
		elem.remove();
		x=x-1;
	}

}

var submitForm= function()
{
	var form= document.getElementById("productForm");
	var newInput =document.createElement("input")
	var manda=1;
	newInput.type="hidden";
	newInput.id="questionNumber";
	newInput.name="questionNumber";
	newInput.value=x-1;
	form.appendChild(newInput);
	
	var name= document.getElementById("productName");
	var ean= document.getElementById("productEAN");
	var data= document.getElementById("date");
	var img= document.getElementById("prodImage");
	
	function myTrim(x) {
		  return x.replace(/^\s+|\s+$/gm,'');
		}
	
	var temp=name.value.trim();
	
	if(temp=="")
		{
			alert("non lasciare campi vuoti");
			manda=0;
		}
	
	
	
	var str = ean.value;
	for(var i=0; i<str.length; i++)
		{
			if(str[i].charCodeAt(0)<48 || str[i].charCodeAt(0)>57)
				{
					alert("il codice EAN puo essere composto solamente da numeri");
					manda=0;
				}
		}
	
	
	
	if(data.value==0)
		{
			alert("devi selezionare una data");
			manda=0;
		}
	
	if(img.files.length==0)
		{
			alert("devi selezionare un'immagine");
			manda=0;
		}
	
	var question;
	for(var i=1; i<x;i++)
		{
			question=document.getElementById("Question"+i)
			if(question.value=="")
				{
					alert("non puoi lasciare le domande vuote");
					manda=0;
				}
		}
	
	if(manda==1)
		{
			form.submit();
		}
	
}
