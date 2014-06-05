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
package net.sourceforge.fenixedu.presentationTier.Action.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.Person.AnyPersonSearchBean;
import net.sourceforge.fenixedu.domain.Person.ExternalPersonBeanFactoryCreator;
import net.sourceforge.fenixedu.domain.person.IDDocumentType;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class ExternalPersonDA extends FenixDispatchAction {

    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        AnyPersonSearchBean anyPersonSearchBean = getRenderedObject();
        if (anyPersonSearchBean == null) {
            anyPersonSearchBean = new AnyPersonSearchBean();
        }
        final String name = request.getParameter("name");
        if (isSpecified(name)) {
            anyPersonSearchBean.setName(name);
        }
        request.setAttribute("anyPersonSearchBean", anyPersonSearchBean);

        return mapping.findForward("showSearch");
    }

    public ActionForward prepareCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        ExternalPersonBeanFactoryCreator externalPersonBean = getRenderedObject();
        if (externalPersonBean == null) {
            externalPersonBean = new ExternalPersonBeanFactoryCreator();
        }
        setRequestParameters(request, externalPersonBean);
        request.setAttribute("externalPersonBean", externalPersonBean);

        return mapping.findForward("showCreateForm");
    }

    private void setRequestParameters(final HttpServletRequest request, final ExternalPersonBeanFactoryCreator externalPersonBean) {
        final String name = request.getParameter("name");
        if (isSpecified(name)) {
            externalPersonBean.setName(name);
        }
        final String idDocumentType = request.getParameter("idDocumentType");
        if (isSpecified(idDocumentType)) {
            externalPersonBean.setIdDocumentType(IDDocumentType.valueOf(idDocumentType));
        }
        final String documentIdNumber = request.getParameter("documentIdNumber");
        if (isSpecified(documentIdNumber)) {
            externalPersonBean.setDocumentIdNumber(documentIdNumber);
        }
    }

    private boolean isSpecified(final String string) {
        return string != null && string.length() > 0;
    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        final Person person = (Person) executeFactoryMethod();
        request.setAttribute("person", person);
        RenderUtils.invalidateViewState();
        return mapping.findForward("showCreatedPerson");
    }
}