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
package net.sourceforge.fenixedu.applicationTier.Servico.department;

import net.sourceforge.fenixedu.applicationTier.Filtro.DepartmentMemberAuthorizationFilter;
import net.sourceforge.fenixedu.applicationTier.Filtro.TeacherAuthorizationFilter;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NotAuthorizedException;
import net.sourceforge.fenixedu.dataTransferObject.department.TeacherPersonalExpectationBean;
import net.sourceforge.fenixedu.domain.teacher.TeacherPersonalExpectation;
import pt.ist.fenixframework.Atomic;

public class InsertTeacherPersonalExpectation {

    protected TeacherPersonalExpectation run(TeacherPersonalExpectationBean bean) {
        if (bean != null) {
            return new TeacherPersonalExpectation(bean);
        }
        return null;
    }

    // Service Invokers migrated from Berserk

    private static final InsertTeacherPersonalExpectation serviceInstance = new InsertTeacherPersonalExpectation();

    @Atomic
    public static TeacherPersonalExpectation runInsertTeacherPersonalExpectation(TeacherPersonalExpectationBean bean)
            throws NotAuthorizedException {
        try {
            DepartmentMemberAuthorizationFilter.instance.execute();
            return serviceInstance.run(bean);
        } catch (NotAuthorizedException ex1) {
            try {
                TeacherAuthorizationFilter.instance.execute();
                return serviceInstance.run(bean);
            } catch (NotAuthorizedException ex2) {
                throw ex2;
            }
        }
    }

}