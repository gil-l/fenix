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
package net.sourceforge.fenixedu.applicationTier.Servico.teacher;

import net.sourceforge.fenixedu.applicationTier.Filtro.ExecutionCourseLecturingTeacherAuthorizationFilter;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NotAuthorizedException;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.LessonPlanning;
import net.sourceforge.fenixedu.domain.ShiftType;
import pt.ist.fenixframework.Atomic;

public class DeleteLessonPlanning {

    protected void run(String executionCourseID, LessonPlanning lessonPlanning, ExecutionCourse executionCourse,
            ShiftType shiftType) {
        if (lessonPlanning != null) {
            lessonPlanning.delete();
        } else if (executionCourse != null && shiftType != null) {
            executionCourse.deleteLessonPlanningsByLessonType(shiftType);
        }
    }

    // Service Invokers migrated from Berserk

    private static final DeleteLessonPlanning serviceInstance = new DeleteLessonPlanning();

    @Atomic
    public static void runDeleteLessonPlanning(String executionCourseID, LessonPlanning lessonPlanning,
            ExecutionCourse executionCourse, ShiftType shiftType) throws NotAuthorizedException {
        ExecutionCourseLecturingTeacherAuthorizationFilter.instance.execute(executionCourseID);
        serviceInstance.run(executionCourseID, lessonPlanning, executionCourse, shiftType);
    }

}