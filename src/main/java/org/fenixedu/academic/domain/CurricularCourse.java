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
package org.fenixedu.academic.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.branch.BranchType;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.RestrictionDoneDegreeModule;
import org.fenixedu.academic.domain.curriculum.CurricularCourseType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseLevel;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseLoad;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.CurricularCourseFunctor;
import org.fenixedu.academic.domain.degreeStructure.CurricularStage;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.degreeStructure.RegimeType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.log.CurriculumLineLog;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.time.calendarStructure.AcademicInterval;
import org.fenixedu.academic.domain.time.calendarStructure.AcademicPeriod;
import org.fenixedu.academic.dto.degreeAdministrativeOffice.gradeSubmission.MarkSheetEnrolmentEvaluationBean;
import org.fenixedu.academic.predicate.MarkSheetPredicates;
import org.fenixedu.academic.util.DateFormatUtil;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;

public class CurricularCourse extends CurricularCourse_Base {

    static final public Comparator<CurricularCourse> CURRICULAR_COURSE_COMPARATOR_BY_DEGREE_AND_NAME =
            (CurricularCourse o1, CurricularCourse o2) -> {
                final Degree degree1 = o1.getDegree();
                final Degree degree2 = o2.getDegree();
                final int degreeTypeComp = degree1.getDegreeType().compareTo(degree2.getDegreeType());
                if (degreeTypeComp != 0) {
                    return degreeTypeComp;
                }
                final int degreeNameComp = Collator.getInstance().compare(degree1.getNome(), degree2.getNome());
                if (degreeNameComp == 0) {
                    return degreeNameComp;
                }
                return CurricularCourse.COMPARATOR_BY_NAME.compare(o1, o2);
            };

    public static List<CurricularCourse> readCurricularCourses() {
        return Bennu.getInstance().getDegreeModulesSet().stream().filter(module -> module instanceof CurricularCourse)
                .map(curricular -> (CurricularCourse) curricular).collect(Collectors.toList());
    }

    protected CurricularCourse() {
        super();
        final Double d = 0d;
        setTheoreticalHours(d);
        setTheoPratHours(d);
        setLabHours(d);
        setPraticalHours(d);
        setCredits(d);
        setEctsCredits(d);
        setWeigth(d);
    }

    /**
     * @deprecated curricular courses should no longer be created without a competence
     */
    @Deprecated
    protected CurricularCourse(DegreeCurricularPlan degreeCurricularPlan, String name, String code, String acronym,
            Boolean enrolmentAllowed, CurricularStage curricularStage) {
        this();
        checkParameters(name, code, acronym);
        checkForCurricularCourseWithSameAttributes(degreeCurricularPlan, name, code, acronym);
        setName(name);
        setCode(code);
        setAcronym(acronym);
        setEnrollmentAllowed(enrolmentAllowed);
        setCurricularStage(curricularStage);
        setDegreeCurricularPlan(degreeCurricularPlan);
        if (curricularStage == CurricularStage.OLD) {
            setRegimeType(RegimeType.SEMESTRIAL);
        }
    }

    private void checkParameters(final String name, final String code, final String acronym) {
        if (StringUtils.isEmpty(name)) {
            throw new DomainException("error.curricularCourse.invalid.name");
        }
        if (StringUtils.isEmpty(code)) {
            throw new DomainException("error.curricularCourse.invalid.code");
        }
        if (StringUtils.isEmpty(acronym)) {
            throw new DomainException("error.curricularCourse.invalid.acronym");
        }
    }

    public CurricularCourse(Double weight, String prerequisites, String prerequisitesEn, CurricularStage curricularStage,
            CompetenceCourse competenceCourse, CourseGroup parentCourseGroup, CurricularPeriod curricularPeriod,
            ExecutionSemester beginExecutionPeriod, ExecutionSemester endExecutionPeriod) {
        this();
        setWeigth(weight);
        setPrerequisites(prerequisites);
        setPrerequisitesEn(prerequisitesEn);
        setCurricularStage(curricularStage);
        setCompetenceCourse(competenceCourse);
        setType(CurricularCourseType.NORMAL_COURSE);
        new Context(parentCourseGroup, this, curricularPeriod, beginExecutionPeriod, endExecutionPeriod);
    }

    public GradeScale getGradeScaleChain() {
        return super.getGradeScale() != null ? super.getGradeScale() : getDegreeCurricularPlan().getGradeScaleChain();
    }

