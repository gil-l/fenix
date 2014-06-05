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
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<h2><bean:message bundle="MANAGER_RESOURCES" key="label.manager.teachersManagement"/></h2>

<table>
	<tr>
		<td class="well">
			<bean:message bundle="MANAGER_RESOURCES" key="label.manager.teachersManagement.welcome"/>		
		</td>
	</tr>
</table>

<br />

<html:link styleClass="btn btn-primary" page="/dissociateProfShipsAndRespFor.do?method=prepareDissociateEC">
	<bean:message bundle="MANAGER_RESOURCES" key="link.manager.teachersManagement.removeECAssociation" />
</html:link>