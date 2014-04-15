package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.util.Locale;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;

public class RegistrationDeclarationODTDocument extends DeclarationODTDocument {

    public RegistrationDeclarationODTDocument(String template, DocumentRequest documentRequest) throws SecurityException,
            IOException {
        super(template, documentRequest);
        setUp(documentRequest);
    }

    private void setUp(DocumentRequest documentRequest) {
        addReportName(Locale.ENGLISH, "Registration Declaration");
        addReportName(new Locale("pt"), "Declaração de Matrícula");
    }

}