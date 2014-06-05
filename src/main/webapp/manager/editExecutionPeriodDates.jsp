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
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<html:xhtml />

<logic:present role="(role(MANAGER) | role(OPERATOR))">

	<h2><bean:message bundle="MANAGER_RESOURCES" key="title.edit.execution.period" /></h2>
	
	<logic:notEmpty name="executionPeriod">
		<html:messages id="message" message="true" bundle="MANAGER_RESOURCES">
			<p><span class="error"><!-- Error messages go here --><bean:write name="message" /></span></p>
		</html:messages>
		<fr:edit name="executionPeriod" schema="EditExecutionPeriodSchema" action="/manageExecutionPeriods.do?method=prepare">
			<fr:layout name="tabular">
				<fr:property name="classes" value="tstyle5 thlight"/>
			</fr:layout>				
			<fr:destination name="cancel" path="/manageExecutionPeriods.do?method=prepare"/>
		</fr:edit>

	</logic:notEmpty>

</logic:present>