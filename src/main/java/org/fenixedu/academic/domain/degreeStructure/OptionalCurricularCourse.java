/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.domain.degreeStructure;

import java.util.List;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Curriculum;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.AnyCurricularCourse;
import org.fenixedu.academic.domain.curricularRules.CreditsLimit;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.curriculum.CurricularCourseType;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.util.MultiLanguageString;

public class OptionalCurricularCourse extends OptionalCurricularCourse_Base {

    protected OptionalCurricularCourse() {
        super();
    }

    /**
     * - This constructor is used to create a 'special' curricular course that
     * will represent any curricular course accordding to a rule
     */
    public OptionalCurricularCourse(CourseGroup parentCourseGroup, String name, String nameEn, CurricularStage curricularStage,
            CurricularPeriod curricularPeriod, ExecutionSemester beginExecutionPeriod, ExecutionSemester endExecutionPeriod) {
        super();
        setName(name);
        setNameEn(nameEn);
        setCurricularStage(curricularStage);
        setType(CurricularCourseType.OPTIONAL_COURSE);
        new Context(parentCourseGroup, this, curricularPeriod, beginExecutionPeriod, endExecutionPeriod);
    }

    @Override
    public boolean isOptionalCurricularCourse() {
        return true;
    }

    @Override
    public void edit(String name, String nameEn, CurricularStage curricularStage) {
        setName(name);
        setNameEn(nameEn);
        setCurricularStage(curricularStage);
    }

    @Override
    public Double getMaxEctsCredits(final ExecutionSemester executionSemester) {
        final CreditsLimit creditsLimitRule = getCreditsLimitRule(executionSemester);
        if (creditsLimitRule != null) {
            return creditsLimitRule.getMaximumCredits();
        }
        final AnyCurricularCourse anyCurricularCourseRule = getAnyCurricularCourseRule(executionSemester);
        if (anyCurricularCourseRule != null) {
            return anyCurricularCourseRule.hasCredits() ? anyCurricularCourseRule.getCredits() : 0;
        }
        return Double.valueOf(0d);
    }

    @Override
    public Double getMinEctsCredits(ExecutionSemester executionSemester) {
        final CreditsLimit creditsLimitRule = getCreditsLimitRule(executionSemester);
        if (creditsLimitRule != null) {
            return creditsLimitRule.getMinimumCredits();
        }
        final AnyCurricularCourse anyCurricularCourseRule = getAnyCurricularCourseRule(executionSemester);
        if (anyCurricularCourseRule != null) {
            return anyCurricularCourseRule.hasCredits() ? anyCurricularCourseRule.getCredits() : 0;
        }
        return Double.valueOf(0d);
    }

    private AnyCurricularCourse getAnyCurricularCourseRule(final ExecutionSemester executionSemester) {
        final List<AnyCurricularCourse> result =
                (List<AnyCurricularCourse>) getCurricularRules(CurricularRuleType.ANY_CURRICULAR_COURSE, executionSemester);
        // must have only one
        return result.isEmpty() ? null : (AnyCurricularCourse) result.iterator().next();
    }

    @Override
    public MultiLanguageString getObjectivesI18N(ExecutionSemester period) {
        return new MultiLanguageString();
    }

    @Override
    public MultiLanguageString getProgramI18N(ExecutionSemester period) {
        return new MultiLanguageString();
    }

    @Override
    public MultiLanguageString getEvaluationMethodI18N(ExecutionSemester period) {
        return new MultiLanguageString();
    }



    @Override
    public String getName(ExecutionSemester period) {
        return getCompetenceCourse().getName(period);
    }

    @Override
    public String getName() {
        return getName(null);
    }

    @Override
    public String getNameEn(ExecutionSemester period) {
        return getCompetenceCourse().getNameEn(period);
    }

    @Override
    public String getNameEn() {
        return getNameEn(null);
    }

    public String getAcronym(ExecutionSemester period) {
        return getCompetenceCourse().getAcronym(period);
    }

    @Override
    public String getAcronym() {
        return getAcronym(null);
    }

    @Override
    public Boolean getBasic() {
        return getBasic(null);
    }

    public Boolean getBasic(ExecutionSemester period) {
        return getCompetenceCourse().isBasic(period);
    }

