var JobTree = {
    treeObject : null,
    selectedNode: null,
    rMenu: $("#rMenu"),
    init: function () {
        $.fn.zTree.init($("#tree"), {
            async: {
                enable: true,
                url: BASE_PATH + "/tree/list_files.do",
                autoParam: ["id"],
                otherParam: {"properties": "fileType", "directions": "desc"}
            },
            view: {
                selectedMulti: false
            },
            edit: {
                enable: true,
                showRemoveBtn: function (treeId, treeNode) {
                    return treeNode.isParent;
                },
                showRenameBtn: function (treeId, treeNode) {
                    return treeNode.isParent;
                }
            },
            callback: {
                onAsyncSuccess: function (event, treeId, treeNode, msg) {
                    //展开根节点
                    if (treeNode == null) {
                        zTree = $.fn.zTree.getZTreeObj("tree");
                        var rootNode = zTree.getNodes()[0];
                        zTree.expandNode(rootNode, true, true, true);
                    } else {
                        if (treeNode.folder) {
                            treeNode.isParent = true;
                        }
                    }
                },
                onClick: OnLeftClick,
                onRename: zTreeOnRename,
                //onRemove: zTreeOnRemove,
                beforeRemove: zTreeBeforeRemove,
                //onExpand: zTreeOnExpand,
                onRightClick: rightClick
            }
        });
        this.treeObject =  $.fn.zTree.getZTreeObj("tree");
        this.hideRightClickMenu();
        initContextMenuFunction();
    },
    showRightClickMenu: function (isRoot, isFolder, x, y) {
        var nodes =  this.treeObject.getSelectedNodes();
        JobTree.selectedNode = nodes[0];

        $("#rMenu button").show();
        if (isFolder) {
            //$("#add-group-btn").hide();
            $("#add-job-btn").show();
            $("#add-group-btn").show();
        } else {
            $("#add-group-btn").hide();
            $("#add-job-btn").hide();
        }
        if (isRoot) {
            $("#add-job-btn").show();
            $("#add-group-btn").show();
        }
        $(rMenu).css({"top": y + "px", "left": x + "px", "visibility": "visible"});
        $("body").bind("mousedown", onBodyMouseDown);
        $(window).bind("mousedown", onBodyMouseDown);
    },
    hideRightClickMenu: function () {
        if (rMenu) $(rMenu).css({"visibility": "hidden"});
        $("body").unbind("mousedown", onBodyMouseDown);
        $(window).unbind("mousedown", onBodyMouseDown);
    },
    addTreeNode : function(newNode){
        //this.treeObject.addNodes(this.treeObject.getSelectedNodes()[0], newNode);
        this.treeObject.reAsyncChildNodes(this.selectedNode, "refresh");
    },
    onFileClick: function (fileId) {

    },
    onAddFolder: function (fileId) {

    },
    onAddFile: function (fileId) {

    }
};

JobTree.init();

zTree = $.fn.zTree.getZTreeObj("tree");

function rightClick(event, treeId, treeNode) {
    if (treeNode == null) return;
    zTree.selectNode(treeNode);
    JobTree.showRightClickMenu(treeNode.getParentNode() == null, treeNode.isParent, event.clientX, event.clientY)
}

function initContextMenuFunction() {
    $("#add-group-btn").click(function(){
        JobTree.hideRightClickMenu();
        JobTree.onAddFolder(JobTree.selectedNode.id);
    });
    $("#add-job-btn").click(function(){
        JobTree.hideRightClickMenu();
        JobTree.onAddFile(JobTree.selectedNode.id);
    });
}

function refreshNode(type, silent) {
    var zTree = $.fn.zTree.getZTreeObj("tree"),
        nodes = zTree.getSelectedNodes();
    if (nodes.length == 0) {
        alert("请先选择一个父节点");
    }
    for (var i = 0, l = nodes.length; i < l; i++) {
        zTree.reAsyncChildNodes(nodes[i], type, silent);
        if (!silent) zTree.selectNode(nodes[i]);
    }

    //编辑页面的依赖树
//   var dt = $.fn.zTree.getZTreeObj("dependencyTree");
//   dt.reAsyncChildNodes(null , "refresh", true);
}


function OnLeftClick(event, treeId, treeNode) {
    JobTree.selectedNode = treeNode;
    if (!treeNode.isParent) {
        JobTree.onFileClick(treeNode.id);
    }
}


function onBodyMouseDown(event) {
    if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length > 0)) {
        $(JobTree.rMenu).css({"visibility": "hidden"});
    }
}

function zTreeOnRename(event, treeId, treeNode, isCancel) {
    $.post(BASE_PATH + "/file/rename", {id: treeNode.id, name: treeNode.name}, function (res) {
        if (res.succeed) Noty.info("重命名成功.");
        else Noty.error("重命名失败，请刷新页面重试.");
    });
}


function zTreeBeforeRemove(treeId, treeNode) {
    if(confirm("确认删除？")){
        $.post(BASE_PATH + "/file/delete.do", {id: treeNode.id}, function (res) {
            if (res.succeed) {
                Noty.info("删除成功");
                JobTree.treeObject.removeNode(treeNode);
            } else {
                Noty.error("删除失败，" + res.message);
            }
        });
    }
    return false;
}

function zTreeOnExpand(event, treeId, treeNode) {
    $.post(BASE_PATH + "/tree/list_files", {
        parent: treeNode.id,
        properties: 'fileType',
        directions: 'desc'
    }, function (res) {
        $.each(res, function (key, data) {
            var nodes = zTree.getNodesByParam("id", data);
            for (var i = 0, l = nodes.length; i < l; i++) {
                if (nodes[i].isParent == false) {
                    zTree.setting.view.fontCss = {};
                    zTree.setting.view.fontCss["color"] = "green";
                    zTree.updateNode(nodes[i]);
                    zTree.setting.view.fontCss = {};
                    break;
                }
            }
        });
    });
}