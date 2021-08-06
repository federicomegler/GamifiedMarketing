var submitForm=function()
{
	var form=document.getElementById("questionnaireForm");
	form.submit();
}

var cancelForm= function()
{
	window.location.replace("Home");
}

var goPrev=function()
{
	var div1=document.getElementById("part1");
	var div2=document.getElementById("part2");
	
	div2.style="display:none";
	div1.style="display:blocks";
}

var goNext= function()
{
	var div1=document.getElementById("part1");
	var div2=document.getElementById("part2");
	
	div2.style="display:block";
	div1.style="display:none";
}