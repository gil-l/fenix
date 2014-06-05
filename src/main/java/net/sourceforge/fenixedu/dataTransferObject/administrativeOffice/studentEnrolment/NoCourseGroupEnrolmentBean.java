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
package net.sourceforge.fenixedu.dataTransferObject.administrativeOffice.studentEnrolment;

import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.curricularRules.executors.ruleExecutors.CurricularRuleLevel;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.degreeStructure.Context;
import net.sourceforge.fenixedu.domain.studentCurriculum.CurriculumGroup;
import net.sourceforge.fenixedu.domain.studentCurriculum.NoCourseGroupCurriculumGroup;
import net.sourceforge.fenixedu.domain.studentCurriculum.NoCourseGroupCurriculumGroupType;

public interface NoCourseGroupEnrolmentBean {

    public NoCourseGroupCurriculumGroupType getGroupType();

    public StudentCurricularPlan getStudentCurricularPlan();

    public void setStudentCurricularPlan(StudentCurricularPlan studentCurricularPlan);

    public ExecutionSemester getExecutionPeriod();

    public void setExecutionPeriod(ExecutionSemester executionSemester);

    public CurriculumGroup getCurriculumGroup();

    public void setCurriculumGroup(CurriculumGroup curriculumGroup);

    public Context getContex();

    public void setContext(Context context);

    public CurricularCourse getSelectedCurricularCourse();

    public DegreeType getDegreeType();

    public void setDegreeType(DegreeType degreeType);

    public Degree getDegree();

    public void setDegree(Degree degree);

    public DegreeCurricularPlan getDegreeCurricularPlan();

    public void setDegreeCurricularPlan(DegreeCurricularPlan degreeCurricularPlan);

    public NoCourseGroupCurriculumGroup getNoCourseGroupCurriculumGroup();

    public CurricularRuleLevel getCurricularRuleLevel();

    public void setCurricularRuleLevel(CurricularRuleLevel curricularRuleLevel);
}
