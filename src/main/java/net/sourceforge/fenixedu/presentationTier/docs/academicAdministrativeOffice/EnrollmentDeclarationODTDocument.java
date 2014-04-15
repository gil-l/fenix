package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.fenixedu.domain.Enrolment;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentPurposeType;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.EnrolmentDeclarationRequest;
import net.sourceforge.fenixedu.domain.student.Registration;

import org.apache.commons.lang.StringUtils;

public class EnrollmentDeclarationODTDocument extends DeclarationODTDocument {

    public EnrollmentDeclarationODTDocument(String templatePath, EnrolmentDeclarationRequest documentRequest)
            throws SecurityException, IOException {
        super(templatePath, documentRequest);
        setUp(documentRequest);
    }

    private void setUp(EnrolmentDeclarationRequest documentRequest) {
        addReportName(Locale.ENGLISH, "Enrollment Decalaration");
        addReportName(new Locale("pt"), "Declaração de Inscrição");

        addCurricularYear(documentRequest);
        final List<Enrolment> enrolments =
                (List<Enrolment>) documentRequest.getRegistration().getEnrolments(getExecutionYear(documentRequest));
        Integer numberEnrolments = Integer.valueOf(enrolments.size());
        addParameter("totalEnrolledCourses", numberEnrolments);

        boolean purposePPRE = documentRequest.getDocumentPurposeType() == DocumentPurposeType.PPRE;
        addParameter("isPurposePPRE", purposePPRE);
        addParameter("previousExecutionYear", "");
        if (purposePPRE) {
            ExecutionYear executionYear = getExecutionYear(documentRequest);
            Registration registration = documentRequest.getRegistration();

            boolean transition = registration.isTransition(executionYear);
            boolean isFirstTime = registration.isFirstTime(executionYear) && !transition;
            addParameter("isFirstTime", isFirstTime);
            if (!isFirstTime) {
                final Registration registrationToInspect = transition ? registration.getSourceRegistration() : registration;
                addParameter("hasApprovement", registrationToInspect.hasApprovement(executionYear.getPreviousExecutionYear()));
                addParameter("previousExecutionYear", executionYear.getPreviousExecutionYear().getYear());
            }
        }

        String documentPurpose = "";
        boolean hasPurpose = documentRequest.getDocumentPurposeType() != null;
        addParameter("hasPurpose", hasPurpose);
        if (hasPurpose) {
            if (documentRequest.getDocumentPurposeType() == DocumentPurposeType.OTHER
                    && !StringUtils.isEmpty(documentRequest.getOtherDocumentPurposeTypeDescription())) {
                documentPurpose = documentRequest.getOtherDocumentPurposeTypeDescription().toUpperCase();
            } else {
                documentPurpose =
                        ResourceBundle.getBundle("resources.EnumerationResources", getLocale())
                                .getString(documentRequest.getDocumentPurposeType().name()).toUpperCase();
            }
        }
        addParameter("documentPurpose", documentPurpose);

    }
}