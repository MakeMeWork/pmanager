$(function(){

	var content = $("#content"),
    		loading = "<img src='/img/loading.gif' alt='Loading' />";

	// Подгрузка первых записей
	$.ajax({
		url: "/newsload",
		dataType: "text",
		type: "GET",
		data: {type: "start"},
		success: function(data){

			if(data){

				content.append(data);
				content.find(".jscroll-loading:first").slideUp(700, function(){

					$(this).remove();
				});

				// Вызываем плагин
				$("#content").jscroll({
					autoTriggerUntil: 2,
					loadingHtml: loading
				});
			};
		},
		beforeSend: function(){
			content.append("<div class='jscroll-loading'>" + loading + "</div>");
		}
	});
});
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
function getWiki(){
    $('#wikiarea').html("<textarea class=\"form-control\" rows=\"7\" id=\"wikitext\"></textarea>");
    $.post('wiki',
    {
        name        : $('#prName').text()
    },function(wiki){
        $('#wikitext').val(wiki);
        $('#changeWiki').attr("onclick","saveWiki()");
        $('#changeWiki').text("Save");
    }
    )
}
function saveWiki(){
    var data = {
        name        : $('#prName').text(),
        wiki        : $('#wikitext').val()
    }
    $.post('swiki',data,function(wiki){
            $('#wikiarea').html(wiki);
            $('#changeWiki').attr("onclick","getWiki()");
            $('#changeWiki').text("Change");
    }
    )
}