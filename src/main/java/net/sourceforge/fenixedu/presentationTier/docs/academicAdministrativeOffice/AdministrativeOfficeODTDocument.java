package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.organizationalStructure.UniversityUnit;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.domain.student.Student;
import net.sourceforge.fenixedu.util.StringFormatter;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.oddjet.Template;
import org.joda.time.DateTime;

import pt.utl.ist.fenix.tools.util.i18n.Language;

public class AdministrativeOfficeODTDocument extends Template {

    public AdministrativeOfficeODTDocument(String templatePath, DocumentRequest documentRequest) throws SecurityException,
            IOException {
        super(documentRequest.getClass().getResourceAsStream(templatePath), new Locale(documentRequest.getLanguage().name()));
        setUp(documentRequest);
    }

    private void setUp(DocumentRequest documentRequest) {

        Unit adminOfficeUnit = documentRequest.getAdministrativeOffice().getUnit();
        Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        Unit universityUnit = UniversityUnit.getInstitutionsUniversityUnitByDate(new DateTime());
        Person coordinator = adminOfficeUnit.getActiveUnitCoordinator();
        Registration registration = documentRequest.getRegistration();
        ExecutionYear executionYear = getExecutionYear(documentRequest);
        Student student = registration.getStudent();
        DegreeType degreeType = registration.getDegreeType();

        addParameter("administrativeOfficeCoordinator", coordinator.getName());
        addParameter("coordinatorGender", coordinator.getGender());

        String adminOfficeName = adminOfficeUnit.getPartyName().getContent(Language.valueOf(getLocale().getLanguage()));
        if (adminOfficeName == null || adminOfficeName.trim().isEmpty()) {
            adminOfficeName = adminOfficeUnit.getPartyName().getPreferedContent();
        }
        addParameter("administrativeOfficeName", adminOfficeName.toUpperCase());
        String universityName = universityUnit.getPartyName().getContent(Language.valueOf(getLocale().getLanguage()));
        if (universityName == null) {
            universityName = universityUnit.getPartyName().getPreferedContent();
        }
        addParameter("universityName", universityName.toUpperCase());
        String institutionName = institutionUnit.getPartyName().getContent(Language.valueOf(getLocale().getLanguage()));
        if (institutionName == null) {
            institutionName = institutionUnit.getPartyName().getPreferedContent();
        }
        addParameter("institutionName", institutionName.toUpperCase());

        addParameter("studentGender", student.getPerson().getGender());
        addParameter("studentNumber", registration.getNumber());
        addParameter("fullName", student.getPerson().getName().toUpperCase());
        addParameter("idDocType", student.getPerson().getIdDocumentType().getLocalizedName(getLocale()));
        addParameter("idDocNumber", student.getPerson().getDocumentIdNumber());
        addParameter("parishOfBirth", StringFormatter.prettyPrint(student.getPerson().getParishOfBirth()));
        addParameter("districtSubdivisionOfBirth",
                StringFormatter.prettyPrint(student.getPerson().getDistrictSubdivisionOfBirth()));
        addParameter("nationality", student.getPerson().getCountry().getFilteredNationality(getLocale()).toUpperCase());
        addParameter("isRegistered", new Boolean(executionYear.containsDate(new DateTime())));

        addParameter("schoolYear", executionYear.getYear());
        addParameter("degreeDescription", registration.getDegreeDescription(executionYear,
                degreeType.hasExactlyOneCycleType() ? degreeType.getCycleType() : registration.getCycleType(executionYear),
                getLocale()));

        addParameter("documentNumber", documentRequest.getServiceRequestNumber());
        addParameter("civilYear", documentRequest.getAcademicServiceRequestYear().getYear());
    }

    final protected void addCurricularYear(DocumentRequest documentRequest) {
        addParameter("hasOnlyOneCurricularYear", documentRequest.getDegreeType().hasExactlyOneCurricularYear());
        String studentCurricularYear = "";
        if (!documentRequest.getDegreeType().hasExactlyOneCurricularYear()) {
            ExecutionYear executionYear = getExecutionYear(documentRequest);
            final Integer curricularYear = Integer.valueOf(documentRequest.getRegistration().getCurricularYear(executionYear));

            studentCurricularYear =
                    ResourceBundle.getBundle("resources.EnumerationResources", getLocale())
                            .getString(curricularYear.toString() + ".ordinal").toUpperCase();
        }
        addParameter("studentCurricularYear", studentCurricularYear);
    }

    final protected ExecutionYear getExecutionYear(DocumentRequest documentRequest) {
        return documentRequest.hasExecutionYear() ? documentRequest.getExecutionYear() : ExecutionYear
                .readByDateTime(documentRequest.getRequestDate());
    }
}