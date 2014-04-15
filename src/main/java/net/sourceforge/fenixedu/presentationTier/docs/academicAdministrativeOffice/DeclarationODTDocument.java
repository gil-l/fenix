package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.util.ResourceBundle;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;

public class DeclarationODTDocument extends AdministrativeOfficeODTDocument {

    public DeclarationODTDocument(String template, DocumentRequest documentRequest) throws SecurityException, IOException {
        super(template, documentRequest);
        setUp(documentRequest);
    }

    private void setUp(DocumentRequest documentRequest) {
        addParameter(
                "supervisingUnit",
                ResourceBundle.getBundle("resources.AcademicAdminOffice", getLocale()).getString(
                        "label.academicDocument.direcaoAcademica"));
    }
}