package net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice;

import java.io.IOException;
import java.math.BigDecimal;

import net.sourceforge.fenixedu.domain.accounting.postingRules.serviceRequests.CertificateRequestPR;
import net.sourceforge.fenixedu.domain.accounting.serviceAgreementTemplates.AdministrativeOfficeServiceAgreementTemplate;
import net.sourceforge.fenixedu.domain.serviceRequests.AcademicServiceRequestSituationType;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.CertificateRequest;
import net.sourceforge.fenixedu.util.Money;

public class CertificateODTDocument extends AdministrativeOfficeODTDocument {

    public CertificateODTDocument(String templatePath, CertificateRequest documentRequest) throws SecurityException, IOException {
        super(templatePath, documentRequest);
        setUp(documentRequest);
    }

    private void setUp(CertificateRequest documentRequest) {
        boolean hasPrice =
                documentRequest.getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.PROCESSING
                        && !documentRequest.isFree() || documentRequest.hasEvent();
        addParameter("hasPrice", hasPrice);
        if (hasPrice) {
            int pageCount = this.getInstancePageCount();
            AdministrativeOfficeServiceAgreementTemplate serviceAgreementTemplate =
                    documentRequest.getAdministrativeOffice().getServiceAgreementTemplate();
            final CertificateRequestPR certificateRequestPR =
                    (CertificateRequestPR) serviceAgreementTemplate.findPostingRuleByEventType(documentRequest.getEventType());
            final Money issuePrice =
                    certificateRequestPR.getBaseAmount().add(
                            certificateRequestPR.getAmountPerUnit().multiply(
                                    BigDecimal.valueOf(documentRequest.getNumberOfUnits())));
            Money urgencyPrice = documentRequest.getUrgentRequest() ? certificateRequestPR.getBaseAmount() : Money.ZERO;
            Money printPrice = certificateRequestPR.getAmountPerPage().multiply(new BigDecimal(pageCount));

            addParameter("printPrice", printPrice);
            addParameter("issuePrice", issuePrice);
            addParameter("urgencyPrice", urgencyPrice);
            addParameter("totalPrice", urgencyPrice.add(printPrice).add(issuePrice));
        }
    }

}