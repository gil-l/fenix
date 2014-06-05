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
<%@ page language="java" %>
<%@ page import="net.sourceforge.fenixedu.presentationTier.Action.resourceAllocationManager.utils.PresentationConstants" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<em><bean:message key="label.teacherPortal"/></em>
<h2><bean:message key="title.teacherInformation"/></h2>
<html:form action="/teachingCareer">

<h3>
	<logic:present name="infoTeachingCareer">
	<bean:message key="message.teachingCareer.edit" />
	</logic:present>
	<logic:notPresent name="infoTeachingCareer">
	<bean:message key="message.teachingCareer.insertCareer" />
	</logic:notPresent>
	<logic:present name="categories">
</h3>

<p class="infoop"><span class="emphasis-box">1</span>
<bean:message key="message.teachingCareer.managementEdit" /></p>
	<span class="error"><!-- Error messages go here -->
		<html:errors/>
	</span>
	<html:hidden bundle="HTMLALT_RESOURCES" altKey="hidden.page" property="page" value="1"/>
	<html:hidden bundle="HTMLALT_RESOURCES" altKey="hidden.method" property="method" value="edit"/>
	<html:hidden bundle="HTMLALT_RESOURCES" altKey="hidden.externalId" property="externalId"/>
	<html:hidden bundle="HTMLALT_RESOURCES" altKey="hidden.infoTeacher#externalId" property="infoTeacher#externalId"/>

<table class="tstyle5">
	<tr>
		<td><bean:message key="message.teachingCareer.years" />:</td>
		<td>
			<html:text bundle="HTMLALT_RESOURCES" altKey="text.beginYear" property="beginYear"/> - 
			<html:text bundle="HTMLALT_RESOURCES" altKey="text.endYear" property="endYear"/>
		</td>
	</tr>
	<tr>
		<td><bean:message key="message.teachingCareer.category" />:</td>
		<td>
			<html:select bundle="HTMLALT_RESOURCES" property="infoCategory#externalId">
				<html:options collection="categories" property="externalId" labelProperty="shortName"/>
			</html:select>
		</td>
	</tr>
	<tr>
		<td><bean:message key="message.teachingCareer.CourseOrPosition" />:</td>
		<td><html:text bundle="HTMLALT_RESOURCES" altKey="text.courseOrPosition" property="courseOrPosition"/></td>
	</tr>
</table>

<p>
	<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.confirm" styleClass="inputbutton" property="confirm"><bean:message key="button.save"/>
	</html:submit> 
	<html:reset bundle="HTMLALT_RESOURCES" altKey="reset.reset" styleClass="inputbutton"><bean:message key="label.clear"/>
	</html:reset>  
	</logic:present>
	</html:form>
</p>

<logic:notPresent name="categories">
	<p>
		<em><bean:message key="message.teachingCareer.notpresent" /></em>
	</p>
</logic:notPresent>