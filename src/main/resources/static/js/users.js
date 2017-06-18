$(function(){

	var content = $("#users"),
    		loading = "<img src='/img/loading.gif' alt='Loading' />";

	// Подгрузка первых записей
	$.ajax({
		url: "profile/load",
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
				$("#users").jscroll({
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