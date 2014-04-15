package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.util.Locale;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.CertificateRequest;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;

public class RegistrationCertificateODTDocument extends CertificateODTDocument {

    public RegistrationCertificateODTDocument(String templatePath, CertificateRequest documentRequest) throws SecurityException,
            IOException {
        super(templatePath, documentRequest);
        setUp(documentRequest);
    }

    private void setUp(DocumentRequest documentRequest) {
        addReportName(Locale.ENGLISH, "Registration Certificate");
        addReportName(new Locale("pt"), "Certidão de Matrícula");
    }
}