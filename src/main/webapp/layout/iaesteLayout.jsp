<%--

    Copyright © 2002 Instituto Superior Técnico

    This file is part of FenixEdu Core.

    FenixEdu Core is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Core is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.

--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:html xhtml="true">
<head>

<title>
<tiles:useAttribute name="title" id="titleK" ignore="true" />
<tiles:useAttribute name="bundle" id="bundleT" ignore="true" />
<logic:present name="bundleT">
	<logic:present name="titleK">
		<bean:message name="titleK" bundle="<%= bundleT.toString() %>" /> -
	</logic:present>
</logic:present>
<logic:notPresent name="bundleT">
	<tiles:getAsString name="title" ignore="true" />
</logic:notPresent>
<%=net.sourceforge.fenixedu.domain.organizationalStructure.Unit.getInstitutionName()%>
</title> <%-- TITLE --%>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath() %>/CSS/iststyle.css" />
<link rel="stylesheet" type="text/css" media="print" href="<%= request.getContextPath() %>/CSS/iststyle_print.css" />
<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath() %>/CSS/webservice.css" />
<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath() %>/CSS/calendar.css" />

</head>
<body>

<style>
h1 a:link, h1 a:visited, h1 a:hover {
background: url(<%= request.getContextPath() %>/images/logo_iaeste.gif) no-repeat top left;
width: 96px;
height: 99px;
display: block;
text-decoration: none;
border: none;
overflow: hidden;
}
</style>


<p class="skipnav"><a href="#main">Saltar men&uacute; de navega&ccedil;&atilde;o</a></p>
<!-- START HEADER -->
    <div id="logoist">
        <h1><a href="#">IAESTE</a></h1>
        <!-- <img alt="[Logo] Instituto Superior Técnico" height="51" src="<%= net.sourceforge.fenixedu.domain.Installation.getInstance().getInstituitionURL() %>img/wwwist.gif" width="234" /> -->
    </div>
<!-- END HEADER -->
<!--START MAIN CONTENT -->

<div id="container_800px">
<div id="wrapper">

<tiles:insert attribute="body" ignore="true"/>

</div> <!-- #wrapper -->
</div> <!-- #container -->

</body>
</html:html>