    @Override
    public void print(StringBuilder dcp, String tabs, Context previousContext) {
        String tab = tabs + "\t";
        dcp.append(tab);
        dcp.append("[CC ").append(getExternalId()).append("][");
        dcp.append(previousContext.getCurricularPeriod().getOrderByType(AcademicPeriod.YEAR)).append("Y,");
        dcp.append(previousContext.getCurricularPeriod().getOrderByType(AcademicPeriod.SEMESTER)).append("S]\t");
        dcp.append("[B:").append(previousContext.getBeginExecutionPeriod().getBeginDateYearMonthDay());
        dcp.append(" E:").append(previousContext.getEndExecutionPeriod() != null ? previousContext.getEndExecutionPeriod()
                .getEndDateYearMonthDay() : "          ");
        dcp.append("]\t");
        dcp.append(getName()).append("\n");
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public DegreeCurricularPlan getParentDegreeCurricularPlan() {
        if (!(getCurricularStage() == CurricularStage.OLD)) {
            return !getParentContextsSet().isEmpty() ? getParentContextsSet().iterator().next().getParentCourseGroup()
                    .getParentDegreeCurricularPlan() : null;
        } else {
            return super.getDegreeCurricularPlan();
        }
    }

    @Override
    final public DegreeCurricularPlan getDegreeCurricularPlan() {
        return getParentDegreeCurricularPlan();
    }

    public void edit(Double weight, String prerequisites, String prerequisitesEn, CurricularStage curricularStage,
            CompetenceCourse competenceCourse) {
        setWeigth(weight);
        setPrerequisites(prerequisites);
        setPrerequisitesEn(prerequisitesEn);
        setCurricularStage(curricularStage);
        setCompetenceCourse(competenceCourse);
    }

    /**
     * - This method is used to edit a 'special' curricular course that will
     * represent any curricular course according to a rule
     *
     * @deprecated This method edits fields that should no longer be used
     */
    @Deprecated
    public void edit(String name, String nameEn, CurricularStage curricularStage) {
        setName(name);
        setNameEn(nameEn);
        setCurricularStage(curricularStage);
    }

    /**
     * - Edit Pre-Bolonha CurricularCourse
     *
     * @deprecated This method edits fields that should no longer be used
     */
    @Deprecated
    public void edit(String name, String nameEn, String code, String acronym, Double weigth, Double credits, Double ectsCredits,
            Integer enrolmentWeigth, Integer minimumValueForAcumulatedEnrollments, Integer maximumValueForAcumulatedEnrollments,
            final Double theoreticalHours, final Double labHours, final Double praticalHours, final Double theoPratHours,
            final GradeScale gradeScale) {
        checkForCurricularCourseWithSameAttributes(getDegreeCurricularPlan(), name, code, acronym);
        setName(name);
        setNameEn(nameEn);
        setCode(code);
        setAcronym(acronym);
        setWeigth(weigth);
        setCredits(credits);
        setEctsCredits(ectsCredits);

        setEnrollmentWeigth(enrolmentWeigth);
        setMinimumValueForAcumulatedEnrollments(minimumValueForAcumulatedEnrollments);
        setMaximumValueForAcumulatedEnrollments(maximumValueForAcumulatedEnrollments);

        setTheoreticalHours(theoreticalHours);
        setLabHours(labHours);
        setPraticalHours(praticalHours);
        setTheoPratHours(theoPratHours);

        super.setGradeScale(gradeScale);
    }

    private void checkForCurricularCourseWithSameAttributes(DegreeCurricularPlan degreeCurricularPlan, String name, String code,
            String acronym) {
        for (final CurricularCourse curricularCourse : degreeCurricularPlan.getCurricularCoursesSet()) {
            if (curricularCourse == this) {
                continue;
            }
            if (curricularCourse.getName().equals(name) && curricularCourse.getCode().equals(code)) {
                throw new DomainException("error.curricularCourseWithSameNameAndCode");
            }
            if (curricularCourse.getAcronym().equals(acronym)) {
                throw new DomainException("error.curricularCourseWithSameAcronym");
            }
        }
    }

    @Override
    public void delete() {
        super.delete();
        getCurriculumLineLogsSet().forEach(CurriculumLineLog::delete);
        setUniversity(null);
        setDegreeCurricularPlan(null);
        setCompetenceCourse(null);
        setRootDomainObject(null);
        super.deleteDomainObject();
    }

    public boolean curricularCourseIsMandatory() {
        return getMandatory();
    }

    public List<CurricularCourseScope> getInterminatedScopes() {
        return getScopesSet().stream().filter(scope -> scope.getEndDate() == null).collect(Collectors.toList());
    }

    public List<CurricularCourseScope> getActiveScopes() {
        return getScopesSet().stream().filter(CurricularCourseScope::isActive).collect(Collectors.toList());
    }

    public boolean hasAnyActiveDegreModuleScope(final int year, final int semester) {
        return getDegreeModuleScopes().stream().anyMatch(scope -> scope.isActive(year, semester));
    }

    public boolean hasAnyActiveDegreModuleScope() {
        return getDegreeModuleScopes().stream().anyMatch(DegreeModuleScope::isActive);
    }

    public boolean hasAnyActiveDegreModuleScope(final ExecutionSemester executionSemester) {
        return getDegreeModuleScopes().stream().anyMatch(scope -> scope.isActiveForExecutionPeriod(executionSemester));
    }

    public boolean hasAnyActiveContext(final ExecutionSemester executionSemester) {
        return getParentContextsSet().stream().anyMatch(context -> context.isValid(executionSemester));
    }

    public boolean hasAnyActiveDegreModuleScope(final ExecutionYear executionYear) {
        return executionYear.getExecutionPeriodsSet().stream().anyMatch(this::hasAnyActiveDegreModuleScope);
    }

    public List<CurricularCourseScope> getActiveScopesInExecutionPeriod(final ExecutionSemester executionSemester) {
        return getScopesSet().stream().filter(scope -> scope.isActiveForExecutionPeriod(executionSemester))
                .collect(Collectors.toList());
    }

    public boolean hasActiveScopesInExecutionPeriod(final ExecutionSemester executionSemester) {
        return getDegreeModuleScopes().stream().anyMatch(scope -> scope.isActiveForExecutionPeriod(executionSemester));
    }

    public Set<CurricularCourseScope> getActiveScopesInExecutionYear(final ExecutionYear executionYear) {
        return getScopesSet().stream().filter(scope -> scope.isActiveForExecutionYear(executionYear)).collect(Collectors.toSet());
    }

    @Deprecated
    public List<DegreeModuleScope> getActiveDegreeModuleScopesInExecutionPeriod(final ExecutionSemester executionSemester) {
        return getDegreeModuleScopes().stream().filter(scope -> scope.isActiveForExecutionPeriod(executionSemester))
                .collect(Collectors.toList());
    }

    public List<DegreeModuleScope> getActiveDegreeModuleScopesInAcademicInterval(AcademicInterval academicInterval) {
        return getDegreeModuleScopes().stream().filter(scope -> scope.isActiveForAcademicInterval(academicInterval))
                .collect(Collectors.toList());
    }

    public List<CurricularCourseScope> getActiveScopesIntersectedByExecutionPeriod(final ExecutionSemester executionSemester) {
        return getScopesSet().stream().filter(scope ->
                (scope.getBeginYearMonthDay().isBefore(executionSemester.getBeginDateYearMonthDay()) && (
                        !scope.hasEndYearMonthDay() || !scope.getEndYearMonthDay()
                                .isBefore(executionSemester.getBeginDateYearMonthDay()))) || !scope.getBeginYearMonthDay()
                        .isAfter(executionSemester.getEndDateYearMonthDay())).collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // BEGIN: Only for enrollment purposes
    // -------------------------------------------------------------

    public boolean hasRestrictionDone(final CurricularCourse precedence) {
        if (!isBolonhaDegree()) { //FIXME Not removing this one as it does not concern competence course information. right?
            throw new DomainException("CurricularCourse.method.only.appliable.to.bolonha.structure");
        }

        final ExecutionSemester currentExecutionPeriod = ExecutionSemester.readActualExecutionSemester();

        return getCurricularRulesSet().stream()
                .filter(rule -> rule.isValid(currentExecutionPeriod) && rule instanceof RestrictionDoneDegreeModule)
                .map(restriction -> (RestrictionDoneDegreeModule) restriction)
                .anyMatch(restriction -> restriction.getPrecedenceDegreeModule() == precedence);
    }

    public CurricularYear getCurricularYearByBranchAndSemester(final Branch branch, final Integer semester) {
        return getCurricularYearByBranchAndSemester(branch, semester, new Date());
    }

    public CurricularYear getCurricularYearByBranchAndSemester(final Branch branch, final Integer semester, final Date date) {
        List<CurricularCourseScope> scopesFound = getScopesSet().stream()
                .filter(scope -> scope.getCurricularSemester().getSemester().equals(semester) && scope.isActive(date) && (
                        scope.getBranch().representsCommonBranch() || (branch != null && branch.equals(scope.getBranch()))))
                .collect(Collectors.toList());
        if (scopesFound.isEmpty()) {
            getScopesSet().stream()
                    .filter(scope -> scope.getCurricularSemester().getSemester().equals(semester) && scope.isActive(date))
                    .forEach(scopesFound::add);
        }
        return getCurricularYearWithLowerYear(scopesFound, date);
    }

    public String getCurricularCourseUniqueKeyForEnrollment() {
        final DegreeType degreeType =
                (getDegreeCurricularPlan() != null && getDegreeCurricularPlan().getDegree() != null) ? getDegreeCurricularPlan()
                        .getDegree().getDegreeType() : null;
        return constructUniqueEnrollmentKey(getCode(), getName(), degreeType);
    }

    public boolean hasActiveScopeInGivenSemester(final Integer semester) {
        return getScopesSet().stream()
                .anyMatch(scope -> scope.getCurricularSemester().getSemester().equals(semester) && scope.isActive());
    }

    public boolean hasScopeInGivenSemester(final Integer semester) {
        return getScopesSet().stream().anyMatch(scope -> scope.getCurricularSemester().getSemester().equals(semester));
    }

    public boolean hasActiveScopeInGivenSemesterForGivenBranch(final CurricularSemester curricularSemester, final Branch branch) {
        return getScopesSet().stream().anyMatch(
                s -> s.getCurricularSemester().equals(curricularSemester) && s.isActive() && s.getBranch().equals(branch));
    }

    public boolean hasActiveScopeInGivenSemesterForGivenBranch(final Integer semester, final Branch branch) {
        return getScopesSet().stream().anyMatch(
                s -> s.getCurricularSemester().getSemester().equals(semester) && s.isActive() && s.getBranch().equals(branch));
    }

    public boolean hasActiveScopeInGivenSemesterForCommonAndGivenBranch(final Integer semester, final Branch branch) {
        return getScopesSet().stream().anyMatch(
                s -> ((s.getBranch().getBranchType().equals(BranchType.COMNBR) || s.getBranch().equals(branch)) && s
                        .getCurricularSemester().getSemester().equals(semester) && s.isActive()));
    }

    private CurricularYear getCurricularYearWithLowerYear(List<CurricularCourseScope> listOfScopes, Date date) {
        return listOfScopes.stream().filter(scope -> scope.isActive(date))
                .map(scope -> scope.getCurricularSemester().getCurricularYear())
                .min(Comparator.nullsLast(Comparator.comparing(CurricularYear::getYear))).orElse(null);
    }

    // -------------------------------------------------------------
    // END: Only for enrollment purposes
    // -------------------------------------------------------------

    private String constructUniqueEnrollmentKey(String code, String name, DegreeType degreeType) {
        StringBuilder stringBuffer = new StringBuilder(50);
        stringBuffer.append(code);
        stringBuffer.append(name);
        if (degreeType != null) {
            stringBuffer.append(degreeType.toString());
        }
        return StringUtils.lowerCase(stringBuffer.toString());
    }

    public static class CurriculumFactory implements Serializable {
        private CurricularCourse curricularCourse;

        private String program;

        private String programEn;

        private String generalObjectives;

        private String generalObjectivesEn;

        private String operacionalObjectives;

        private String operacionalObjectivesEn;

        private DateTime lastModification;

        public CurriculumFactory(final CurricularCourse curricularCourse) {
            setCurricularCourse(curricularCourse);
        }

        public String getGeneralObjectives() {
            return generalObjectives;
        }

        public void setGeneralObjectives(String generalObjectives) {
            this.generalObjectives = generalObjectives;
        }

        public String getGeneralObjectivesEn() {
            return generalObjectivesEn;
        }

        public void setGeneralObjectivesEn(String generalObjectivesEn) {
            this.generalObjectivesEn = generalObjectivesEn;
        }

        public String getOperacionalObjectives() {
            return operacionalObjectives;
        }

        public void setOperacionalObjectives(String operacionalObjectives) {
            this.operacionalObjectives = operacionalObjectives;
        }

        public String getOperacionalObjectivesEn() {
            return operacionalObjectivesEn;
        }

        public void setOperacionalObjectivesEn(String operacionalObjectivesEn) {
            this.operacionalObjectivesEn = operacionalObjectivesEn;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getProgramEn() {
            return programEn;
        }

        public void setProgramEn(String programEn) {
            this.programEn = programEn;
        }

        public DateTime getLastModification() {
            return lastModification;
        }

        public void setLastModification(DateTime lastModification) {
            this.lastModification = lastModification;
        }

        public CurricularCourse getCurricularCourse() {
            return curricularCourse;
        }

        public void setCurricularCourse(final CurricularCourse curricularCourse) {
            this.curricularCourse = curricularCourse;
        }

        public String getObjectives() {
            if (!StringUtils.isEmpty(getGeneralObjectives()) && !StringUtils.isEmpty(getOperacionalObjectives())) {
                return getGeneralObjectives() + " " + getOperacionalObjectives();
            }
            return null;
        }

        public String getObjectivesEn() {
            if (!StringUtils.isEmpty(getGeneralObjectivesEn()) && !StringUtils.isEmpty(getOperacionalObjectivesEn())) {
                return getGeneralObjectivesEn() + " " + getOperacionalObjectivesEn();
            }
            return null;
        }
    }

    public Curriculum editCurriculum(String program, String programEn, String generalObjectives, String generalObjectivesEn,
            String operacionalObjectives, String operacionalObjectivesEn, DateTime lastModification) {
        Curriculum curriculum = findLatestCurriculum();
        final ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
        if (!curriculum.getLastModificationDateDateTime()
                .isBefore(currentExecutionYear.getBeginDateYearMonthDay().toDateMidnight()) && !curriculum
                .getLastModificationDateDateTime().isAfter(currentExecutionYear.getEndDateYearMonthDay().toDateMidnight())) {
            curriculum.edit(generalObjectives, operacionalObjectives, program, generalObjectivesEn, operacionalObjectivesEn,
                    programEn);
        } else {
            curriculum = insertCurriculum(program, programEn, operacionalObjectives, operacionalObjectivesEn, generalObjectives,
                    generalObjectivesEn, lastModification);
        }
        return curriculum;
    }

    public Curriculum insertCurriculum(String program, String programEn, String operacionalObjectives,
            String operacionalObjectivesEn, String generalObjectives, String generalObjectivesEn, DateTime lastModification) {

        Curriculum curriculum = new Curriculum();

        curriculum.setCurricularCourse(this);
        curriculum.setProgram(program);
        curriculum.setProgramEn(programEn);
        curriculum.setOperacionalObjectives(operacionalObjectives);
        curriculum.setOperacionalObjectivesEn(operacionalObjectivesEn);
        curriculum.setGeneralObjectives(generalObjectives);
        curriculum.setGeneralObjectivesEn(generalObjectivesEn);
        curriculum.setLastModificationDateDateTime(lastModification);

        return curriculum;
    }

    public List<ExecutionCourse> getExecutionCoursesByExecutionPeriod(final ExecutionSemester executionSemester) {
        return getAssociatedExecutionCoursesSet().stream()
                .filter(execution -> execution.getExecutionPeriod().equals(executionSemester)).collect(Collectors.toList());
    }

    public List<ExecutionCourse> getExecutionCoursesByExecutionYear(final ExecutionYear executionYear) {
        return getAssociatedExecutionCoursesSet().stream()
                .filter(execution -> execution.getExecutionPeriod().getExecutionYear().equals(executionYear))
                .collect(Collectors.toList());
    }

    public Curriculum findLatestCurriculum() {
        return getAssociatedCurriculumsSet().stream().max(Comparator.comparing(Curriculum::getLastModificationDateDateTime))
                .orElse(null);
    }

    public Curriculum findLatestCurriculumModifiedBefore(Date date) {
        return getAssociatedCurriculumsSet().stream()
                .filter(curriculum -> curriculum.getLastModificationDateDateTime().toDate().before(date))
                .max(Comparator.comparing(Curriculum::getLastModificationDateDateTime)).orElse(null);
    }

    final public double getProblemsHours() {
        return getProblemsHours(null, null);
    }

    final public Double getProblemsHours(final CurricularPeriod curricularPeriod) {
        return getProblemsHours(curricularPeriod, null);
    }

    final public double getProblemsHours(final ExecutionSemester executionSemester) {
        return getProblemsHours(null, executionSemester);
    }

    final public Double getProblemsHours(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getProblemsHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    final public double getLaboratorialHours() {
        return getLaboratorialHours(null, null);
    }

    final public Double getLaboratorialHours(final CurricularPeriod curricularPeriod) {
        return getLaboratorialHours(curricularPeriod, null);
    }

    final public double getLaboratorialHours(final ExecutionSemester executionSemester) {
        return getLaboratorialHours(null, executionSemester);
    }

    final public Double getLaboratorialHours(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getLaboratorialHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    final public Double getSeminaryHours() {
        return getSeminaryHours(null, null);
    }

    final public Double getSeminaryHours(final CurricularPeriod curricularPeriod) {
        return getSeminaryHours(curricularPeriod, null);
    }

    final public double getSeminaryHours(final ExecutionSemester executionSemester) {
        return getSeminaryHours(null, executionSemester);
    }

    final public Double getSeminaryHours(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getSeminaryHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    final public double getFieldWorkHours() {
        return getFieldWorkHours(null, null);
    }

    final public Double getFieldWorkHours(final CurricularPeriod curricularPeriod) {
        return getFieldWorkHours(curricularPeriod, null);
    }

    final public double getFieldWorkHours(final ExecutionSemester executionSemester) {
        return getFieldWorkHours(null, executionSemester);
    }

    final public Double getFieldWorkHours(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getFieldWorkHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    final public double getTrainingPeriodHours() {
        return getTrainingPeriodHours(null, null);
    }

    final public Double getTrainingPeriodHours(final CurricularPeriod curricularPeriod) {
        return getTrainingPeriodHours(curricularPeriod, null);
    }

    final public double getTrainingPeriodHours(final ExecutionSemester executionSemester) {
        return getTrainingPeriodHours(null, executionSemester);
    }

    final public Double getTrainingPeriodHours(final CurricularPeriod curricularPeriod,
            final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getTrainingPeriodHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    final public double getTutorialOrientationHours() {
        return getTutorialOrientationHours(null, null);
    }

    final public Double getTutorialOrientationHours(final CurricularPeriod curricularPeriod) {
        return getTutorialOrientationHours(curricularPeriod, null);
    }

    final public double getTutorialOrientationHours(final ExecutionSemester executionSemester) {
        return getTutorialOrientationHours(null, executionSemester);
    }

    final public Double getTutorialOrientationHours(final CurricularPeriod curricularPeriod,
            final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getTutorialOrientationHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(),
                        executionSemester);
    }

    final public double getAutonomousWorkHours() {
        return getAutonomousWorkHours(null, (ExecutionSemester) null);
    }

    final public Double getAutonomousWorkHours(final CurricularPeriod curricularPeriod) {
        return getAutonomousWorkHours(curricularPeriod, (ExecutionSemester) null);
    }

    final public double getAutonomousWorkHours(final ExecutionSemester executionSemester) {
        return getAutonomousWorkHours(null, executionSemester);
    }

    final public Double getAutonomousWorkHours(final CurricularPeriod curricularPeriod, final ExecutionYear executionYear) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getAutonomousWorkHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionYear);
    }

    final public Double getAutonomousWorkHours(final CurricularPeriod curricularPeriod,
            final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getAutonomousWorkHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    final public double getContactLoad() {
        return getContactLoad(null, (ExecutionSemester) null);
    }

    final public Double getContactLoad(final CurricularPeriod curricularPeriod) {
        return getContactLoad(curricularPeriod, (ExecutionSemester) null);
    }

    final public double getContactLoad(final ExecutionSemester executionSemester) {
        return getContactLoad(null, executionSemester);
    }

    final public Double getContactLoad(final CurricularPeriod curricularPeriod, final ExecutionYear executionYear) {
        return getContactLoad(curricularPeriod, executionYear == null ? null : executionYear.getLastExecutionPeriod());
    }

    final public Double getContactLoad(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getContactLoad(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    final public double getTotalLoad() {
        return getTotalLoad(null, (ExecutionSemester) null);
    }

    final public Double getTotalLoad(final CurricularPeriod curricularPeriod) {
        return getTotalLoad(curricularPeriod, (ExecutionSemester) null);
    }

    final public double getTotalLoad(final ExecutionSemester executionSemester) {
        return getTotalLoad(null, executionSemester);
    }

    final public Double getTotalLoad(final CurricularPeriod curricularPeriod, final ExecutionYear executionYear) {
        return getTotalLoad(curricularPeriod, executionYear == null ? null : executionYear.getLastExecutionPeriod());
    }

    final public Double getTotalLoad(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getTotalLoad(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    @Override
    final public Double getLabHours() {
        return getLabHours(null, null);
    }

    final public Double getLabHours(final CurricularPeriod curricularPeriod) {
        return getLabHours(curricularPeriod, null);
    }

    final public double getLabHours(final ExecutionSemester executionSemester) {
        return getLabHours(null, executionSemester);
    }

    final public Double getLabHours(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getLaboratorialHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    @Override
    final public Double getTheoreticalHours() {
        return getTheoreticalHours(null, null);
    }

    final public Double getTheoreticalHours(final CurricularPeriod curricularPeriod) {
        return getTheoreticalHours(curricularPeriod, null);
    }

    final public double getTheoreticalHours(final ExecutionSemester executionSemester) {
        return getTheoreticalHours(null, executionSemester);
    }

    final public Double getTheoreticalHours(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? 0d : competence
                .getTheoreticalHours(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    @Override
    final public Double getPraticalHours() {
        final Double praticalHours = super.getPraticalHours();
        return praticalHours == null ? 0d : praticalHours;
    }

    @Override
    final public Double getTheoPratHours() {
        final Double theoPratHours = super.getTheoPratHours();
        return theoPratHours == null ? 0d : theoPratHours;
    }

    @Override
    final public Double getCredits() {
        return getEctsCredits();
    }

    @Override
    public Double getEctsCredits() {
        return getEctsCredits((CurricularPeriod) null, (ExecutionSemester) null);
    }

    public Double getEctsCredits(final CurricularPeriod curricularPeriod) {
        return getEctsCredits(curricularPeriod, (ExecutionSemester) null);
    }

    public Double getEctsCredits(final ExecutionSemester executionSemester) {
        return getEctsCredits((CurricularPeriod) null, executionSemester);
    }

    public Double getEctsCredits(final ExecutionYear executionYear) {
        return getEctsCredits(null, executionYear);
    }

    public Double getEctsCredits(final CurricularPeriod curricularPeriod, final ExecutionYear executionYear) {
        return getEctsCredits(curricularPeriod, executionYear == null ? null : executionYear.getLastExecutionPeriod());
    }

    public Double getEctsCredits(final CurricularPeriod curricularPeriod, final ExecutionSemester executionSemester) {
        return getEctsCredits(curricularPeriod == null ? null : curricularPeriod.getChildOrder(), executionSemester);
    }

    public Double getEctsCredits(final Integer order, final ExecutionSemester executionSemester) {
        CompetenceCourse competence = getCompetenceCourse();
        if (competence != null) {
            return competence.getEctsCredits(order, executionSemester);
        } else if (isOptionalCurricularCourse()) {
            return 0d;
        }
        throw new DomainException("CurricularCourse.with.no.ects.credits");
    }

    @Override
    public Double getMaxEctsCredits(final ExecutionSemester executionSemester) {
        return getEctsCredits(executionSemester);
    }

    @Override
    public Double getMinEctsCredits(final ExecutionSemester executionSemester) {
        return getEctsCredits(executionSemester);
    }

    @Override
    @Deprecated()
    // typo: use getWeight() instead
    final public Double getWeigth() {
        return getWeight();
    }

    final public Double getWeight() {
        if (isBolonhaDegree()) {
            return getEctsCredits();
        }

        if (getDegreeType().isMasterDegree()) {
            return getCredits();
        }

        return super.getWeigth();
    }

    final public Double getWeight(ExecutionSemester semester) {
        if (isBolonhaDegree()) {
            return getEctsCredits(semester);
        }

        if (getDegreeType().isMasterDegree()) {
            return getCredits();
        }

        return super.getWeigth();
    }

    public CurricularSemester getCurricularSemesterWithLowerYearBySemester(Integer semester, Date date) {
        return getScopesSet().stream()
                .filter(scope -> scope.getCurricularSemester().getSemester().equals(semester) && scope.isActive(date))
                .map(CurricularCourseScope::getCurricularSemester)
                .min(Comparator.comparing(CurricularSemester::getCurricularYear)).orElse(null);
    }

    private List<Enrolment> getActiveEnrollments(ExecutionSemester executionSemester, Registration registration) {
        Stream<Enrolment> enrollments = getEnrolmentsStream().filter(enrollment -> !enrollment.isAnnulled());
        if (executionSemester != null) {
            enrollments = enrollments.filter(enrollment -> enrollment.getExecutionPeriod().equals(executionSemester));
        }
        if (registration != null) {
            enrollments = enrollments
                    .filter(enrollment -> enrollment.getStudentCurricularPlan().getRegistration().equals(registration));
        }
        return enrollments.collect(Collectors.toList());
    }

    //FIXME WHY do active methods search for not annulled and not annulled search for active?
    //FIXME WHY have these add methods at all? first is not used in academic or ist integration
    public void addNotAnulledEnrolmentsForExecutionPeriod(final Collection<Enrolment> enrolments,
            final ExecutionSemester executionSemester) {
        getEnrolmentsStream().filter(enrollment -> enrollment.isActive() && enrollment.getExecutionPeriod() == executionSemester)
                .forEach(enrolments::add);
    }

    @Deprecated
    public void addActiveEnrollments(final Collection<Enrolment> enrolments, final ExecutionSemester executionSemester) {
        getEnrolmentsStream()
                .filter(enrollment -> !enrollment.isAnnulled() && enrollment.getExecutionPeriod() == executionSemester)
                .forEach(enrolments::add);
    }

    /**
     * @deprecated use {@link #getEnrolmentsByAcademicInterval(AcademicInterval)}
     */
    @Deprecated
    public List<Enrolment> getEnrolmentsByExecutionPeriod(final ExecutionSemester executionSemester) {
        List<Enrolment> result = new ArrayList<>();
        addActiveEnrollments(result, executionSemester);
        return result;
    }

    public List<Enrolment> getEnrolmentsByAcademicInterval(AcademicInterval academicInterval) {
        return getEnrolmentsStream().filter(enrollment -> !enrollment.isAnnulled() && (
                enrollment.getExecutionPeriod().getAcademicInterval().equals(academicInterval) || enrollment.getExecutionPeriod()
                        .getExecutionYear().getAcademicInterval().equals(academicInterval))).collect(Collectors.toList());
    }

    private Stream<Enrolment> getEnrolmentsStream() {
        return getCurriculumModulesSet().stream().filter(CurriculumModule::isEnrolment).map(module -> (Enrolment) module);
    }

    public List<Enrolment> getEnrolments() {
        return getEnrolmentsStream().collect(Collectors.toList());
    }

    public int countEnrolmentsByExecutionPeriod(final ExecutionSemester executionSemester) {
        return getEnrolmentsByExecutionPeriod(executionSemester).size();
    }

    public List<Enrolment> getEnrolmentsByYear(String year) {
        return getEnrolmentsByExecutionYear(ExecutionYear.readExecutionYearByName(year));
    }

    public int getNumberOfStudentsWithFirstEnrolmentIn(ExecutionSemester executionSemester) {
        Map<Student, List<Enrolment>> students = getAllEnrolmentsUntil(executionSemester).stream().collect(
                Collectors.groupingBy(enrollment -> enrollment.getStudentCurricularPlan().getRegistration().getStudent()));

        return (int) students.values().stream()
                .filter(list -> list.size() == 1 && list.get(0).getExecutionPeriod().equals(executionSemester)).count();
    }

    private List<Enrolment> getAllEnrolmentsUntil(ExecutionSemester executionSemester) {
        return getEnrolmentsStream().filter(enrollment -> !enrollment.isAnnulled() && enrollment.getExecutionPeriod()
                .isBeforeOrEquals(executionSemester)).collect(Collectors.toList());
    }

    public List<Enrolment> getEnrolmentsByExecutionYear(ExecutionYear executionYear) {
        return getEnrolmentsStream()
                .filter(enrollment -> enrollment.getExecutionPeriod().getExecutionYear().equals(executionYear))
                .collect(Collectors.toList());
    }

    public boolean hasEnrolmentsForExecutionYear(final ExecutionYear executionYear) {
        return getEnrolmentsStream()
                .anyMatch(enrollment -> enrollment.getExecutionPeriod().getExecutionYear().equals(executionYear));
    }

    public Enrolment getEnrolmentByStudentAndYear(Registration registration, String year) {
        return getEnrolmentsStream()
                .filter(enrollment -> enrollment.getStudentCurricularPlan().getRegistration().equals(registration) && enrollment
                        .getExecutionPeriod().getExecutionYear().getYear().equals(year)).findFirst().orElse(null);
    }

    public List<Enrolment> getActiveEnrollments(Registration registration) {
        return getActiveEnrollments(null, registration);
    }

    public List<Enrolment> getActiveEnrollments() {
        return getActiveEnrollments(null, null);
    }

    public List<Enrolment> getActiveEnrollments(ExecutionSemester executionSemester) {
        List<Enrolment> enrolments = new ArrayList<>();
        addActiveEnrollments(enrolments, executionSemester);
        return enrolments;
    }

    private Stream<Dismissal> getDismissalsStream(ExecutionSemester executionSemester) {
        return getCurriculumModulesSet().stream().filter(CurriculumModule::isDismissal).map(module -> (Dismissal) module)
                .filter(dismissal -> dismissal.getExecutionPeriod() == executionSemester);
    }

    public List<Dismissal> getDismissals(ExecutionSemester executionSemester) {
        return getDismissalsStream(executionSemester).collect(Collectors.toList());
    }

    public List<Enrolment> getActiveEnrollments(ExecutionYear executionYear) {
        return executionYear.getExecutionPeriodsSet().stream().flatMap(semester -> getActiveEnrollments(semester).stream())
                .collect(Collectors.toList());
    }

    @Override
    public String getName(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? super.getName() : competence.getName(period);
    }

    @Override
    public String getName() {
        return getName(null);
    }

    @Override
    public String getNameEn(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? super.getNameEn() : competence.getNameEn(period);
    }

    @Override
    public String getNameEn() {
        return getNameEn(null);
    }

    public String getAcronym(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? super.getAcronym() : competence.getAcronym(period);
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
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? super.getBasic() : competence.isBasic(period);
    }

    public DepartmentUnit getDepartmentUnit(ExecutionSemester semester) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getDepartmentUnit(semester);
    }

    public DepartmentUnit getDepartmentUnit() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getDepartmentUnit();
    }

    public String getObjectives(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getObjectives(period);
    }

    public String getObjectives() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getObjectives();
    }

    public String getObjectivesEn(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getObjectivesEn(period);
    }

    public String getObjectivesEn() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getObjectivesEn();
    }

    public MultiLanguageString getObjectivesI18N(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getObjectivesI18N(period);
    }

    public String getProgram(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getProgram(period);
    }

    public String getProgram() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getProgram();
    }

    public String getProgramEn(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getProgramEn(period);
    }

    public String getProgramEn() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getProgramEn();
    }

    public MultiLanguageString getProgramI18N(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getProgramI18N(period);
    }

    public MultiLanguageString getPrerequisitesI18N() {
        return new MultiLanguageString(MultiLanguageString.pt, getPrerequisites())
                .with(MultiLanguageString.en, getPrerequisitesEn());
    }

    public String getEvaluationMethod(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getEvaluationMethod(period);
    }

    public String getEvaluationMethod() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getEvaluationMethod();
    }

    public String getEvaluationMethodEn(ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getEvaluationMethodEn(period);
    }

    public String getEvaluationMethodEn() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getEvaluationMethodEn();
    }

    public MultiLanguageString getEvaluationMethodI18N(ExecutionSemester period) {
        return new MultiLanguageString(MultiLanguageString.pt, getEvaluationMethod(period))
                .with(MultiLanguageString.en, getEvaluationMethodEn(period));
    }

    public RegimeType getRegime(final ExecutionSemester period) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getRegime(period);
    }

    public RegimeType getRegime(final ExecutionYear executionYear) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getRegime(executionYear);
    }

    public RegimeType getRegime() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : competence.getRegime();
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
        CompetenceCourse competence = getCompetenceCourse();
        return competence != null && competence.isDissertation();
    }

    public boolean isAnual() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence != null && competence.isAnual();
    }

    public boolean isAnual(final ExecutionYear executionYear) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence != null && competence.isAnual(executionYear);
    }

