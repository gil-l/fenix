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
package net.sourceforge.fenixedu.applicationTier.Servico.scientificCouncil.credits;

import static net.sourceforge.fenixedu.injectionCode.AccessControl.check;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.Professorship;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.domain.teacher.TeacherMasterDegreeService;
import net.sourceforge.fenixedu.domain.teacher.TeacherService;
import net.sourceforge.fenixedu.predicates.RolePredicates;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class EditTeacherMasterDegreeCredits {

    @Atomic
    public static void run(Map<String, String> hoursMap, Map<String, String> creditsMap) throws NumberFormatException {
        check(RolePredicates.SCIENTIFIC_COUNCIL_PREDICATE);
        Set<String> professorshipIDs = new HashSet<String>(hoursMap.keySet());
        professorshipIDs.addAll(creditsMap.keySet());

        for (String stringID : professorshipIDs) {
            String creditsString = creditsMap.get(stringID);
            String hoursString = hoursMap.get(stringID);
            if (hoursString.equals("") && creditsString.equals("")) {
                continue;
            }
            Professorship professorship = FenixFramework.getDomainObject(stringID);
            Teacher teacher = professorship.getTeacher();
            ExecutionSemester executionSemester = professorship.getExecutionCourse().getExecutionPeriod();

            TeacherService teacherService = teacher.getTeacherServiceByExecutionPeriod(executionSemester);
            if (teacherService == null) {
                teacherService = new TeacherService(teacher, executionSemester);
            }

            TeacherMasterDegreeService teacherMasterDegreeService =
                    teacherService.getMasterDegreeServiceByProfessorship(professorship);
            if (teacherMasterDegreeService == null) {
                teacherMasterDegreeService = new TeacherMasterDegreeService(teacherService, professorship);
            }

            Double credits = null;
            Double hours = null;
            if (!creditsString.equals("")) {
                credits = Double.parseDouble(creditsString);
            }
            if (!hoursString.equals("")) {
                hours = Double.parseDouble(hoursString);
            }
            teacherMasterDegreeService.updateValues(hours, credits);
        }
    }

}