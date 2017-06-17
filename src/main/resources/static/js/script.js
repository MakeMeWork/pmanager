function makeEditable(id, btnId){
	$('#'+id).attr("contenteditable",true);
	$('#'+btnId).attr("onclick","makeNoEditable(\'"+id+"\',\'"+btnId+"\')");
	$('#'+btnId).text("Save");
}
function makeNoEditable(id, btnId){
	$('#'+id).attr("contenteditable",false);
	$('#'+btnId).attr("onclick","makeEditable(\'"+id+"\',\'"+btnId+"\')");
	$('#'+btnId).text("Change");
	$.post('change',{
		content		: $('#'+id).text(),
		name        : $('#prName').text()
	})
}