    public boolean isSemestrial(final ExecutionYear executionYear) {
        CompetenceCourse competence = getCompetenceCourse();
        return competence != null && competence.isSemestrial(executionYear);
    }

    public boolean isEquivalent(CurricularCourse oldCurricularCourse) {
        CompetenceCourse competence = getCompetenceCourse();
        return equals(oldCurricularCourse) || (competence != null && competence.getAssociatedCurricularCoursesSet()
                .contains(oldCurricularCourse));
    }

    public boolean hasScopeForCurricularYear(final Integer curricularYear, final ExecutionSemester executionSemester) {
        return getDegreeModuleScopes().stream().anyMatch(
                scope -> scope.isActiveForExecutionPeriod(executionSemester) && scope.getCurricularYear().equals(curricularYear));
    }

    static public List<CurricularCourse> readByCurricularStage(final CurricularStage curricularStage) {
        return CurricularCourse.readCurricularCourses().stream()
                .filter(curricular -> curricular.getCurricularStage() != null && curricular.getCurricularStage()
                        .equals(curricularStage)).collect(Collectors.toList());
    }

    public Set<CurricularCourseScope> findCurricularCourseScopesIntersectingPeriod(final Date beginDate, final Date endDate) {
        return getScopesSet().stream().filter(scope -> scope.intersects(beginDate, endDate)).collect(Collectors.toSet());
    }

