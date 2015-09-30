<%@page import="org.springframework.util.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	pageContext.setAttribute("path", path);
	pageContext.setAttribute("basePath", basePath);
%>

<%@include file="meta.html" %>
<%@include file="title.html" %>
<%@include file="basic_style.html" %>
<%@include file="html5_shim.html" %>
