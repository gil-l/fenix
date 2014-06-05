package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.FileNotFoundException;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;
import net.sourceforge.fenixedu.util.Bundle;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.oddjet.exception.DocumentLoadException;

public class DeclarationODTDocument extends AdministrativeOfficeODTDocument {

    public DeclarationODTDocument(String template, DocumentRequest documentRequest) throws DocumentLoadException,
    FileNotFoundException {
        super(template, documentRequest);
        setup();
    }

    private void setup() {
        addParameter("supervisingUnit",
                BundleUtil.getString(Bundle.ACADEMIC, getLocale(), "label.academicDocument.direcaoAcademica"));
    }
}