    public Set<CurricularCourseScope> findCurricularCourseScopesIntersectingExecutionCourse(ExecutionCourse executionCourse) {
        AcademicInterval academicInterval = executionCourse.getAcademicInterval();
        return findCurricularCourseScopesIntersectingPeriod(academicInterval.getStart().toDate(),
                academicInterval.getEnd().toDate());
    }

    public Set<Enrolment> getEnrolmentsNotInAnyMarkSheet(EvaluationSeason season, ExecutionSemester executionSemester) {
        Set<Enrolment> result = getEnrolmentsStream().filter(enrollment ->
                (enrollment.isValid(executionSemester) && (season.isSpecialAuthorization() || (
                        season.equals(enrollment.getEvaluationSeason()) && !enrollment
                                .hasAssociatedMarkSheetOrFinalGrade(season)))) || (season.isImprovement() && enrollment
                        .hasImprovementFor(executionSemester) && !enrollment.hasAssociatedMarkSheet(season)))
                .collect(Collectors.toSet());
        if (season.isImprovement()) {
            final DegreeCurricularPlan degreeCurricularPlan = getDegreeCurricularPlan();
            getCurricularCourseEquivalencesSet().stream().filter(equivalence -> equivalence.isFrom(degreeCurricularPlan))
                    .flatMap(equivalence -> equivalence.getOldCurricularCoursesSet().stream())
                    .filter(CurricularCourse::isBolonhaDegree).flatMap(CurricularCourse::getEnrolmentsStream)
                    .filter(enrollment -> enrollment.hasImprovementFor(executionSemester) && !enrollment
                            .hasAssociatedMarkSheet(season)).forEach(result::add);
        }
        return result;
    }

