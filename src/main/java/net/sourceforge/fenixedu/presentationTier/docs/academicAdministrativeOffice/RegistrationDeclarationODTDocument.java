package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;

public class RegistrationDeclarationODTDocument extends DeclarationODTDocument {

    public RegistrationDeclarationODTDocument(String template, DocumentRequest documentRequest) throws SecurityException,
            IOException {
        super(template, documentRequest);
    }

}