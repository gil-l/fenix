package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sourceforge.fenixedu.domain.Enrolment;
import net.sourceforge.fenixedu.domain.OptionalEnrolment;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.EnrolmentCertificateRequest;

import org.fenixedu.oddjet.exception.DocumentLoadException;
import org.fenixedu.oddjet.table.CategoricalTableData;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class EnrollmentCertificateODTDocument extends CertificateODTDocument {

    public EnrollmentCertificateODTDocument(String templatePath, EnrolmentCertificateRequest documentRequest)
            throws DocumentLoadException, FileNotFoundException {
        super(templatePath, documentRequest);
        setup(documentRequest);
    }

    private void setup(EnrolmentCertificateRequest documentRequest) {

        addCurricularYear();

        addParameter("isDetailed", documentRequest.getDetailed());
        if (documentRequest.getDetailed()) {
            final Collection<Enrolment> enrollments =
                    new TreeSet<Enrolment>(Enrolment.COMPARATOR_BY_EXECUTION_YEAR_AND_NAME_AND_ID);

            enrollments.addAll(documentRequest.getEntriesToReport());

            addTableDataSource("enrolledCourses", getEnrollmentTableData(enrollments));

            enrollments.clear();
            enrollments.addAll(documentRequest.getExtraCurricularEntriesToReport());
            if (!enrollments.isEmpty()) {
                addParameter("hasExtraCurricular", true);
                addTableDataSource("enrolledCoursesExtra", getEnrollmentTableData(enrollments));
            } else {
                addParameter("hasExtraCurricular", false);
            }

            enrollments.clear();
            enrollments.addAll(documentRequest.getPropaedeuticEntriesToReport());
            if (!enrollments.isEmpty()) {
                addParameter("hasPropaedeutics", true);
                addTableDataSource("enrolledCoursesProp", getEnrollmentTableData(enrollments));
            } else {
                addParameter("hasPropaedeutics", false);
            }
        }
    }

    private void addEnrollmentData(Collection<Enrolment> enrollments, List<Object> name, List<Object> ects,
            String creditsDescription) {
        for (Enrolment e : enrollments) {
            name.add(getEnrollmentName(e));
            ects.add(e.getCurricularCourse().getEctsCredits(e.getExecutionPeriod()).toString() + creditsDescription);
        }
    }

    private void addEnrollmentData(Collection<Enrolment> enrollments, List<Object> name) {
        for (Enrolment e : enrollments) {
            name.add(getEnrollmentName(e));
        }
    }

    private CategoricalTableData getEnrollmentTableData(Collection<Enrolment> enrollments) {
        Map<String, List> enrolledCourses = new HashMap<>();
        List<Object> name = new ArrayList<>();
        List<Object> ects = new ArrayList<>();
        if (documentRequest.isToShowCredits()) {
            String creditsDescription = "";
            creditsDescription =
                    documentRequest.isRequestForRegistration() ? documentRequest.getDegreeType().getCreditsDescription() : "";
                    addEnrollmentData(enrollments, name, ects, creditsDescription);
        } else {
            addEnrollmentData(enrollments, name);
        }
        enrolledCourses.put("name", name);
        enrolledCourses.put("ects", ects);
        return new CategoricalTableData(enrolledCourses);
    }

    private Object getEnrollmentName(Enrolment e) {
        final MultiLanguageString eNameMLS;
        if (e instanceof OptionalEnrolment) {
            final OptionalEnrolment optionalEnrolment = (OptionalEnrolment) e;
            eNameMLS = optionalEnrolment.getCurricularCourse().getNameI18N();
        } else {
            eNameMLS = e.getName();
        }
        String eName = eNameMLS.getContent(getLocale());
        if (eName == null || eName.trim().isEmpty()) {
            eName = eNameMLS.getPreferedContent();
        }
        return eName.toUpperCase();
    }

}