    public Set<Enrolment> getEnrolmentsNotInAnyMarkSheetForOldMarkSheets(EvaluationSeason season,
            ExecutionSemester executionSemester) {
        return getEnrolmentsStream().filter(enrollment ->
                (enrollment.isValid(executionSemester) && season.equals(enrollment.getEvaluationSeason()) && (
                        season.isSpecialAuthorization() || enrollment.canBeSubmittedForOldMarkSheet(season))) || (
                        season.isImprovement() && enrollment.hasImprovementFor(executionSemester) && enrollment
                                .canBeSubmittedForOldMarkSheet(season))).collect(Collectors.toSet());
    }

    private boolean hasEnrolmentsNotInAnyMarkSheet(ExecutionSemester executionSemester) {
        return getEnrolmentsStream().anyMatch(enrolment ->
                (enrolment.isValid(executionSemester) && enrolment.getEvaluationSeason().isNormal() && !enrolment
                        .hasAssociatedMarkSheetOrFinalGrade(EvaluationSeason.readNormalSeason())) || (enrolment.hasImprovement()
                        && !enrolment.hasAssociatedMarkSheet(EvaluationSeason.readImprovementSeason()) && enrolment
                        .hasImprovementFor(executionSemester)));
    }

    public MarkSheet createNormalMarkSheet(ExecutionSemester executionSemester, Teacher responsibleTeacher, Date evaluationDate,
            EvaluationSeason season, Boolean submittedByTeacher, Collection<MarkSheetEnrolmentEvaluationBean> evaluationBeans,
            Person person) {
        return MarkSheet.createNormal(this, executionSemester, responsibleTeacher, evaluationDate, season, submittedByTeacher,
                evaluationBeans, person);
    }

