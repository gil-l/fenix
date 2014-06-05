/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.fenixedu.presentationTier.Action.webSiteManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(module = "webSiteManager", path = "/manageUnitAnnouncements", functionality = ListSitesAction.class)
@Forwards(@Forward(name = "viewAnnouncementsRedirect",
        path = "/webSiteManager/manageUnitAnnouncements.do?method=viewAnnouncements&tabularVersion=true"))
public class UnitAnnouncementsDA extends UnitSiteAnnouncementManagement {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.setAttribute("siteActionName", "/manageUnitSite.do");
        request.setAttribute("siteContextParam", "oid");
        request.setAttribute("siteContextParamValue", getSite(request).getExternalId());

        request.setAttribute("unitId", getSite(request).getUnit().getExternalId());
        request.setAttribute("announcementsActionName", "/manageUnitAnnouncements.do");
        return super.execute(mapping, actionForm, request, response);
    }

    @Override
    protected String getContextInformation(ActionMapping mapping, HttpServletRequest request) {
        return mapping.getPath() + ".do";
    }

    @Override
    protected String getExtraRequestParameters(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();

        addExtraParameter(request, builder, "siteId");
        addExtraParameter(request, builder, "tabularVersion");
        addExtraParameter(request, builder, "oid");

        return builder.toString();
    }

}
