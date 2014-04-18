package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.util.List;
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

        addCurricularYear();

        final List<Enrolment> enrolments = (List<Enrolment>) documentRequest.getRegistration().getEnrolments(getExecutionYear());
        Integer numberEnrolments = Integer.valueOf(enrolments.size());
        addParameter("totalEnrolledCourses", numberEnrolments);

        addParameter("isFirstTime", "");
        addParameter("previousExecutionYear", "");
        addParameter("hasApprovement", "");
        if (documentRequest.getDocumentPurposeType() == DocumentPurposeType.PPRE) {
            ExecutionYear executionYear = getExecutionYear();
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
        if (documentRequest.getDocumentPurposeType() != null) {
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