    public MarkSheet createOldNormalMarkSheet(ExecutionSemester executionSemester, Teacher responsibleTeacher,
            Date evaluationDate, EvaluationSeason season, Collection<MarkSheetEnrolmentEvaluationBean> evaluationBeans,
            Person person) {
        return MarkSheet
                .createOldNormal(this, executionSemester, responsibleTeacher, evaluationDate, season, evaluationBeans, person);
    }

    public MarkSheet rectifyEnrolmentEvaluation(MarkSheet markSheet, EnrolmentEvaluation enrolmentEvaluation, Date evaluationDate,
            Grade grade, String reason, Person person) {

        if (markSheet == null || evaluationDate == null || grade.isEmpty()) {
            throw new DomainException("error.markSheet.invalid.arguments");
        }

        if (!markSheet.getEnrolmentEvaluationsSet().contains(enrolmentEvaluation)) {
            throw new DomainException("error.no.student.in.markSheet");
        }

        if (markSheet.isNotConfirmed()) {
            throw new DomainException("error.markSheet.must.be.confirmed");
        }

        if (enrolmentEvaluation.getRectification() != null) {
            throw new DomainException("error.markSheet.student.alreadyRectified",
                    enrolmentEvaluation.getEnrolment().getStudentCurricularPlan().getRegistration().getNumber().toString());
        }

        enrolmentEvaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
        // enrolmentEvaluation.setWhenDateTime(new DateTime());

        MarkSheet rectificationMarkSheet =
                createRectificationMarkSheet(markSheet.getExecutionPeriod(), evaluationDate, markSheet.getResponsibleTeacher(),
                        markSheet.getEvaluationSeason(), reason,
                        new MarkSheetEnrolmentEvaluationBean(enrolmentEvaluation.getEnrolment(), evaluationDate, grade), person);

        // Rectification MarkSheet MUST have only ONE EnrolmentEvaluation
        rectificationMarkSheet.getEnrolmentEvaluationsSet().iterator().next().setRectified(enrolmentEvaluation);
        return rectificationMarkSheet;
    }

