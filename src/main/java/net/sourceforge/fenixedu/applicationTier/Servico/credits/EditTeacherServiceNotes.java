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
package net.sourceforge.fenixedu.applicationTier.Servico.credits;

import net.sourceforge.fenixedu.applicationTier.Filtro.DepartmentAdministrativeOfficeAuthorizationFilter;
import net.sourceforge.fenixedu.applicationTier.Filtro.DepartmentMemberAuthorizationFilter;
import net.sourceforge.fenixedu.applicationTier.Filtro.TeacherAuthorizationFilter;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NotAuthorizedException;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.domain.person.RoleType;
import net.sourceforge.fenixedu.domain.teacher.TeacherService;
import net.sourceforge.fenixedu.domain.teacher.TeacherServiceNotes;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class EditTeacherServiceNotes {

    protected Boolean run(Teacher teacher, String executionPeriodId, String managementFunctionNote, String serviceExemptionNote,
            String otherNote, String masterDegreeTeachingNote, String functionsAccumulation, String thesisNote, RoleType roleType)
            throws FenixServiceException {

        ExecutionSemester executionSemester = FenixFramework.getDomainObject(executionPeriodId);
        TeacherService teacherService = teacher.getTeacherServiceByExecutionPeriod(executionSemester);

        if (teacherService == null) {
            teacherService = new TeacherService(teacher, executionSemester);
        }

        TeacherServiceNotes teacherServiceNotes = teacherService.getTeacherServiceNotes();
        if (teacherServiceNotes == null) {
            teacherServiceNotes = new TeacherServiceNotes(teacherService);
        }

        teacherServiceNotes.editNotes(managementFunctionNote, serviceExemptionNote, otherNote, masterDegreeTeachingNote,
                functionsAccumulation, thesisNote, roleType);

        if (StringUtils.isEmpty(teacherServiceNotes.getManagementFunctionNotes())
                && StringUtils.isEmpty(teacherServiceNotes.getServiceExemptionNotes())
                && StringUtils.isEmpty(teacherServiceNotes.getOthersNotes())
                && StringUtils.isEmpty(teacherServiceNotes.getFunctionsAccumulation())
                && StringUtils.isEmpty(teacherServiceNotes.getMasterDegreeTeachingNotes())
                && StringUtils.isEmpty(teacherServiceNotes.getThesisNote())) {
            teacherServiceNotes.delete();
        }

        return Boolean.TRUE;
    }

    // Service Invokers migrated from Berserk

    private static final EditTeacherServiceNotes serviceInstance = new EditTeacherServiceNotes();

    @Atomic
    public static Boolean runEditTeacherServiceNotes(Teacher teacher, String executionPeriodId, String managementFunctionNote,
            String serviceExemptionNote, String otherNote, String masterDegreeTeachingNote, String functionsAccumulation,
            String thesisNote, RoleType roleType) throws FenixServiceException, NotAuthorizedException {
        try {
            DepartmentAdministrativeOfficeAuthorizationFilter.instance.execute();
            return serviceInstance.run(teacher, executionPeriodId, managementFunctionNote, serviceExemptionNote, otherNote,
                    masterDegreeTeachingNote, functionsAccumulation, thesisNote, roleType);
        } catch (NotAuthorizedException ex1) {
            try {
                TeacherAuthorizationFilter.instance.execute();
                return serviceInstance.run(teacher, executionPeriodId, managementFunctionNote, serviceExemptionNote, otherNote,
                        masterDegreeTeachingNote, functionsAccumulation, thesisNote, roleType);
            } catch (NotAuthorizedException ex2) {
                try {
                    DepartmentMemberAuthorizationFilter.instance.execute();
                    return serviceInstance.run(teacher, executionPeriodId, managementFunctionNote, serviceExemptionNote,
                            otherNote, masterDegreeTeachingNote, functionsAccumulation, thesisNote, roleType);
                } catch (NotAuthorizedException ex3) {
                    throw ex3;
                }
            }
        }
    }

}