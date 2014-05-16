package net.sourceforge.fenixedu.domain.serviceRequests.documentRequests;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.sf.jasperreports.engine.JRException;
import net.sourceforge.fenixedu.dataTransferObject.serviceRequests.AcademicServiceRequestBean;
import net.sourceforge.fenixedu.dataTransferObject.serviceRequests.DocumentRequestCreateBean;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.documents.DocumentRequestGeneratedDocument;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.serviceRequests.AcademicServiceRequest;
import net.sourceforge.fenixedu.domain.serviceRequests.RegistryCode;
import net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice.AdministrativeOfficeDocument;
import net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice.EnrollmentCertificateODTDocument;
import net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice.EnrollmentDeclarationODTDocument;
import net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice.RegistrationCertificateODTDocument;
import net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice.RegistrationDeclarationODTDocument;
import net.sourceforge.fenixedu.util.report.ReportsUtils;

import org.fenixedu.oddjet.exception.DocumentLoadException;
import org.fenixedu.oddjet.exception.DocumentSaveException;
import org.fenixedu.oddjet.exception.OpenOfficeConnectionException;

public abstract class DocumentRequest extends DocumentRequest_Base implements IDocumentRequest {
    public static Comparator<AcademicServiceRequest> COMPARATOR_BY_REGISTRY_NUMBER = new Comparator<AcademicServiceRequest>() {
        @Override
        public int compare(AcademicServiceRequest o1, AcademicServiceRequest o2) {
            int codeCompare = RegistryCode.COMPARATOR_BY_CODE.compare(o1.getRegistryCode(), o2.getRegistryCode());
            if (codeCompare != 0) {
                return codeCompare;
            } else {
                return o1.getExternalId().compareTo(o2.getExternalId());
            }
        }
    };

    protected DocumentRequest() {
        super();
    }

    protected void checkParameters(final DocumentRequestCreateBean bean) {
        if (bean.getChosenDocumentPurposeType() == DocumentPurposeType.OTHER && bean.getOtherPurpose() == null) {
            throw new DomainException(
                    "error.serviceRequests.documentRequests.DocumentRequest.otherDocumentPurposeTypeDescription.cannot.be.null.for.other.purpose.type");
        }
    }

    @Override
    public String getDescription() {
        return getDescription(getAcademicServiceRequestType(), getDocumentRequestType().getQualifiedName());
    }

    @Override
    public AcademicServiceRequestType getAcademicServiceRequestType() {
        return AcademicServiceRequestType.DOCUMENT;
    }

    @Override
    abstract public DocumentRequestType getDocumentRequestType();

    @Override
    abstract public String getDocumentTemplateKey();

    abstract public boolean isPagedDocument();

    @Override
    final public boolean isCertificate() {
        return getDocumentRequestType().isCertificate();
    }

    final public boolean isDeclaration() {
        return getDocumentRequestType().isDeclaration();
    }

    @Override
    final public boolean isDiploma() {
        return getDocumentRequestType().isDiploma();
    }

    @Override
    final public boolean isPastDiploma() {
        return getDocumentRequestType().isPastDiploma();
    }

    @Override
    public boolean isRegistryDiploma() {
        return getDocumentRequestType().isRegistryDiploma();
    }

    @Override
    final public boolean isDiplomaSupplement() {
        return getDocumentRequestType().isDiplomaSupplement();
    }

    @Override
    protected void internalChangeState(AcademicServiceRequestBean academicServiceRequestBean) {
        super.internalChangeState(academicServiceRequestBean);

        if (academicServiceRequestBean.isToProcess()) {
            if (!getFreeProcessed()) {
                assertPayedEvents();
            }
        }
    }

    protected void assertPayedEvents() {
        if (getRegistration().hasGratuityDebtsCurrently()) {
            throw new DomainException("DocumentRequest.registration.has.not.payed.gratuities");
        }

        if (getRegistration().hasInsuranceDebtsCurrently()) {
            throw new DomainException("DocumentRequest.registration.has.not.payed.insurance.fees");
        }

        if (getRegistration().hasAdministrativeOfficeFeeAndInsuranceDebtsCurrently(getAdministrativeOffice())) {
            throw new DomainException("DocumentRequest.registration.has.not.payed.administrative.office.fees");
        }
    }

    protected void assertPayedEvents(final ExecutionYear executionYear) {
        if (executionYear != null) {
            if (getRegistration().hasGratuityDebts(executionYear)) {
                throw new DomainException("DocumentRequest.registration.has.not.payed.gratuities");
            }

            if (getRegistration().hasInsuranceDebts(executionYear)) {
                throw new DomainException("DocumentRequest.registration.has.not.payed.insurance.fees");
            }

            if (getRegistration().hasAdministrativeOfficeFeeAndInsuranceDebts(getAdministrativeOffice(), executionYear)) {
                throw new DomainException("DocumentRequest.registration.has.not.payed.administrative.office.fees");
            }
        }
    }