    public MarkSheet rectifyOldEnrolmentEvaluation(EnrolmentEvaluation enrolmentEvaluation, EvaluationSeason season,
            Date evaluationDate, Grade newGrade, String reason, Person person) {

        if (enrolmentEvaluation == null || evaluationDate == null || newGrade.isEmpty()) {
            throw new DomainException("error.markSheet.invalid.arguments");
        }

        if (enrolmentEvaluation.getRectification() != null) {
            throw new DomainException("error.markSheet.student.alreadyRectified",
                    enrolmentEvaluation.getEnrolment().getStudentCurricularPlan().getRegistration().getNumber().toString());
        }

        enrolmentEvaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);

        MarkSheet rectificationMarkSheet =
                createRectificationOldMarkSheet(enrolmentEvaluation.getExecutionPeriod(), evaluationDate,
                        enrolmentEvaluation.getPersonResponsibleForGrade().getTeacher(), season, reason,
                        new MarkSheetEnrolmentEvaluationBean(enrolmentEvaluation.getEnrolment(), evaluationDate, newGrade),
                        person);

        // Rectification MarkSheet MUST have only ONE EnrolmentEvaluation
        rectificationMarkSheet.getEnrolmentEvaluationsSet().iterator().next().setRectified(enrolmentEvaluation);
        return rectificationMarkSheet;

    }

    private MarkSheet createRectificationMarkSheet(ExecutionSemester executionSemester, Date evaluationDate,
            Teacher responsibleTeacher, EvaluationSeason season, String reason, MarkSheetEnrolmentEvaluationBean evaluationBean,
            Person person) {

        return MarkSheet
                .createRectification(this, executionSemester, responsibleTeacher, evaluationDate, season, reason, evaluationBean,
                        person);
    }

    public MarkSheet createRectificationOldMarkSheet(ExecutionSemester executionSemester, Date evaluationDate,
            Teacher responsibleTeacher, EvaluationSeason season, String reason, MarkSheetEnrolmentEvaluationBean evaluationBean,
            Person person) {

        return MarkSheet.createOldRectification(this, executionSemester, responsibleTeacher, evaluationDate, season, reason,
                evaluationBean, person);
    }

    public Collection<MarkSheet> searchMarkSheets(ExecutionSemester executionSemester, Teacher teacher, Date evaluationDate,
            MarkSheetState markSheetState, EvaluationSeason season) {

        Stream<MarkSheet> markSheets = getMarkSheetsSet().stream();

        if (executionSemester != null) {
            markSheets.filter(sheet -> sheet.getExecutionPeriod().equals(executionSemester));
        }
        if (teacher != null) {
            markSheets.filter(sheet -> sheet.getResponsibleTeacher().equals(teacher));
        }
        if (evaluationDate != null) {
            markSheets.filter(sheet ->
                    DateFormatUtil.compareDates("dd/MM/yyyy", evaluationDate, sheet.getEvaluationDateDateTime().toDate()) == 0);
        }
        if (markSheetState != null) {
            markSheets.filter(sheet -> sheet.getMarkSheetState().equals(markSheetState));
        }
        if (season != null) {
            markSheets.filter(sheet -> sheet.getEvaluationSeason().equals(season));
        }
        return markSheets.collect(Collectors.toList());
    }

    public boolean hasScopeInGivenSemesterAndCurricularYearInDCP(CurricularYear curricularYear,
            DegreeCurricularPlan degreeCurricularPlan, final ExecutionSemester executionSemester) {

        return (degreeCurricularPlan == null || getDegreeCurricularPlan().equals(degreeCurricularPlan)) && getDegreeModuleScopes()
                .stream().anyMatch(
                        s -> s.isActiveForExecutionPeriod(executionSemester) && (curricularYear == null || s.getCurricularYear()
                                .equals(curricularYear.getYear())));
    }

    public boolean isGradeSubmissionAvailableFor(ExecutionSemester executionSemester) {
        return EvaluationSeason.all().anyMatch(season -> season.isGradeSubmissionAvailable(this, executionSemester));
    }

    public ExecutionDegree getExecutionDegreeFor(AcademicInterval academicInterval) {
        return getDegreeCurricularPlan().getExecutionDegreeByAcademicInterval(academicInterval);
    }

    @Deprecated
    public ExecutionDegree getExecutionDegreeFor(ExecutionYear executionYear) {
        return getDegreeCurricularPlan().getExecutionDegreeByYear(executionYear);
    }

    public boolean hasAnyDegreeGradeToSubmit(ExecutionSemester period) {
        return hasEnrolmentsNotInAnyMarkSheet(period);
    }

    public boolean hasAnyDegreeMarkSheetToConfirm(ExecutionSemester period) {
        return getMarkSheetsSet().stream().anyMatch(sheet -> sheet.getExecutionPeriod().equals(period) && sheet.isNotConfirmed());
    }

    public List<DegreeModuleScope> getDegreeModuleScopes() {
        return DegreeModuleScope.getDegreeModuleScopes(this);
    }

    private int countAssociatedStudentsByExecutionPeriodAndEnrolmentNumber(ExecutionSemester executionSemester,
            int enrolmentNumber) {
        return (int) getEnrolmentsStream().filter(enrollment -> enrollment.getExecutionPeriod() == executionSemester)
                .map(Enrolment::getStudentCurricularPlan).mapToLong(plan -> plan.getEnrolmentsSet().stream()
                        .filter(enrollment -> enrollment.getCurricularCourse() == this && (
                                enrollment.getExecutionPeriod().compareTo(executionSemester) <= 0)).count())
                .filter(count -> count == enrolmentNumber).count();
    }

    public Integer getTotalEnrolmentStudentNumber(ExecutionSemester executionSemester) {
        return (int) getEnrolmentsStream().filter(enrollment -> enrollment.getExecutionPeriod() == executionSemester).count();
    }

    public Integer getFirstTimeEnrolmentStudentNumber(ExecutionSemester executionSemester) {
        return countAssociatedStudentsByExecutionPeriodAndEnrolmentNumber(executionSemester, 1);
    }

    public Integer getSecondTimeEnrolmentStudentNumber(ExecutionSemester executionSemester) {
        return getTotalEnrolmentStudentNumber(executionSemester) - getFirstTimeEnrolmentStudentNumber(executionSemester);
    }

    public List<ExecutionCourse> getMostRecentExecutionCourses() {
        ExecutionSemester period = ExecutionSemester.readActualExecutionSemester();

        while (period != null) {
            List<ExecutionCourse> executionCourses = getExecutionCoursesByExecutionPeriod(period);
            if (executionCourses != null && !executionCourses.isEmpty()) {
                return executionCourses;
            }

            period = period.getPreviousExecutionPeriod();
        }

        return new ArrayList<>();
    }

    public boolean isActive(final ExecutionYear executionYear) {
        final ExecutionYear executionYearToCheck =
                executionYear == null ? ExecutionYear.readCurrentExecutionYear() : executionYear;
        return executionYearToCheck.getExecutionPeriodsSet().stream().anyMatch(this::isActive);
    }

    public boolean isActive(final ExecutionSemester executionSemester) {
        return getActiveScopesInExecutionPeriod(executionSemester).size() > 0
                || getActiveDegreeModuleScopesInExecutionPeriod(executionSemester).size() > 0;
    }

    public boolean hasEnrolmentForPeriod(final ExecutionSemester executionSemester) {
        return getEnrolmentsStream()
                .anyMatch(enrollment -> !enrollment.isAnnulled() && enrollment.getExecutionPeriod() == executionSemester);
    }

    @Override
    public void getAllDegreeModules(final Collection<DegreeModule> degreeModules) {
        degreeModules.add(this);
    }

    public List<CurricularCourseEquivalence> getOldCurricularCourseEquivalences(final DegreeCurricularPlan degreeCurricularPlan) {
        return getOldCurricularCourseEquivalencesSet().stream().filter(equivalence -> equivalence.isFrom(degreeCurricularPlan))
                .collect(Collectors.toList());
    }

    public List<CurricularCourseEquivalence> getCurricularCourseEquivalencesFor(
            final CurricularCourse equivalentCurricularCourse) {
        return getOldCurricularCourseEquivalencesSet().stream()
                .filter(equivalence -> equivalence.getEquivalentCurricularCourse() == equivalentCurricularCourse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCurricularCourse() {
        return true;
    }

    public DegreeModuleScope getOldestDegreeModuleScope() {
        return getDegreeModuleScopes().stream()
                .sorted(DegreeModuleScope.COMPARATOR_BY_CURRICULAR_YEAR_AND_SEMESTER_AND_CURRICULAR_COURSE_NAME).findFirst()
                .orElse(null);
    }

    @Override
    public Integer getMinimumValueForAcumulatedEnrollments() {
        return super.getMinimumValueForAcumulatedEnrollments() == null ? Integer.valueOf(0) : super
                .getMinimumValueForAcumulatedEnrollments();
    }

    @Override
    public Integer getMaximumValueForAcumulatedEnrollments() {
        return super.getMaximumValueForAcumulatedEnrollments() == null ? Integer.valueOf(0) : super
                .getMaximumValueForAcumulatedEnrollments();
    }

    public BigDecimal getTotalHoursByShiftType(ShiftType type, ExecutionSemester executionSemester) {
        if (type != null) {
            Double hours = null;
            switch (type) {
            case TEORICA:
                hours = getTheoreticalHours(executionSemester);
                break;
            case TEORICO_PRATICA:
                hours = getTheoPratHours();
                break;
            case PRATICA:
                hours = getPraticalHours();
                break;
            case PROBLEMS:
                hours = getProblemsHours(executionSemester);
                break;
            case LABORATORIAL:
                hours = getLabHours(executionSemester);
                break;
            case TRAINING_PERIOD:
                hours = getTrainingPeriodHours(executionSemester);
                break;
            case SEMINARY:
                hours = getSeminaryHours(executionSemester);
                break;
            case TUTORIAL_ORIENTATION:
                hours = getTutorialOrientationHours(executionSemester);
                break;
            case FIELD_WORK:
                hours = getFieldWorkHours(executionSemester);
                break;
            default:
                break;
            }
            return hours != null ? BigDecimal.valueOf(hours)
                    .multiply(BigDecimal.valueOf(CompetenceCourseLoad.NUMBER_OF_WEEKS)) : null;
        }
        return null;
    }

    public boolean hasAnyExecutionCourseIn(ExecutionSemester executionSemester) {
        return getAssociatedExecutionCoursesSet().stream()
                .anyMatch(execution -> execution.getExecutionPeriod().equals(executionSemester));
    }

    @Override
    public Set<CurricularCourse> getAllCurricularCourses() {
        return Collections.singleton(this);
    }

    @Override
    public Set<CurricularCourse> getAllCurricularCourses(ExecutionSemester executionSemester) {
        return getAllCurricularCourses();
    }

    public boolean getCanCreateMarkSheet() {
        return !isDissertation() || (isDissertation() && MarkSheetPredicates.checkDissertation(getDegree()));
    }

    public Collection<MarkSheet> getMarkSheetsByPeriod(ExecutionSemester executionSemester) {
        return getMarkSheetsSet().stream().filter(sheet -> sheet.getExecutionPeriod() == executionSemester)
                .collect(Collectors.toSet());
    }

    @Override
    public void doForAllCurricularCourses(final CurricularCourseFunctor curricularCourseFunctor) {
        curricularCourseFunctor.doWith(this);
    }

    public CompetenceCourseLevel getCompetenceCourseLevel() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? null : getCompetenceCourse().getCompetenceCourseLevel();
    }

    public boolean hasCompetenceCourseLevel() {
        return getCompetenceCourseLevel() != null;
    }

    public List<EnrolmentEvaluation> getEnrolmentEvaluationsForOldMarkSheet(final ExecutionSemester executionSemester,
            final EvaluationSeason season) {
        Stream<EnrolmentEvaluation> evaluations;
        if (season.isImprovement()) {
            evaluations = getEnrolmentsStream().map(enrollment -> enrollment.getLatestEnrolmentEvaluationBySeason(season))
                    .filter(Objects::nonNull).filter(evaluation -> evaluation.getExecutionPeriod().equals(executionSemester));
        } else {
            evaluations = getEnrolmentsStream().filter(enrollment -> enrollment.isValid(executionSemester))
                    .map(enrollment -> enrollment.getLatestEnrolmentEvaluationBySeason(season)).filter(Objects::nonNull);
        }
        return evaluations.filter(evaluation -> evaluation.isFinal() && evaluation.getExamDateYearMonthDay() != null)
                .collect(Collectors.toList());
    }

    public boolean hasExecutionDegreeByYearAndCampus(ExecutionYear executionYear, Space campus) {
        return getDegreeCurricularPlan().hasExecutionDegreeByYearAndCampus(executionYear, campus);
    }

    public boolean hasAnyExecutionDegreeFor(ExecutionYear executionYear) {
        return getDegreeCurricularPlan().hasAnyExecutionDegreeFor(executionYear);
    }

    @Override
    public void applyToCurricularCourses(final ExecutionYear executionYear, final Predicate predicate) {
        predicate.evaluate(this);
    }

    @Override
    public void addAssociatedExecutionCourses(final ExecutionCourse associatedExecutionCourses) {
        Collection<ExecutionCourse> executionCourses = getAssociatedExecutionCoursesSet();

        if (getAssociatedExecutionCoursesSet().stream().anyMatch(execution -> associatedExecutionCourses != execution
                && execution.getExecutionPeriod() == associatedExecutionCourses.getExecutionPeriod())) {
            throw new DomainException("error.executionCourse.curricularCourse.already.associated");
        }
        super.addAssociatedExecutionCourses(associatedExecutionCourses);
    }

    public String getCodeAndName(final ExecutionInterval executionInterval) {
        final String code = getCode();
        final String name = getNameI18N(executionInterval).getContent();

        return (StringUtils.isEmpty(code) ? "" : code + " - ") + name;
    }

    @Override
    public String getCode() {
        CompetenceCourse competence = getCompetenceCourse();
        return competence == null ? super.getCode() : competence.getCode();
    }

    public void setWeight(final BigDecimal input) {
        super.setWeigth(input == null ? null : input.doubleValue());
    }

    /**
     * @deprecated Use {@link #getWeight()} instead.
     */
    @Deprecated
    public Double getBaseWeight() {
        return super.getWeigth();
    }
}
