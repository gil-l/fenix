package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.FileNotFoundException;
import java.io.InputStream;

import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.organizationalStructure.UniversityUnit;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.domain.student.Student;
import net.sourceforge.fenixedu.util.Bundle;
import net.sourceforge.fenixedu.util.StringFormatter;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.oddjet.Template;
import org.fenixedu.oddjet.exception.DocumentLoadException;
import org.joda.time.DateTime;

public class AdministrativeOfficeODTDocument extends Template {

    protected DocumentRequest documentRequest;

    public AdministrativeOfficeODTDocument(String templatePath, DocumentRequest documentRequest) throws DocumentLoadException,
            FileNotFoundException {
        super(getTemplateAsResource(templatePath, documentRequest), documentRequest.getLanguage());
        this.documentRequest = documentRequest;
        setup();
    }

    public static InputStream getTemplateAsResource(String templatePath, DocumentRequest documentRequest)
            throws FileNotFoundException {
        InputStream template = documentRequest.getClass().getResourceAsStream(templatePath);
        if (template == null) {
            throw new FileNotFoundException("Missing template file: " + templatePath);
        }
        return template;
    }

    private void setup() {

        Unit adminOfficeUnit = documentRequest.getAdministrativeOffice().getUnit();
        Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        Unit universityUnit = UniversityUnit.getInstitutionsUniversityUnitByDate(new DateTime());
        Person coordinator = adminOfficeUnit.getActiveUnitCoordinator();
        Registration registration = documentRequest.getRegistration();
        ExecutionYear executionYear = getExecutionYear();
        Student student = registration.getStudent();
        DegreeType degreeType = registration.getDegreeType();

        //coordinator info
        addParameter("coordinator", coordinator);

        //units info
        addParameter("administrativeOfficeName", adminOfficeUnit.getPartyName());
        addParameter("universityName", universityUnit.getPartyName());
        addParameter("institutionName", institutionUnit.getPartyName());

        //student general info
        addParameter("student", student.getPerson());
        //getLocalizedName is not forseen by Oddjet (when getContent(Locale) is available delete parameter and use student.idDocumentType
        addParameter("idDocType", student.getPerson().getIdDocumentType().getLocalizedName(getLocale()));
        //Once pretty print is no longer necessary delete parameters and use student.parishOfBirth and student.districtSubdivisionOfBirth
        addParameter("parishOfBirth", StringFormatter.prettyPrint(student.getPerson().getParishOfBirth()));
        addParameter("districtSubdivisionOfBirth",
                StringFormatter.prettyPrint(student.getPerson().getDistrictSubdivisionOfBirth()));

        //registration and year related info
        //a student's number may vary between registrations (for old students mostly)
        addParameter("registrationStudentNumber", registration.getNumber());
        addParameter("degreeDescription", registration.getDegreeDescription(executionYear,
                degreeType.hasExactlyOneCycleType() ? degreeType.getCycleType() : registration.getCycleType(executionYear),
                        getLocale()));
        addParameter("isRegistered", new Boolean(executionYear.containsDate(new DateTime())));
        addParameter("schoolYear", executionYear.getYear());

        //document request info
        addParameter("documentNumber", documentRequest.getServiceRequestNumber());
        addParameter("civilYear", documentRequest.getAcademicServiceRequestYear().getYear());
    }

    protected void addCurricularYear() {
        //curricular year info
        addParameter("hasOnlyOneCurricularYear", documentRequest.getDegreeType().hasExactlyOneCurricularYear());
        String registrationCurricularYear = "";
        if (!documentRequest.getDegreeType().hasExactlyOneCurricularYear()) {
            ExecutionYear executionYear = getExecutionYear();
            final Integer curricularYear = Integer.valueOf(documentRequest.getRegistration().getCurricularYear(executionYear));

            registrationCurricularYear =
                    BundleUtil.getString(Bundle.ENUMERATION, getLocale(), curricularYear.toString() + ".ordinal");
        }
        addParameter("registrationCurricularYear", registrationCurricularYear);
    }

    protected ExecutionYear getExecutionYear() {
        return documentRequest.hasExecutionYear() ? documentRequest.getExecutionYear() : ExecutionYear
                .readByDateTime(documentRequest.getRequestDate());
    }
}