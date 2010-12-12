package net.sourceforge.fenixedu.presentationTier.Action.rectorate.batches;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.domain.documents.DocumentRequestGeneratedDocument;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.serviceRequests.RectorateSubmissionBatch;
import net.sourceforge.fenixedu.domain.serviceRequests.RectorateSubmissionState;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DiplomaSupplementRequest;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequest;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.RegistryDiplomaRequest;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;
import net.sourceforge.fenixedu.presentationTier.Action.commons.excel.ExcelUtils;
import net.sourceforge.fenixedu.presentationTier.Action.commons.zip.ZipUtils;
import net.sourceforge.fenixedu.presentationTier.docs.academicAdministrativeOffice.AdministrativeOfficeDocument;
import net.sourceforge.fenixedu.util.report.ReportsUtils;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/rectorateIncomingBatches", module = "rectorate")
@Forwards({ @Forward(name = "index", path = "/rectorate/incomingBatches.jsp"),
	@Forward(name = "printDocument", path = "/academicAdminOffice/serviceRequests/documentRequests/printDocument.jsp"),
	@Forward(name = "viewBatch", path = "/academicAdminOffice/rectorateDocumentSubmission/showBatch.jsp") })
public class RectorateIncomingBatchesDispatchAction extends FenixDispatchAction {
    
    public ActionForward index(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) {
	List<AdministrativeOffice> offices = RootDomainObject.getInstance().getAdministrativeOffices();
	Set<RectorateSubmissionBatch> batches = new HashSet<RectorateSubmissionBatch>();

	for (AdministrativeOffice office : offices) {
	    for (RectorateSubmissionBatch batch : office.getRectorateSubmissionBatchesByState(RectorateSubmissionState.SENT)) {
		if (hasDiplomaRequests(batch)) {
		    batches.add(batch);
		}
	    }
	}

	request.setAttribute("sent", batches);
	return mapping.findForward("index");
    }

    private boolean obeysToPresentationRestriction(DocumentRequest docRequest) {
	return !docRequest.isCancelled() && !docRequest.isRejected() && (docRequest instanceof RegistryDiplomaRequest)
		|| (docRequest instanceof DiplomaSupplementRequest);
    }

    private boolean hasDiplomaRequests(RectorateSubmissionBatch batch) {
	for (DocumentRequest docRequest : batch.getDocumentRequestSet()) {
	    if (obeysToPresentationRestriction(docRequest)) {
		return true;
	    }
	}

	return false;
    }

    private Set<DocumentRequest> getRelevantDocuments(Set<DocumentRequest> documentRequestSet) {
	Set<DocumentRequest> requests = new HashSet<DocumentRequest>();
	for (DocumentRequest docRequest : documentRequestSet) {
	    if (obeysToPresentationRestriction(docRequest)) {
		requests.add(docRequest);
	    }
	}
	return requests;
    }

    public ActionForward viewBatch(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) {
	RectorateSubmissionBatch batch = getDomainObject(request, "batchOid");
	Set<String> actions = new HashSet<String>();
	Set<String> confirmActions = new HashSet<String>();

	actions.add("generateMetadataForRegistry");
	actions.add("zipDocuments");

	request.setAttribute("batch", batch);

	Set<DocumentRequest> requests = getRelevantDocuments(batch.getDocumentRequestSet());

	request.setAttribute("requests", requests);
	request.setAttribute("actions", actions);
	request.setAttribute("confirmActions", confirmActions);
	return mapping.findForward("viewBatch");
    }

    public ActionForward zipDocuments(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) {

	RectorateSubmissionBatch batch = getDomainObject(request, "batchOid");
	Set<DocumentRequest> requestsToZip = getRelevantDocuments(batch.getDocumentRequestSet());

	if (!requestsToZip.isEmpty()) {
	    ZipUtils zipUtils = new ZipUtils();
	    zipUtils.createAndFlushArchive(requestsToZip, response, batch);
	    return null;
	} else {
	    addActionMessage(request, "error.rectorateSubmission.noDocumentsToZip");
	    request.setAttribute("batchOid", batch.getExternalId());
	    return viewBatch(mapping, actionForm, request, response);
	}
    }

    public ActionForward generateMetadataForRegistry(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) {
	RectorateSubmissionBatch batch = getDomainObject(request, "batchOid");
	Set<DocumentRequest> docs = getRelevantDocuments(batch.getDocumentRequestSet());
	ExcelUtils excelUtils = new ExcelUtils(request, response);
	excelUtils.generateSortedExcel(docs, "registos-");
	return null;
    }

    protected DocumentRequest getDocumentRequest(HttpServletRequest request) {
	return (DocumentRequest) rootDomainObject.readAcademicServiceRequestByOID(getRequestParameterAsInteger(request,
		"documentRequestId"));
    }

    public ActionForward printDocument(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws JRException, IOException, FenixFilterException, FenixServiceException {
	final DocumentRequest documentRequest = getDocumentRequest(request);
	try {
	    final List<AdministrativeOfficeDocument> documents = (List<AdministrativeOfficeDocument>) AdministrativeOfficeDocument.AdministrativeOfficeDocumentCreator
		    .create(documentRequest);
	    final AdministrativeOfficeDocument[] array = {};
	    byte[] data = ReportsUtils.exportMultipleToPdfAsByteArray(documents.toArray(array));
	    DocumentRequestGeneratedDocument.store(documentRequest, documents.iterator().next().getReportFileName() + ".pdf",
		    data);
	    response.setContentLength(data.length);
	    response.setContentType("application/pdf");
	    response.addHeader("Content-Disposition", "attachment; filename=" + documents.iterator().next().getReportFileName()
		    + ".pdf");

	    final ServletOutputStream writer = response.getOutputStream();
	    writer.write(data);
	    writer.flush();
	    writer.close();

	    response.flushBuffer();
	    return mapping.findForward("");
	} catch (DomainException e) {
	    addActionMessage(request, e.getKey());
	    request.setAttribute("registration", documentRequest.getRegistration());
	    return mapping.findForward("viewRegistrationDetails");
	}
    }
}