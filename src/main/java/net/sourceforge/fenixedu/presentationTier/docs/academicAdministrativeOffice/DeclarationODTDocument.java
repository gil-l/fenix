package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.FileNotFoundException;
import java.util.ResourceBundle;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;

import org.fenixedu.oddjet.exception.DocumentLoadException;

public class DeclarationODTDocument extends AdministrativeOfficeODTDocument {

    public DeclarationODTDocument(String template, DocumentRequest documentRequest) throws DocumentLoadException,
            FileNotFoundException {
        super(template, documentRequest);
        setup();
    }

    private void setup() {
        addParameter(
                "supervisingUnit",
                ResourceBundle.getBundle("resources.AcademicAdminOffice", getLocale()).getString(
                        "label.academicDocument.direcaoAcademica"));
    }
}