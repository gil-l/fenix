<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<span class="error"><html:errors/></span>	
<br/>
<table>
<tr>
	<td  colspan="3"></td>
	<td  colspan="4"><div align="center"><h4><bean:message key="label.exam.enrollment.period"/></h4></div></td>
	<td  ></td>
</tr>	
<tr>
	<td class="listClasses-header" ><bean:message key="label.season"/></td>
	<td class="listClasses-header" ><bean:message key="label.day"/></td>
	<td class="listClasses-header" ><bean:message key="label.beginning"/></td>	
	<td class="listClasses-header" ><bean:message key="label.exam.enrollment.begin.day"/></td>
	<td class="listClasses-header" ><bean:message key="label.exam.enrollment.begin.hour"/></td>
	<td class="listClasses-header" ><bean:message key="label.exam.enrollment.end.day"/></td>
	<td class="listClasses-header" ><bean:message key="label.exam.enrollment.end.hour"/></td>
		
	
	<td class="listClasses-header" ><bean:message key="label.students.enrolled.exam"/></td>
</tr>
<logic:iterate id="exam" name="infoExamList">
	<html:form action="/examEnrollmentEditionManager" >
		
	<bean:define id="idInternal" name="exam" property="idInternal"/>
<tr>
	<td class="listClasses"><bean:write name="exam" property="season"/></td>
	<td class="listClasses"><bean:write name="exam" property="date"/></td>
	<td class="listClasses"><bean:write name="exam" property="beginningHour"/></td>
	<td class="listClasses"><html:text size="10" name="exam" property="enrollmentBeginDayFormatted"/></td>
	<td class="listClasses"><html:text size="5" name="exam" property="enrollmentBeginTimeFormatted"/></td>
	<td class="listClasses"><html:text size="10" name="exam" property="enrollmentEndDayFormatted"/></td>
	<td class="listClasses"><html:text size="5" name="exam" property="enrollmentEndTimeFormatted"/></td>
	<td class="listClasses" >
		<html:link 
			page="<%= "/showStudentsEnrolledInExam.do?objectCode="+ pageContext.findAttribute("objectCode")+"&amp;examCode=" +pageContext.findAttribute("idInternal") %>" >
			<bean:message key="label.students.enrolled.exam"/></html:link></td>			
	<bean:define id="examCode" name="exam" property="idInternal"/>
	
	<html:hidden property="method" value="editExamEnrollment" />
	<html:hidden property="examCode" value="<%= pageContext.findAttribute("examCode").toString() %>" />
	<html:hidden property="objectCode" value="<%= pageContext.findAttribute("objectCode").toString() %>" /> 

	<td><html:submit styleClass="inputbutton">
<bean:message key="button.save"/>
</html:submit></td>	
</tr>
</html:form>	
</logic:iterate>
</table>

