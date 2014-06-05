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
<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<logic:equal name="isTeacher" value="true">

<%-- ### Title #### --%>
<h2><bean:message key="label.phd.manageThesisDocuments" bundle="PHD_RESOURCES" /></h2>
<%-- ### End of Title ### --%>


<%--  ###  Return Links / Steps Information(for multistep forms)  ### --%>
<bean:define id="individualProgramProcessId" name="process" property="individualProgramProcess.externalId" />

<html:link action="<%= "/phdIndividualProgramProcess.do?method=viewProcess&processId=" + individualProgramProcessId.toString() %>">
	<bean:message bundle="PHD_RESOURCES" key="label.back"/>
</html:link>

<br/><br/>
<%--  ### Return Links / Steps Information (for multistep forms)  ### --%>


<%--  ### Error Messages  ### --%>
<jsp:include page="/phd/errorsAndMessages.jsp" />
<%--  ### End of Error Messages  ### --%>


<%--  ### Context Information (e.g. Person Information, Registration Information)  ### --%>
<table>
  <tr style="vertical-align: top;">
    <td style="width: 55%">
    	<strong><bean:message  key="label.phd.process" bundle="PHD_RESOURCES"/></strong>
		<fr:view schema="PhdIndividualProgramProcess.view.simple" name="process" property="individualProgramProcess">
			<fr:layout name="tabular">
				<fr:property name="classes" value="tstyle2 thlight mtop15" />
			</fr:layout>
		</fr:view>
	</td>
  </tr>
</table>
<%--  ### End Of Context Information  ### --%>

<bean:define id="processId" name="process" property="externalId" />
<br/>

<%--  ### Documents  ### --%>
<logic:empty name="process" property="latestDocumentVersions">
	<strong><bean:message  key="label.phd.documents" bundle="PHD_RESOURCES"/></strong>
	<br/>
	<bean:message  key="label.phd.noDocuments" bundle="PHD_RESOURCES"/>
</logic:empty>

<logic:notEmpty name="sharedDocuments" >	
	
	<strong><bean:message  key="label.phd.documents" bundle="PHD_RESOURCES"/></strong>
	<p>
	<span class="compress">
	<html:link action="/phdThesisProcess.do?method=downloadThesisDocuments" paramId="processId" paramName="processId">
		<bean:message key="label.phd.documents.download.all" bundle="PHD_RESOURCES" />
	</html:link>
	</span>
	</p>
	

	<fr:view schema="PhdProgramProcessDocument.view" name="sharedDocuments">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle2 thlight mtop15" />
			
			<fr:property name="linkFormat(view)" value="${downloadUrl}"/>
			<fr:property name="key(view)" value="label.view"/>
			<fr:property name="bundle(view)" value="PHD_RESOURCES"/>
			<fr:property name="order(view)" value="0" />
			<fr:property name="hasContext(view)" value="true" />
			<fr:property name="contextRelative(view)" value="true" />
			<fr:property name="module(view)" value="" />

			<fr:property name="sortBy" value="documentType=asc" />
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<logic:empty name="sharedDocuments" >
	<em> <bean:message key="message.phd.thesis.shared.documents.empty" bundle="PHD_RESOURCES" /> </em>
</logic:empty>

<%--  ### End Of Documents  ### --%>
<br/><br/>

<%--  ### End of Operation Area  ### --%>

</logic:equal>
