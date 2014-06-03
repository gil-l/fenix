package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.FileNotFoundException;

import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;

import org.fenixedu.oddjet.exception.DocumentLoadException;

public class RegistrationDeclarationODTDocument extends DeclarationODTDocument {

    public RegistrationDeclarationODTDocument(String template, DocumentRequest documentRequest) throws DocumentLoadException,
            FileNotFoundException {
        super(template, documentRequest);
    }

}