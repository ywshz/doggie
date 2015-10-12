(function() {
	var WorkersPage = {
		init : function() {
			WorkersPage.loadData();
			WorkersPage.initButtonEvent();
			WorkersPage.initMessage();
			
		},
		loadData : function(){
			$.post(BASE_PATH + "/workers/list",function(res){
				$.each(res,function(i,v){
					var row = "<tr>";
					row += '<td><input type="text" class="form-control" id="name_'+v.id+'" placeholder="名称" value="'+v.name+'"></td>'
					row += '<td><input type="text" class="form-control" id="url_'+v.id+'" placeholder="http://127.0.0.1:8082" value="'+v.url+'"></td>';
					row += '<td><button type="button"  data="'+v.id+'" class="update-btn btn btn-default">更新</button> &nbsp;';
					row += '<button type="button"  data="'+v.id+'" class="delete-btn btn btn-default">删除</button></td>';
					row += '</tr>';
					$(row).prependTo($("#worker-list-tbody"));
				});
				
				$(".update-btn").click(function(){
					$("#update-form input[name='id']").val($(this).attr("data"));
					$("#update-form input[name='name']").val($("#name_"+$(this).attr("data")).val());
					$("#update-form input[name='url']").val($("#url_"+$(this).attr("data")).val());
					$("#update-form").submit();
				});
				
				$(".delete-btn").click(function(){
					$("#delete-form input[name='id']").val($(this).attr("data"));
					$("#delete-form").submit();
				});
			});
		},
		initButtonEvent : function(){
			$(".save-btn").click(function(){
				$("#add-form input[name='name']").val($("#name-add-input").val());
				$("#add-form input[name='url']").val($("#url-add-input").val());
				$("#add-form").submit();
			});
		},
		initMessage : function(){
			if($("#status").val() == 'false'){
				Noty.error($("#message").val());
			}else if($("#status").val() == 'true'){
				Noty.info("操作成功!");
			}
		}
	};

	WorkersPage.init();
}());
