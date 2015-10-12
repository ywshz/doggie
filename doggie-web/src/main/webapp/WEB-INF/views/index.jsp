<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <%@include file="common/common_head.jsp" %>
    <link href="${path }/resources/css/zTreeStyle/zTreeStyle.css" rel="stylesheet">
    <link href="${path }/resources/codemirror/lib/codemirror.css" rel="stylesheet">
    <style type="text/css">
        div#rMenu {
            position: fixed;
            visibility: hidden;
            top: 0;
            z-index: 1000
        }
    </style>
</head>
<body>
<%@include file="common/headmenu.jsp" %>

<div class="container-fluid">
    <div class="row">
        <%@include file="common/sidebar.jsp" %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">

            <div id="right-content-div" class="hide">
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                                <div class="panel-heading">操作</div>
                            <div class="panel-body">
                                <button type="button" class="btn btn-primary" id="edit-btn">
                                    <span class=" "></span>编辑
                                </button>

                                <button type="button" class="btn btn-default" id="dependency-btn">
                                    <span class=" "></span>依赖关系图
                                </button>

                                <button type="button" class="btn btn-default" id="manual-run-btn">
                                    <span class=" "></span>手动执行
                                </button>

                                <button type="button" class="btn btn-default" id="resume-run-btn">
                                    <span class=" "></span>手动恢复
                                </button>

                                <button type="button" class="btn btn-default" id="open-close-btn">
                                    <span class=" "></span>开启/关闭
                                </button>
								<!--
								<button type="button" class="btn btn-default" id="alarm-setting-btn">
                                    <span class=" "></span>告警设置
                                </button>
                                -->
                                <button type="button" class="btn btn-default" id="delete-btn">
                                    <span class=" "></span>删除
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">基本信息</div>
                            <div class="panel-body">
                                <table class="table">
                                    <tr>
                                        <td><strong>ID:</strong></td>
                                        <td id="job-id-td">?</td>
                                        <td><strong>任务类型:</strong></td>
                                        <td id="job-type-td">?</td>
                                    </tr>
                                    <tr>
                                        <td><strong>名称:</strong></td>
                                        <td id="name-td">?</td>
                                        <td><strong>调度类型:</strong></td>
                                        <td id="run-type-td">?</td>
                                    </tr>
                                    <tr>
                                        <td><strong>调度状态:</strong></td>
                                        <td id="auto-td">?</td>
                                        <td><strong>依赖/定时:</strong></td>
                                        <td id="run-time-td">?</td>
                                    </tr>
                                    <tr>
                                        <td><strong>分配策略:</strong></td>
                                        <td id="allocation-type-td">自动</td>
                                        <td><strong>执行机器:</strong></td>
                                        <td id="execution-machine-td"></td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel panel-default">
                    <div class="panel-heading">脚本</div>
                    <div class="panel-body">
                        <textarea id="script-p">?</textarea>
                    </div>
                </div>

                <div class="panel panel-default console-panel">
                    <div class="panel-heading">历史日志(最近10条),<a href="javascript:void(0);"
                                                              id="click-refresh-link">点击刷新</a></div>
                    <div class="panel-body">
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>状态</th>
                                <th>运行时间</th>
                                <th>结束时间</th>
                                <th>执行机器</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody id="history-tbody">
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
</div>


<div class="modal fade  bs-example-modal-lg" id="editModal" tabindex="-1" role="dialog"
     aria-labelledby="editModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title" id="editModalLabel">编辑</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form">
                    <div class="form-group">
                        <label for="inputName" class="col-sm-2 control-label">名称</label>

                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="inputName" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-2 control-label">调度类型</label>

                        <div class="col-sm-10">
                            <select class="form-control" id="inputScheduleType">
                                <option value="SHELL">Shell脚本</option>
                                <option value="HIVE">Hive脚本</option>
                                <option value="PYTHON">Python脚本</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-2 control-label">依赖/定时</label>

                        <div class="col-sm-10">
                            <div class="radio">
                                <label>
                                    <input type="radio" name="scheduleType" id="radioSchedualByTime" value="CRON">
                                    定时表达式
                                </label>
                                <input type="text" class="form-control" id="inputCron" value="0 0 0 * * ?">
                            </div>
                            <div class="radio">
                                <label>
                                    <input type="radio" name="scheduleType" id="radioSchedualByDependency" value="DEPENDENCY">
                                    依赖
                                </label>
                                <input type="text" class="form-control" id="dependenciesSel" name="dependencies"
                                       readonly="readonly" onclick="">

                                <div id="menuContent" style="display:none;" class="zTreeDemoBackground left">
                                    <ul id="dependencyTree" class="ztree">
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="" class="col-sm-2 control-label">分配策略:</label>
                        <div class="col-sm-10">
                            <select class="form-control" id="allocationTypeSel">
                                <option value="AUTO">自动</option>
                                <option value="ASSIGN">手动分配</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-2 control-label">执行机器:</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="executionMachineinput" name="executionMachine"
                                   placeholder="若分配策略为自动，此配置无效">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="" class="col-sm-2 control-label">脚本</label>

                        <div class="col-sm-10">
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-sm-2"></div>
                        <div class="col-sm-11">
                            <textarea id="edit-script"></textarea>
                        </div>
                    </div>
                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" id="update-job-btn" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade in" id="logModal" tabindex="-1" role="dialog"
     aria-labelledby="logModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title" id="logModalLabel">运行日志</h4>
            </div>
            <div class="modal-body">
                <div id="log-div" style='height:400px;overflow: auto;'>
                    <p id="log-his-p"></p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

<input type="hidden" id="head_menu_index_input" value="1">
<input type="hidden" id="viewing-job-input"/>
<form action="${path }/dependency_map.jsp" target="_blank" id="dependency_map_form" method="post">
    <input type="hidden" name="id" value="" id="dependency_view_job_id">
</form>
<%@include file="common/common_foot.jsp" %>
<script type="text/javascript" src="${path }/resources/js/jquery.ztree.core-3.5.js"></script>
<script type="text/javascript" src="${path }/resources/js/jquery.ztree.excheck-3.5.min.js"></script>
<script type="text/javascript" src="${path }/resources/js/jquery.ztree.exedit-3.5.min.js"></script>
<script type="text/javascript" src="${path }/resources/codemirror/lib/codemirror.js"></script>
<script type="text/javascript" src="${path }/resources/codemirror/mode/sql/sql.js"></script>
<script type="text/javascript" src="${path }/resources/js/JobTree.js"></script>
<script type="text/javascript" src="${path }/resources/js/index.js"></script>
</body>
</html>
