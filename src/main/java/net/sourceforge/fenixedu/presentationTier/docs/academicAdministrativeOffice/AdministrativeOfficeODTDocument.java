package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.oddjet.Template;
import org.joda.time.DateTime;

import pt.utl.ist.fenix.tools.util.i18n.Language;

public class AdministrativeOfficeODTDocument extends Template {

    protected DocumentRequest documentRequest;

    static final protected String EMPTY_STR = StringUtils.EMPTY;

    static final protected String SINGLE_SPACE = " ";

    static final protected String YYYYMMMDD = "yyyyMMdd";

    public AdministrativeOfficeODTDocument(String templatePath, DocumentRequest documentRequest) throws SecurityException,
            IOException {
        super(getTemplateAsResource(templatePath, documentRequest), new Locale(documentRequest.getLanguage().name()));
        this.documentRequest = documentRequest;
        setUp();
    }

    public String getReportFileName() {
        final StringBuilder result = new StringBuilder();

        result.append(documentRequest.getPerson().getIstUsername());
        result.append("-");
        result.append(new DateTime().toString(YYYYMMMDD, getLocale()));
        result.append("-");
        result.append(documentRequest.getDescription().replace(":", EMPTY_STR).replace(SINGLE_SPACE, EMPTY_STR));
        result.append("-");
        result.append(getLocale().toString());

        return result.toString();
    }

    public static InputStream getTemplateAsResource(String templatePath, DocumentRequest documentRequest)
            throws FileNotFoundException {
        InputStream template = documentRequest.getClass().getResourceAsStream(templatePath);
        if (template == null) {
            throw new FileNotFoundException("Missing template file: " + templatePath);
        }
        return template;
    }

    private void setUp() {

        Unit adminOfficeUnit = documentRequest.getAdministrativeOffice().getUnit();
        Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        Unit universityUnit = UniversityUnit.getInstitutionsUniversityUnitByDate(new DateTime());
        Person coordinator = adminOfficeUnit.getActiveUnitCoordinator();
        Registration registration = documentRequest.getRegistration();
        ExecutionYear executionYear = getExecutionYear();
        Student student = registration.getStudent();
        DegreeType degreeType = registration.getDegreeType();

        addParameter("administrativeOfficeCoordinator", coordinator.getName());
        addParameter("coordinatorGender", coordinator.getGender());

        String adminOfficeName = adminOfficeUnit.getPartyName().getContent(Language.valueOf(getLocale().getLanguage()));
        if (adminOfficeName == null || adminOfficeName.trim().isEmpty()) {
            adminOfficeName = adminOfficeUnit.getPartyName().getContent();
        }
        addParameter("administrativeOfficeName", adminOfficeName);
        addParameter("administrativeOfficeNameCaps", adminOfficeName.toUpperCase(getLocale()));
        String universityName = universityUnit.getPartyName().getContent(Language.valueOf(getLocale().getLanguage()));
        if (universityName == null) {
            universityName = universityUnit.getPartyName().getContent();
        }
        addParameter("universityName", universityName.toUpperCase(getLocale()));
        String institutionName = institutionUnit.getPartyName().getContent(Language.valueOf(getLocale().getLanguage()));
        if (institutionName == null) {
            institutionName = institutionUnit.getPartyName().getContent();
        }
        addParameter("institutionName", institutionName);
        addParameter("institutionNameCaps", institutionName.toUpperCase(getLocale()));

        addParameter("studentGender", student.getPerson().getGender());
        addParameter("studentNumber", registration.getNumber());
        addParameter("fullName", student.getPerson().getName().toUpperCase(getLocale()));
        addParameter("idDocType", student.getPerson().getIdDocumentType().getLocalizedName(getLocale()));
        addParameter("idDocNumber", student.getPerson().getDocumentIdNumber());
        addParameter("parishOfBirth", StringFormatter.prettyPrint(student.getPerson().getParishOfBirth()));
        addParameter("districtSubdivisionOfBirth",
                StringFormatter.prettyPrint(student.getPerson().getDistrictSubdivisionOfBirth()));
        addParameter("nationality", student.getPerson().getCountry().getFilteredNationality(getLocale()).toUpperCase(getLocale()));
        addParameter("isRegistered", new Boolean(executionYear.containsDate(new DateTime())));

        addParameter("schoolYear", executionYear.getYear());
        addParameter("degreeDescription", registration.getDegreeDescription(executionYear,
                degreeType.hasExactlyOneCycleType() ? degreeType.getCycleType() : registration.getCycleType(executionYear),
                getLocale()));

        addParameter("documentNumber", documentRequest.getServiceRequestNumber());
        addParameter("civilYear", documentRequest.getAcademicServiceRequestYear().getYear());
    }

    protected void addCurricularYear() {
        addParameter("hasOnlyOneCurricularYear", documentRequest.getDegreeType().hasExactlyOneCurricularYear());
        String studentCurricularYear = "";
        if (!documentRequest.getDegreeType().hasExactlyOneCurricularYear()) {
            ExecutionYear executionYear = getExecutionYear();
            final Integer curricularYear = Integer.valueOf(documentRequest.getRegistration().getCurricularYear(executionYear));

            studentCurricularYear =
                    ResourceBundle.getBundle("resources.EnumerationResources", getLocale())
                            .getString(curricularYear.toString() + ".ordinal").toUpperCase(getLocale());
        }
        addParameter("studentCurricularYear", studentCurricularYear);
    }

    protected ExecutionYear getExecutionYear() {
        return documentRequest.hasExecutionYear() ? documentRequest.getExecutionYear() : ExecutionYear
                .readByDateTime(documentRequest.getRequestDate());
    }
}