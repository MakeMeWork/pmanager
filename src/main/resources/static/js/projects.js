$(function(){

	var content = $("#projects"),
    		loading = "<img src='/img/loading.gif' alt='Loading' />";

	// Подгрузка первых записей
	$.ajax({
		url: "project/load",
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
				$("#projects").jscroll({
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