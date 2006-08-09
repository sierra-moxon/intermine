function setFavouriteTemplate(templateName, image){
	AjaxServices.setFavouriteTemplate(templateName);
	image.src='images/star_active.gif';
	image.onclick='';
	image.style.cursor='';
	image.title='This template is a favourite';
}

function precomputeTemplate(templateName){
	document.getElementById('precompute_'+templateName).innerHTML="Precomputing..";
	AjaxServices.preCompute(templateName,function(str) { 
		document.getElementById('precompute_'+templateName).style.color="#777";
		document.getElementById('precompute_'+templateName).innerHTML="Precomputed";
	 });
}

function renameElement(name, type, index){
	document.getElementById('form_'+name).style.display="none";
	document.getElementById('name_'+name).innerHTML="<i>saving...</i>";
	document.getElementById('name_'+name).style.display="block";
	AjaxServices.rename(name,type, document.getElementById('newName_'+name).value, function(str){
		document.getElementById('name_'+name).innerHTML=str;
		document.getElementById('selected_bag_'+index).value=str;
	});
}