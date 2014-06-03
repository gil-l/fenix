package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;

public class IRSDeclarationODTDocument extends DeclarationODTDocument {

    public IRSDeclarationODTDocument(String templatePath, DocumentRequest documentRequest) throws SecurityException, IOException {
        super(templatePath, documentRequest);
        setup(documentRequest);
    }

    private void setup(DocumentRequest documentRequest) {

    }
}