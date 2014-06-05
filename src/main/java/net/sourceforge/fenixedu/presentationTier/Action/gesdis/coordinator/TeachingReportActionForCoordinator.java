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
package net.sourceforge.fenixedu.presentationTier.Action.gesdis.coordinator;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NotAuthorizedException;
import net.sourceforge.fenixedu.presentationTier.Action.coordinator.DegreeCoordinatorIndex;
import net.sourceforge.fenixedu.presentationTier.Action.gesdis.TeachingReportAction;
import net.sourceforge.fenixedu.presentationTier.config.FenixNotAuthorizedExceptionHandler;
import pt.ist.fenixWebFramework.struts.annotations.ExceptionHandling;
import pt.ist.fenixWebFramework.struts.annotations.Exceptions;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(module = "coordinator", path = "/viewTeachingReport", input = "/viewTeachingReport.do?method=read",
        formBean = "teachingReportForm", functionality = DegreeCoordinatorIndex.class)
@Forwards(@Forward(name = "successfull-read", path = "/gesdis/viewTeachingReport.jsp"))
@Exceptions(@ExceptionHandling(type = NotAuthorizedException.class, key = "error.exception.notAuthorized",
        handler = FenixNotAuthorizedExceptionHandler.class, scope = "request"))
public class TeachingReportActionForCoordinator extends TeachingReportAction {
}