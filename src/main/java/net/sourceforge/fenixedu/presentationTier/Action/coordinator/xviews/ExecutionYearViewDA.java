package net.sourceforge.fenixedu.presentationTier.Action.coordinator.xviews;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.CurricularYear;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.Enrolment;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Grade;
import net.sourceforge.fenixedu.domain.GradeScale;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;
import net.sourceforge.fenixedu.presentationTier.Action.masterDegree.coordinator.CoordinatedDegreeInfo;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixWebFramework.struts.annotations.Tile;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Mapping(path = "/analytics", module = "coordinator")
@Forwards({ @Forward(name = "showHome", path = "/coordinator/analytics/home.jsp", tileProperties = @Tile(
        title = "private.coordinator.management.courses.analytictools.executionyear")) })
public class ExecutionYearViewDA extends FenixDispatchAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CoordinatedDegreeInfo.setCoordinatorContext(request);
        return super.execute(mapping, actionForm, request, response);
    }

    private JsonArray computeExecutionYearsForDegreeCurricularPlan(DegreeCurricularPlan degreeCurricularPlan) {
        JsonArray executionYears = new JsonArray();
        //TODO: CHECK IF THIS IS THE VALID WAY TO DO THINGS; IMPORTED FROM PREVIOUS CODE
        for (ExecutionYear year : Bennu.getInstance().getExecutionYearsSet()) {
            if (year.isInclusivelyBetween(degreeCurricularPlan.getInauguralExecutionYear(),
                    degreeCurricularPlan.getLastExecutionYear())) {
                executionYears.add(executionYearToJson(year));
            }

        }
        return executionYears;
    }

    private JsonObject executionYearToJson(ExecutionYear year) {
        JsonObject executionYearJson = new JsonObject();
        executionYearJson.addProperty("id", year.getExternalId());
        executionYearJson.addProperty("name", year.getQualifiedName());
        return executionYearJson;
    }

    public ActionForward showHome(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        User userView = Authenticate.getUser();

        String degreeCurricularPlanID = request.getParameter("degreeCurricularPlanID");
        DegreeCurricularPlan degreeCurricularPlan = FenixFramework.getDomainObject(degreeCurricularPlanID);

        JsonArray executionYears = computeExecutionYearsForDegreeCurricularPlan(degreeCurricularPlan);
        request.setAttribute("executionYears", executionYears);

        String executionYearId = request.getParameter("executionYear");

        ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();

        if (executionYearId != null) {
            currentExecutionYear = FenixFramework.getDomainObject(executionYearId);
        }

        request.setAttribute("degreeCurricularPlanID", degreeCurricularPlanID);
        request.setAttribute("currentExecutionYear", computeExecutionYearStatistics(degreeCurricularPlan, currentExecutionYear));

        return mapping.findForward("showHome");

    }

    private Set<Enrolment> getDegreeCurricularPlanEnrolmentsForExecutionYear(DegreeCurricularPlan degreeCurricularPlan,
            ExecutionYear executionYear) {
        Set<Enrolment> enrolments = new HashSet<Enrolment>();
        for (StudentCurricularPlan scp : degreeCurricularPlan.getStudentCurricularPlansSet()) {
            for (Enrolment enrol : scp.getEnrolmentsSet()) {
                if (enrol.getExecutionPeriod().getExecutionYear() == executionYear
                        && enrol.getParentCycleCurriculumGroup() != null
                        && degreeCurricularPlan.getCycleCourseGroup(enrol.getParentCycleCurriculumGroup().getCycleType()) != null) {
                    enrolments.add(enrol);
                }
            }
        }
        return enrolments;
    }

    private static class CurricularCourseGradeEntry {

        BigDecimal sum = BigDecimal.ZERO;

        Integer notEvaluated = 0;
        Integer approved = 0;
        Integer flunked = 0;
        Integer attending = 0;

        List<Enrolment> enrolmentList = new ArrayList<Enrolment>();

        Integer quantity = 0;

        public CurricularCourseGradeEntry(Enrolment enrolment) {
            plus(enrolment);
        }

        public CurricularCourseGradeEntry plus(Enrolment enrolment) {
            enrolmentList.add(enrolment);
            Grade grade = enrolment.getGrade();
            if (grade == null || grade.isEmpty()) {
                attending++;
            } else if (grade.isApproved()) {
                approved++;
                if (grade.getGradeScale() == GradeScale.TYPE20) {
                    sum = sum.add(grade.getNumericValue());
                    quantity++;
                }
            } else if (grade.isNotEvaluated()) {
                notEvaluated++;
            } else if (grade.isNotApproved()) {
                flunked++;
            }
            return this;
        }

        public BigDecimal getAverage() {
            if (quantity > 0) {
                return sum.divide(new BigDecimal(quantity), RoundingMode.HALF_EVEN);
            } else {
                return BigDecimal.ZERO;
            }

        }

        public int getTotal() {
            return notEvaluated + approved + flunked + attending;
        }

    }

    private static class CurricularYearGradeEntry {

        BigDecimal sum = BigDecimal.ZERO;

        Integer notEvaluated = 0;
        Integer approved = 0;
        Integer flunked = 0;
        Integer attending = 0;

        Integer quantity = 0;

        public CurricularYearGradeEntry(Enrolment enrolment) {
            plus(enrolment);
        }

        public CurricularYearGradeEntry plus(Enrolment enrolment) {
            Grade grade = enrolment.getGrade();
            if (grade == null || grade.isEmpty()) {
                attending++;
            } else if (grade.isApproved()) {
                approved++;
                if (grade.getGradeScale() == GradeScale.TYPE20) {
                    sum = sum.add(grade.getNumericValue());
                    quantity++;
                }
            } else if (grade.isNotEvaluated()) {
                notEvaluated++;
            } else if (grade.isNotApproved()) {
                flunked++;
            }
            return this;
        }

        public BigDecimal getAverage() {
            if (quantity > 0) {
                return sum.divide(new BigDecimal(quantity), RoundingMode.HALF_EVEN);
            } else {
                return BigDecimal.ZERO;
            }

        }

        public int getTotal() {
            return notEvaluated + approved + flunked + attending;
        }
    }

    private JsonObject computeExecutionYearStatistics(DegreeCurricularPlan degreeCurricularPlan, ExecutionYear executionYear) {
        JsonObject result = new JsonObject();
        result.addProperty("id", executionYear.getExternalId());
        result.addProperty("name", executionYear.getQualifiedName());

        Map<CurricularYear, CurricularYearGradeEntry> curricularYearGradeMap =
                new HashMap<CurricularYear, CurricularYearGradeEntry>();

        Map<CurricularCourse, CurricularCourseGradeEntry> curricularCourseGradeMap =
                new HashMap<CurricularCourse, CurricularCourseGradeEntry>();

        for (Enrolment enrolment : getDegreeCurricularPlanEnrolmentsForExecutionYear(degreeCurricularPlan, executionYear)) {
            updateCurricularYearGradeMapIfRelevant(curricularYearGradeMap, enrolment);
            updateCurricularCourseGradeMapIfRelevant(curricularCourseGradeMap, enrolment);
        }

        int degreeCurricularPlanAttending = 0;
        int degreeCurricularPlanApproved = 0;
        int degreeCurricularPlanNotEvaluated = 0;
        int degreeCurricularPlanFlunked = 0;

        JsonArray curricularYearsJsonArray = new JsonArray();
        for (Entry<CurricularYear, CurricularYearGradeEntry> entry : curricularYearGradeMap.entrySet()) {

            JsonObject curricularYearJsonObject = new JsonObject();
            curricularYearJsonObject.addProperty("year", entry.getKey().getYear());
            curricularYearJsonObject.addProperty("average", entry.getValue().getAverage());
            curricularYearJsonObject.addProperty("total", entry.getValue().getTotal());

            int curricularYearApproved = entry.getValue().approved;
            degreeCurricularPlanApproved += curricularYearApproved;

            int curricularYearFlunked = entry.getValue().flunked;
            degreeCurricularPlanFlunked += curricularYearFlunked;

            int curricularYearNotEvaluated = entry.getValue().notEvaluated;
            degreeCurricularPlanNotEvaluated += curricularYearNotEvaluated;

            int curricularYearAttending = entry.getValue().attending;
            degreeCurricularPlanAttending += curricularYearAttending;

            curricularYearJsonObject.addProperty("approved", curricularYearApproved);
            curricularYearJsonObject.addProperty("flunked", curricularYearFlunked);
            curricularYearJsonObject.addProperty("not-evaluated", curricularYearNotEvaluated);
            curricularYearJsonObject.addProperty("attending", curricularYearAttending);

            curricularYearsJsonArray.add(curricularYearJsonObject);
        }

        result.addProperty("attending", degreeCurricularPlanAttending);
        result.addProperty("approved", degreeCurricularPlanApproved);
        result.addProperty("notEvaluated", degreeCurricularPlanNotEvaluated);
        result.addProperty("flunked", degreeCurricularPlanFlunked);

        result.addProperty("total", degreeCurricularPlanAttending + degreeCurricularPlanApproved
                + degreeCurricularPlanNotEvaluated + degreeCurricularPlanFlunked);

        JsonArray curricularCoursesJsonArray = new JsonArray();

        int years = degreeCurricularPlan.getDegreeType().getYears();
        for (int i = 1; i <= years; i++) {
            JsonObject year = new JsonObject();
            year.addProperty("year", i);
            JsonArray curricularCoursesArray = new JsonArray();
            for (CurricularCourse curricularCourse : degreeCurricularPlan.getCurricularCoursesByExecutionYearAndCurricularYear(
                    executionYear, i)) {
                if (curricularCourseGradeMap.containsKey(curricularCourse)) {
                    CurricularCourseGradeEntry entry = curricularCourseGradeMap.get(curricularCourse);

                    JsonObject curricularCourseJsonObject = new JsonObject();
                    int curricularCourseApproved = entry.approved;
                    int curricularCourseFlunked = entry.flunked;
                    int curricularCourseNotEvaluated = entry.notEvaluated;
                    int curricularCourseAttending = entry.attending;

                    curricularCourseJsonObject.addProperty("acronym", curricularCourse.getAcronym());
                    curricularCourseJsonObject.addProperty("name", curricularCourse.getName());

                    curricularCourseJsonObject.addProperty("approved", curricularCourseApproved);
                    curricularCourseJsonObject.addProperty("flunked", curricularCourseFlunked);
                    curricularCourseJsonObject.addProperty("not-evaluated", curricularCourseNotEvaluated);
                    curricularCourseJsonObject.addProperty("attending", curricularCourseAttending);

                    curricularCourseJsonObject.addProperty("average", entry.getAverage());

                    curricularCourseJsonObject.addProperty("total", entry.getTotal());

                    JsonArray gradesArray = new JsonArray();
                    for (Enrolment enrolment : entry.enrolmentList) {
                        Grade grade = enrolment.getGrade();
                        JsonObject enrolmentJson = new JsonObject();
                        if (grade != null && grade.isApproved()) {
                            enrolmentJson.addProperty("grade", grade.getIntegerValue());
                        } else if (grade.isNotEvaluated()) {
                            enrolmentJson.addProperty("grade", "NA");
                        } else if (grade.isNotApproved()) {
                            enrolmentJson.addProperty("grade", "RE");
                        }
                        gradesArray.add(enrolmentJson);
                    }
                    curricularCourseJsonObject.add("grades", gradesArray);

                    curricularCoursesArray.add(curricularCourseJsonObject);
                }
            }
            year.add("entries", curricularCoursesArray);

            curricularCoursesJsonArray.add(year);
        }

        result.add("curricular-years", curricularYearsJsonArray);
        result.add("curricular-courses", curricularCoursesJsonArray);
        return result;

    }

    private void updateCurricularYearGradeMapIfRelevant(Map<CurricularYear, CurricularYearGradeEntry> map, Enrolment enrolment) {
        CurricularYear year = CurricularYear.readByYear(enrolment.getRegistration().getCurricularYear());
        if (map.containsKey(year)) {
            map.get(year).plus(enrolment);
        } else {
            map.put(year, new CurricularYearGradeEntry(enrolment));
        }
    }

    private void updateCurricularCourseGradeMapIfRelevant(Map<CurricularCourse, CurricularCourseGradeEntry> map,
            Enrolment enrolment) {
        CurricularCourse curricularCourse = enrolment.getCurricularCourse();
        if (map.containsKey(curricularCourse)) {
            map.get(curricularCourse).plus(enrolment);
        } else {
            map.put(curricularCourse, new CurricularCourseGradeEntry(enrolment));
        }
    }

}
