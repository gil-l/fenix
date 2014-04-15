package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sourceforge.fenixedu.domain.Enrolment;
import net.sourceforge.fenixedu.domain.OptionalEnrolment;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.EnrolmentCertificateRequest;

import org.fenixedu.oddjet.table.CategoricalTableData;

import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class EnrollmentCertificateODTDocument extends CertificateODTDocument {

    public EnrollmentCertificateODTDocument(String templatePath, EnrolmentCertificateRequest documentRequest)
            throws SecurityException, IOException {
        super(templatePath, documentRequest);
        setUp(documentRequest);
    }

    private void setUp(EnrolmentCertificateRequest documentRequest) {

        addCurricularYear(documentRequest);

        addParameter("isDetailed", documentRequest.getDetailed());
        if (documentRequest.getDetailed()) {
            final Collection<Enrolment> enrollments =
                    new TreeSet<Enrolment>(Enrolment.COMPARATOR_BY_EXECUTION_YEAR_AND_NAME_AND_ID);

            enrollments.addAll(documentRequest.getEntriesToReport());
            List<Object> name = new ArrayList<>();
            List<Object> ects = new ArrayList<>();
            String creditsDescription = "";
            if (documentRequest.isToShowCredits()) {
                creditsDescription =
                        documentRequest.isRequestForRegistration() ? documentRequest.getDegreeType().getCreditsDescription() : "";
                addEnrollmentData(enrollments, name, ects, creditsDescription);
            } else {
                addEnrollmentData(enrollments, name);
            }

            Map<String, List<Object>> enrolledCourses = new HashMap<>();
            addTableDataSource("enrolledCourses", new CategoricalTableData(enrolledCourses));

            enrollments.clear();
            enrollments.addAll(documentRequest.getExtraCurricularEntriesToReport());
            if (!enrollments.isEmpty()) {
                name = new ArrayList<>();
                ects = new ArrayList<>();
                enrolledCourses = new HashMap<>();
                addParameter("hasExtraCurricular", true);
                if (documentRequest.isToShowCredits()) {
                    addEnrollmentData(enrollments, name, ects, creditsDescription);
                } else {
                    addEnrollmentData(enrollments, name);
                }
                addTableDataSource("enrolledCoursesExtra", new CategoricalTableData(enrolledCourses));
            } else {
                addParameter("hasExtraCurricular", true);
            }

            enrollments.clear();
            enrollments.addAll(documentRequest.getPropaedeuticEntriesToReport());
            if (!enrollments.isEmpty()) {
                name = new ArrayList<>();
                ects = new ArrayList<>();
                enrolledCourses = new HashMap<>();
                addParameter("hasPropaedeutics", true);
                if (documentRequest.isToShowCredits()) {
                    addEnrollmentData(enrollments, name, ects, creditsDescription);
                } else {
                    addEnrollmentData(enrollments, name);
                }
                addTableDataSource("enrolledCoursesProp", new CategoricalTableData(enrolledCourses));
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

    private Object getEnrollmentName(Enrolment e) {
        final MultiLanguageString eNameMLS;
        if (e instanceof OptionalEnrolment) {
            final OptionalEnrolment optionalEnrolment = (OptionalEnrolment) e;
            eNameMLS = optionalEnrolment.getCurricularCourse().getNameI18N();
        } else {
            eNameMLS = e.getName();
        }
        String eName = eNameMLS.getContent(Language.valueOf(getLocale().getLanguage()));
        if (eName == null || eName.trim().isEmpty()) {
            eName = eNameMLS.getPreferedContent();
        }
        return eName;
    }

}