    @Override
    public boolean isDownloadPossible() {
        return getLastGeneratedDocument() != null && !isCancelled() && !isRejected();
    }

    @Override
    final public boolean isToShowCredits() {
        return getDegreeType() != DegreeType.DEGREE;
    }

    public boolean hasNumberOfPages() {
        return getNumberOfPages() != null && getNumberOfPages().intValue() != 0;
    }

    public Locale getLocale() {
        return null;
    }

    @Override
    protected void checkRulesToDelete() {
        super.checkRulesToDelete();
        if (hasRegistryCode()) {
            throw new DomainException("error.AcademicServiceRequest.cannot.be.deleted");
        }
    }

    @Override
    public byte[] generateDocument() {
        try {
            switch (getDocumentRequestType()) {
            case ENROLMENT_DECLARATION:
                return new EnrollmentDeclarationODTDocument(
                        "/odtreports/academicOffice/enrollment/declaration/DeclaracaoInscricao"
                                + getLanguage().name().toUpperCase() + ".odt", (EnrolmentDeclarationRequest) this)
                        .getInstancePDFByteArray();
            case ENROLMENT_CERTIFICATE:
                return new EnrollmentCertificateODTDocument("/odtreports/academicOffice/enrollment/certificate/CertidaoInscricao"
                        + getLanguage().name().toUpperCase() + ".odt", (EnrolmentCertificateRequest) this)
                        .getInstancePDFByteArray();
            case SCHOOL_REGISTRATION_CERTIFICATE:
                return new RegistrationCertificateODTDocument(
                        "/odtreports/academicOffice/registration/certificate/CertidaoMatricula"
                                + getLanguage().name().toUpperCase() + ".odt", (CertificateRequest) this)
                        .getInstancePDFByteArray();
            case SCHOOL_REGISTRATION_DECLARATION:
                return new RegistrationDeclarationODTDocument(
                        "/odtreports/academicOffice/registration/declaration/DeclaracaoMatricula"
                                + getLanguage().name().toUpperCase() + ".odt", this).getInstancePDFByteArray();
            default:
                List<AdministrativeOfficeDocument> documents =
                        AdministrativeOfficeDocument.AdministrativeOfficeDocumentCreator.create(this);
                final AdministrativeOfficeDocument[] array = {};
                byte[] data = ReportsUtils.exportMultipleToPdfAsByteArray(documents.toArray(array));
                DocumentRequestGeneratedDocument.store(this, documents.iterator().next().getReportFileName() + ".pdf", data);

                return data;
            }
        } catch (JRException | SecurityException | DocumentLoadException | DocumentSaveException | OpenOfficeConnectionException
                | IOException e) {
            throw new DomainException("error.documentRequest.errorGeneratingDocument", e);
        }
    }

    @Override
    public String getReportFileName() {
        try {
            switch (getDocumentRequestType()) {
            case ENROLMENT_DECLARATION:
                return new EnrollmentDeclarationODTDocument(
                        "/odtreports/academicOffice/enrollment/declaration/DeclaracaoInscricao"
                                + getLanguage().name().toUpperCase() + ".odt", (EnrolmentDeclarationRequest) this)
                        .getReportFileName();
            case ENROLMENT_CERTIFICATE:
                return new EnrollmentCertificateODTDocument("/odtreports/academicOffice/enrollment/certificate/CertidaoInscricao"
                        + getLanguage().name().toUpperCase() + ".odt", (EnrolmentCertificateRequest) this).getReportFileName();
            case SCHOOL_REGISTRATION_CERTIFICATE:
                return new RegistrationCertificateODTDocument(
                        "/odtreports/academicOffice/registration/certificate/CertidaoMatricula"
                                + getLanguage().name().toUpperCase() + ".odt", (CertificateRequest) this).getReportFileName();
            case SCHOOL_REGISTRATION_DECLARATION:
                return new RegistrationDeclarationODTDocument(
                        "/odtreports/academicOffice/registration/declaration/DeclaracaoMatricula"
                                + getLanguage().name().toUpperCase() + ".odt", this).getReportFileName();
            default:
                return AdministrativeOfficeDocument.AdministrativeOfficeDocumentCreator.create(this).iterator().next()
                        .getReportFileName();
            }
        } catch (SecurityException | IOException e) {
            throw new DomainException("error.documentRequest.errorGeneratingDocument", e);
        }
    }

    @Deprecated
    public java.util.Set<net.sourceforge.fenixedu.domain.administrativeOffice.curriculumValidation.DocumentPrintRequest> getRequest() {
        return getRequestSet();
    }

    @Deprecated
    public boolean hasAnyRequest() {
        return !getRequestSet().isEmpty();
    }

}
