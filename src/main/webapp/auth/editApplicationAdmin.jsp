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
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<%@ page
	import="net.sourceforge.fenixedu.presentationTier.Action.resourceAllocationManager.utils.PresentationConstants"%>
<%@page import="net.sourceforge.fenixedu.domain.person.RoleType"%>
<html:xhtml />

<h2>
	<bean:message key="oauthapps.title.edit.application" bundle="APPLICATION_RESOURCES" />
</h2>

<p class="infoop2">
<b><bean:message key="oauthapps.label.edit.application.warning" bundle="APPLICATION_RESOURCES" /></b>
</p>

<fr:edit id="edit" name="application" type="net.sourceforge.fenixedu.domain.ExternalApplication" schema="oauthapps.create.admin.app">
	<fr:destination name="success" path="/externalApps.do?method=viewAllApplications" />
	<fr:destination name="cancel" path="/externalApps.do?method=viewAllApplications" />
</fr:edit>
