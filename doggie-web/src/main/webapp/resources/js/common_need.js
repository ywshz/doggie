var BASE_PATH = '${path }';
//主菜单
var selectMenuIndex = $("#head_menu_index_input").val()
$("#head_menu_index_" + selectMenuIndex).addClass("active");
//边菜单
var selectSidebarIndex = $("#side_bar_index_input").val()
$("#head_submenu_index_" + selectSidebarIndex).addClass("active");
$("#side_bar_index_" + selectSidebarIndex).addClass("active");