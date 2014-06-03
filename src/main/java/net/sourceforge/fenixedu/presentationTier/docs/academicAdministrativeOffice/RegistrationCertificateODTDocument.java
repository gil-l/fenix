package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.FileNotFoundException;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.CertificateRequest;

import org.fenixedu.oddjet.exception.DocumentLoadException;

public class RegistrationCertificateODTDocument extends CertificateODTDocument {

    public RegistrationCertificateODTDocument(String templatePath, CertificateRequest documentRequest)
            throws DocumentLoadException, FileNotFoundException {
        super(templatePath, documentRequest);
    }
}