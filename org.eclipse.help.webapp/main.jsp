<%@ page import="java.util.*,org.eclipse.help.servlet.*,org.w3c.dom.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);

	LayoutData layout = new LayoutData(application,request);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=WebappResources.getString("Help", request)%></title>
<script language="JavaScript" src="help_js.jsp"></script>
</head>

<frameset onload="onloadFrameset()"  rows="layout.getBannerHeight()%>,24,*"  frameborder="0" framespacing="0" border=0 spacing=0 style="border:1px solid WindowText;">
	<frame name="BannerFrame" src='<%=layout.getBannerURL()%>'  marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize=0>
	<frame name="SearchFrame" src='<%="search.jsp"+layout.getQuery()%>'  marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize=0>
	<frame name="HelpFrame" src='<%="help.jsp"+layout.getQuery()%>'  marginwidth="0" marginheight="0" scrolling="no" frameborder="0" >
</frameset>

</html>

