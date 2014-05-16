package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.CertificateRequest;

public class RegistrationCertificateODTDocument extends CertificateODTDocument {

    public RegistrationCertificateODTDocument(String templatePath, CertificateRequest documentRequest) throws SecurityException,
            IOException {
        super(templatePath, documentRequest);
    }
}