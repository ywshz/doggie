(function () {
    var IndexPage = {
        editor: null,
        scriptView: null,
        jobEntity: {
            data: {
                id: null,
                name: null,
                jobType: null,
                scheduleType: null,
                scheduleStatus: null,
                cron: null,
                dependencies: null,
                script: null,
                createTime: null,
                allocationType: null,
                executionMachine: null
            },
            build: function (job) {
                IndexPage.jobEntity.data = job;

                //if(this.data.jobType=='SHELL') this.data.jobType=0;
                //else if(this.data.jobType=='HIVE') this.data.jobType=1;
                //else if(this.data.jobType=='PYTHON') this.data.jobType=2;
                //
                //if(this.data.scheduleType=='CRON') this.data.scheduleType=0;
                //else if(this.data.scheduleType=='DEPENDENCY') this.data.scheduleType=1;
                //
                //if(this.data.scheduleStatus=='ON') this.data.scheduleStatus=0;
                //else if(this.data.scheduleStatus=='OFF') this.data.scheduleStatus=1;
                //
                //if(this.data.allocationType=='AUTO') this.data.allocationType=0;
                //else if(this.data.allocationType=='ASSIGN') this.data.allocationType=1;
            }
        },
        treeObject : null,
        initEditor: function () {
            editor = CodeMirror.fromTextArea(document.getElementById("edit-script"), {
                lineNumbers: true,
                mode: 'text/x-hive',
                indentWithTabs: true,
                smartIndent: true,
                matchBrackets: true,
                autofocus: true,
                width: '100%',
                height: '400px'
            });

            scriptView = CodeMirror.fromTextArea(document.getElementById("script-p"), {
                lineNumbers: true,
                mode: 'text/x-hive',
                indentWithTabs: true,
                smartIndent: true,
                matchBrackets: true,
                autofocus: true,
                readOnly: true
            });
        },
        initButtonsEvent: function () {
            $("#edit-btn").click(function () {
                $('#editModal').modal({
                    backdrop: 'static',
                    keyboard: false
                });
                //从隐藏层显示,需要延迟刷新,否则仍无法正常显示
                setTimeout(function () {
                    editor.refresh();
                }, 200);

                var dt = $.fn.zTree.getZTreeObj("dependencyTree");
                dt.reAsyncChildNodes(null, "refresh", true);
            });

            //依赖图按钮
            $("#dependency-btn").click(function () {
                $("#dependency_view_job_id").val($("#viewing-job-input").val());
                $("#dependency_map_form").submit();
            });

            $("#manual-run-btn").click(function () {
                var org = $("#manual-run-btn").html();
                $("#manual-run-btn").attr("disabled", "disabled");
                setTimeout(function () {
                    refreshHistoryView($("#viewing-job-input").val());
                }, 2000)
                $.post(BASE_PATH + "/jobs/manualrun.do", {jobId: $("#viewing-job-input").val()}, function (res) {
                    alert("已进入任务队列");
                    $("#manual-run-btn").removeAttr("disabled");
                    $("#manual-run-btn").html(org);
                    refreshHistoryView($("#viewing-job-input").val());
                });
            });

            $("#resume-run-btn").click(function () {
                var org = $("#resume-run-btn").html();
                $("#resume-run-btn").attr("disabled", "disabled");
                $("#resume-run-btn").html("运行中...");
                setTimeout(function () {
                    refreshHistoryView($("#viewing-job-input").val());
                }, 2000)
                $.post(BASE_PATH + "/jobs/resumerun.do", {jobId: $("#viewing-job-input").val()}, function (res) {
                    $("#resume-run-btn").removeAttr("disabled");
                    $("#resume-run-btn").html(org);
                    if (res) {
                        alert("已加入运行队列");
                    } else {
                        alert("ERROR:运行失败");
                    }
                });
            });
            $("#open-close-btn").click(function () {
                var org = $("#open-close-btn").html();
                $("#open-close-btn").attr("disabled", "disabled");
                $("#open-close-btn").html("处理中...");
                $.post(BASE_PATH + "/jobs/openclosejob.do", {id: $("#viewing-job-input").val()}, function (res) {
                    $("#open-close-btn").removeAttr("disabled", "disabled");
                    $("#open-close-btn").html(org);
                    if ("opened" == res) {
                        $("#auto-td").html("开启");
                        alert("开启成功");
                    }
                    if ("closed" == res) {
                        $("#auto-td").html("关闭");
                        alert("关闭成功");
                    }
                });
            });
            $("#delete-btn").bind("click",this.removeJob);

            $("#update-job-btn").bind("click", this.updateJob);

            $("#dependenciesSel").click(function () {
                IndexPage.showDependencyTree();
            });

            $("#click-refresh-link").click(function () {
                IndexPage.refreshHistoryView(IndexPage.lastViewJob);
            });
        },
        initTree: function () {
            this.treeObject = $.fn.zTree.getZTreeObj("tree");
            JobTree.onFileClick = function (fileId) {
                IndexPage.loadJob(fileId);
            }

            JobTree.onAddFolder = function (fileId) {
                $.post(BASE_PATH + "/file/save", {"parent": fileId, name: "新文件夹", fileType: 1}, function (res) {
                    if (res.succeed) {
                        JobTree.addTreeNode({id: res.data, name: "新文件夹", isParent: true});
                    } else {
                        Noty.error("操作发生异常:" + res.message);
                    }
                });
            }

            JobTree.onAddFile = function (fileId) {
                $.post(BASE_PATH + "/file/save", {"parent": fileId, name: "新Job", fileType: 0}, function (res) {
                    if (res.succeed) {
                        JobTree.addTreeNode({id: res.data, name: "新Job", isParent: false});
                    } else {
                        Noty.error("操作发生异常:" + res.message);
                    }
                });
            }

            $.fn.zTree.init($("#dependencyTree"), {
                check: {
                    enable: true,
                    chkboxType: {"Y": "", "N": ""}
                },
                async: {
                    enable: true,
                    url: BASE_PATH + "/tree/list_files.do",
                    autoParam: ["id"],
                    otherParam: {"properties": "fileType", "directions": "desc"}
                },
                view: {
                    dblClickExpand: false
                },
                callback: {
                    onNodeCreated: function (event, treeId, treeNode) {
                        var zTree = $.fn.zTree.getZTreeObj("dependencyTree");
                        if (treeNode.isParent)
                            zTree.setChkDisabled(treeNode, true);
                    },
                    beforeClick: function (treeId, treeNode) {
                        var zTree = $.fn.zTree.getZTreeObj("dependencyTree");
                        zTree.checkNode(treeNode, !treeNode.checked, null, true);
                        return false;
                    },
                    onCheck: function (e, treeId, treeNode) {
                        var zTree = $.fn.zTree.getZTreeObj("dependencyTree"),
                            nodes = zTree.getCheckedNodes(true),
                            v = "";
                        for (var i = 0, l = nodes.length; i < l; i++) {
                            v += nodes[i].id + ",";
                        }
                        if (v.length > 0) v = v.substring(0, v.length - 1);
                        $("#dependenciesSel").val(v);
                    }
                }
            });

        },
        init: function () {
            this.initEditor();
            this.initButtonsEvent();
            this.initTree();
        },
        lastViewFile: '',
        lastViewJob: '',
        loadJob: function (fileId,forceLoad) {
            if (forceLoad==false && this.lastViewFile == fileId) {
                return;
            }
            this.lastViewFile = fileId;
            $.post(BASE_PATH + "/job/get_by_file_id", {fileId: fileId}, function (job) {
                if (job == "" || job == null) {
                    $("#right-content-div").addClass("hide");
                } else {
                    IndexPage.lastViewJob = job.id;
                    $("#right-content-div").removeClass("hide");

                    IndexPage.jobEntity.build(job);
                    IndexPage.freshJobView(job);
                    IndexPage.refreshHistoryView(job.id);
                }
            });
        },
        freshJobView: function (data) {
            $("#job-id-td").text(data.id);
            $("#job-type-td").text(data.jobType);
            $("#name-td").text(data.name);
            $("#run-type-td").text(data.scheduleType == 'CRON' ? "定时调度" : "依赖调度");
            $("#auto-td").text(data.scheduleStatus == 'OFF' ? "关闭" : "开启");
            $("#run-time-td").text(data.scheduleType == 'CRON' ? data.cron : data.dependencies);
            $("#allocation-type-td").text(data.allocationType == 'AUTO' ? '自动' : '指定');
            $("#execution-machine-td").text(data.allocationType != 'AUTO' ? data.executionMachine : '自动');

            scriptView.setValue(data.script == null ? "" : data.script);
            $("#viewing-job-input").val(data.id);

            $("#inputName").val(data.name);
            $("#inputScheduleType").val(data.jobType);

            $("#allocationTypeSel").val(data.allocationType);
            $("#executionMachineinput").val(data.executionMachine);
            if (data.scheduleType == 'CRON') {
                $("#radioSchedualByTime").prop("checked", true);
                $("#radioSchedualByDependency").prop("checked", false);
                $("#inputCron").val(data.cron);
            } else {
                $("#radioSchedualByTime").prop("checked", false);
                $("#radioSchedualByDependency").prop("checked", true);
                $("#dependenciesSel").val(data.dependencies);
            }
            editor.setValue(data.script == null ? "" : data.script);
        },
        refreshHistoryView: function (jobId) {
            $.post(BASE_PATH + "/job/get_history_list", {jobId: jobId}, function (data) {
                $("#history-tbody").html("");

                $.each(data, function (key, his) {

                    var td = "<tr><td>" + his.id + "</td><td>" + his.result + "</td><td>" + BkUtils.formatDate(his.startTime, "yyyy-MM-dd hh:mm:ss") + "</td><td>" + BkUtils.formatDate(his.endTime, "yyyy-MM-dd hh:mm:ss") + "</td><td>" + his.executionMachine + "</td><td>";
                    td += '<button type="button" class="btn btn-default btn-xs" onclick="viewLog(' + his.id + ')">查看日志</button>';
                    if (his.status == "RUNNING") {
                        td += ',<button type="button" class="btn btn-primary btn-xs" onclick="killJob(' + his.id + ')">取消任务</button>';
                    }
                    td += "</td></tr>"
                    $("#history-tbody").append(td);
                });
            });
        },
        viewLog: function (historyId) {
            if (refreshJobLogTimer != undefined) {
                clearTimeout(refreshJobLogTimer);
                $("#log-his-p").html("");
                document.getElementById('log-div').scrollTop = 0;
            }

            $.post(BASE_PATH + "/jobs/gethistorylog.do", {historyId: historyId}, function (res) {
                $("#log-his-p").html("");
                $("#logModal").modal("show");

                if (res.status == "SUCCESS" || res.status == "FAILED") {
                    var cr = $("#log-his-p").html();
                    var nr = res.log.replace(/\n/g, "<br>");
                    $("#log-his-p").html(nr);
                    document.getElementById('log-div').scrollTop = document.getElementById('log-div').scrollHeight;
                } else {
                    refreshJobLogTimer = setInterval(function () {
                        $.post(BASE_PATH + "/jobs/gethistorylog.do", {historyId: historyId}, function (res) {
                            var cr = $("#log-his-p").html();
                            var nr = res.log.replace(/\n/g, "<br>");
                            if (cr != nr) {
                                $("#log-his-p").html(nr);
                                document.getElementById('log-div').scrollTop = document.getElementById('log-div').scrollHeight;
                            }
                            if (res.status == "SUCCESS" || res.status == "FAILED") {
                                clearTimeout(refreshJobLogTimer);
                                refreshHistoryView($("#viewing-job-input").val());
                            }
                        });

                    }, 1000);
                }

            });
        },
        updateJob: function () {
            var param = {};
            param.id=IndexPage.jobEntity.data.id;
            param.name = $("#inputName").val()
            param.jobType = $("#inputScheduleType").val()
            param.scheduleType = $('input[type="radio"][name="scheduleType"]:checked').val()
            param.cron = $("#inputCron").val()
            param.dependencies = $("#dependenciesSel").val()
            param.script = editor.getValue()
            param.allocationType = $("#allocationTypeSel").val()
            param.executionMachine = $("#executionMachineinput").val()

            $.post(BASE_PATH + "/job/update_job.do", param,
                function (res) {
                    if (!res.succeed) {
                        Noty.error("操作失败!" + res.message);
                        return;
                    }

                    var nodes = IndexPage.treeObject.getSelectedNodes()
                    nodes[0].name = param.name;
                    IndexPage.treeObject.updateNode(nodes[0]);

                    $('#editModal').modal('hide');
                    var forceReload = true;
                    IndexPage.loadJob(IndexPage.lastViewFile,forceReload);
                    Noty.info("修改成功!");
                });
        },
        showDependencyTree: function () {
            $("#menuContent").show();
            $(window).bind("mousedown", function (event) {
                if (!(event.target.id == "dependenciesSel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length > 0)) {
                    IndexPage.hideDependencyTree();
                }
            });
        },

        hideDependencyTree: function () {
            $("#menuContent").show();
            $(window).unbind("mousedown");
        },
        removeJob : function(){
            $.post(BASE_PATH + "/job/delete", {jobId: IndexPage.lastViewJob}, function (res) {
                if (res.succeed) {
                    $("#right-content-div").addClass("hide");
                    var nodes = IndexPage.treeObject.getSelectedNodes();
                    for (var i = 0, l = nodes.length; i < l; i++) {
                        IndexPage.treeObject.removeNode(nodes[i]);
                    }
                    Noty.info("删除成功");
                } else {
                    Noty.error("删除失败，" + res.message);
                }
            });
        }

    }

    IndexPage.init();
}());