    public DepartmentUnit getDepartmentUnit(ExecutionSemester semester) {
        return getCompetenceCourse().getDepartmentUnit(semester);
    }

    public DepartmentUnit getDepartmentUnit() {
        return getCompetenceCourse().getDepartmentUnit();
    }

    public String getObjectives(ExecutionSemester period) {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getObjectives(period);
        }
        Curriculum curriculum = findLatestCurriculumModifiedBefore(period.getExecutionYear().getEndDate());
        if (curriculum != null) {
            return curriculum.getFullObjectives();
        }
        return null;
    }

    public String getObjectives() {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getObjectives();
        }
        Curriculum curriculum = findLatestCurriculum();
        if (curriculum != null) {
            return curriculum.getFullObjectives();
        }
        return null;
    }

    public String getObjectivesEn(ExecutionSemester period) {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getObjectivesEn(period);
        }
        Curriculum curriculum = findLatestCurriculumModifiedBefore(period.getExecutionYear().getEndDate());
        if (curriculum != null) {
            return curriculum.getFullObjectivesEn();
        }
        return null;
    }

    public String getObjectivesEn() {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getObjectivesEn();
        }
        Curriculum curriculum = findLatestCurriculum();
        if (curriculum != null) {
            return curriculum.getFullObjectivesEn();
        }
        return null;
    }

    public MultiLanguageString getObjectivesI18N(ExecutionSemester period) {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getObjectivesI18N(period);
        }
        Curriculum curriculum = findLatestCurriculumModifiedBefore(period.getExecutionYear().getEndDate());
        if (curriculum != null) {
            return curriculum.getFullObjectivesI18N();
        }
        return new MultiLanguageString();
    }

    public String getProgram(ExecutionSemester period) {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getProgram(period);
        }
        Curriculum curriculum = findLatestCurriculumModifiedBefore(period.getExecutionYear().getEndDate());
        if (curriculum != null) {
            return curriculum.getProgram();
        }
        return null;
    }

    public String getProgram() {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getProgram();
        }
        Curriculum curriculum = findLatestCurriculum();
        if (curriculum != null) {
            return curriculum.getProgram();
        }
        return null;
    }

    public String getProgramEn(ExecutionSemester period) {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getProgramEn(period);
        }
        Curriculum curriculum = findLatestCurriculum();
        if (curriculum != null) {
            return curriculum.getProgramEn();
        }
        return null;
    }

    public String getProgramEn() {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getProgramEn();
        }
        Curriculum curriculum = findLatestCurriculum();
        if (curriculum != null) {
            return curriculum.getProgramEn();
        }
        return null;
    }

    public MultiLanguageString getProgramI18N(ExecutionSemester period) {
        if (isBolonhaDegree()) {
            return getCompetenceCourse().getProgramI18N(period);
        }
        Curriculum curriculum = findLatestCurriculumModifiedBefore(period.getExecutionYear().getEndDate());
        if (curriculum != null) {
            return curriculum.getProgramI18N();
        }
        return new MultiLanguageString();
    }

    public MultiLanguageString getPrerequisitesI18N() {
        return new MultiLanguageString(MultiLanguageString.pt, getPrerequisites())
                .with(MultiLanguageString.en, getPrerequisitesEn());
    }

    public String getEvaluationMethod(ExecutionSemester period) {
        if (getCompetenceCourse() != null) {
            return getCompetenceCourse().getEvaluationMethod(period);
        }
        if (hasAnyExecutionCourseIn(period)) {
            return getExecutionCoursesByExecutionPeriod(period).iterator().next().getEvaluationMethodText();
        } else {
            return null;
        }
    }

    public String getEvaluationMethod() {
        if (getCompetenceCourse() != null) {
            return getCompetenceCourse().getEvaluationMethod();
        }
        return null;
    }

    public String getEvaluationMethodEn(ExecutionSemester period) {
        if (getCompetenceCourse() != null) {
            return getCompetenceCourse().getEvaluationMethodEn(period);
        }
        if (hasAnyExecutionCourseIn(period)) {
            return getExecutionCoursesByExecutionPeriod(period).iterator().next().getEvaluationMethodTextEn();
        } else {
            return null;
        }
    }

    public String getEvaluationMethodEn() {
        if (getCompetenceCourse() != null) {
            return getCompetenceCourse().getEvaluationMethodEn();
        }
        return null;
    }

    public MultiLanguageString getEvaluationMethodI18N(ExecutionSemester period) {
        if (isBolonhaDegree()) {
            return new MultiLanguageString(MultiLanguageString.pt, getCompetenceCourse().getEvaluationMethod(period))
                    .with(MultiLanguageString.en, getCompetenceCourse().getEvaluationMethodEn(period));
        }
        List<ExecutionCourse> courses = getExecutionCoursesByExecutionPeriod(period);
        if (courses.isEmpty()) {
            return new MultiLanguageString();
        }
        return new MultiLanguageString(MultiLanguageString.pt, courses.iterator().next().getEvaluationMethodText())
                .with(MultiLanguageString.en, courses.iterator().next().getEvaluationMethodTextEn());
    }

    public RegimeType getRegime(final ExecutionSemester period) {
        final CompetenceCourse competenceCourse = getCompetenceCourse();
        return competenceCourse == null ? null : competenceCourse.getRegime(period);
    }

    public RegimeType getRegime(final ExecutionYear executionYear) {
        final CompetenceCourse competenceCourse = getCompetenceCourse();
        return competenceCourse == null ? null : competenceCourse.getRegime(executionYear);
    }

    public RegimeType getRegime() {
        if (getCompetenceCourse() != null) {
            return getCompetenceCourse().getRegime();
        }
        return isOptionalCurricularCourse() ? RegimeType.SEMESTRIAL : null;
    }

    public boolean hasRegime() {
        return getRegime() != null;
    }

    public boolean hasRegime(final ExecutionYear executionYear) {
        return getRegime(executionYear) != null;
    }


    /**
     * Maintened for legacy code compatibility purposes only. Makes no sense to
     * check an Enrolment concept in a CurricularCourse.
     *
     * @return true if CurricularCourseType checks accordingly
     */
    @Deprecated
    final public boolean isPropaedeutic() {
        // if (isBolonhaDegree()) {
        // throw new
        // DomainException("CurricularCourse.must.check.propaedeutic.status.in.enrolment.in.bolonha.degrees");
        // }
        return getType().equals(CurricularCourseType.P_TYPE_COURSE);
    }

    public boolean isOptionalCurricularCourse() {
        return false;
    }

    @Override
    final public boolean isOptional() {
        return getType() == CurricularCourseType.OPTIONAL_COURSE;
    }

    final public boolean isTFC() {
        return getType() == CurricularCourseType.TFC_COURSE;
    }

    @Override
    public boolean isDissertation() {
        CompetenceCourse competenceCourse = getCompetenceCourse();
        return competenceCourse == null ? false : competenceCourse.isDissertation();
    }

    public boolean isAnual() {
        if (!isBolonhaDegree()) {
            return getRegimeType() == RegimeType.ANUAL;
        }
        return getCompetenceCourse() != null && getCompetenceCourse().isAnual();
    }

    public boolean isAnual(final ExecutionYear executionYear) {
        if (!isBolonhaDegree()) {
            return getRegimeType() == RegimeType.ANUAL;
        }
        return getCompetenceCourse() != null && getCompetenceCourse().isAnual(executionYear);
    }

    public boolean isSemestrial(final ExecutionYear executionYear) {
        if (!isBolonhaDegree()) {
            return getRegimeType() == RegimeType.SEMESTRIAL;
        }
        return getCompetenceCourse() != null && getCompetenceCourse().isSemestrial(executionYear);
    }

    public boolean isEquivalent(CurricularCourse oldCurricularCourse) {
        return equals(oldCurricularCourse) || (getCompetenceCourse() != null && getCompetenceCourse()
                .getAssociatedCurricularCoursesSet().contains(oldCurricularCourse));
    }

    @Override
    public String getCode() {
        if (getCompetenceCourse() != null) {
            return getCompetenceCourse().getCode();
        }
        return super.getCode();
    }

    public CompetenceCourseLevel getCompetenceCourseLevel() {
        return getCompetenceCourse() != null ? getCompetenceCourse().getCompetenceCourseLevel() : null;
    }
}
