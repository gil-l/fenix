package net.sourceforge.fenixedu.domain.phd.thesis.meeting;

import java.util.Collections;
import java.util.Set;

import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.accessControl.CurrentDegreeCoordinatorsGroup;
import net.sourceforge.fenixedu.domain.accessControl.Group;
import net.sourceforge.fenixedu.domain.accessControl.GroupUnion;
import net.sourceforge.fenixedu.domain.accessControl.RoleGroup;
import net.sourceforge.fenixedu.domain.person.RoleType;
import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramDocumentType;
import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramProcess;
import net.sourceforge.fenixedu.domain.phd.PhdProgram;
import net.sourceforge.fenixedu.domain.phd.PhdProgramProcess;

public class PhdMeetingMinutesDocument extends PhdMeetingMinutesDocument_Base {
    
    public  PhdMeetingMinutesDocument() {
        super();
    }
    
    public PhdMeetingMinutesDocument(PhdMeeting meeting, PhdIndividualProgramDocumentType documentType, String remarks,
	    byte[] content, String filename, Person uploader) {
	this();
	init(meeting, documentType, remarks, content, filename, uploader);

    }

    @SuppressWarnings("unchecked")
    protected void init(PhdMeeting meeting, PhdIndividualProgramDocumentType documentType, String remarks, byte[] content,
	    String filename, Person uploader) {

	checkParameters(meeting.getMeetingProcess(), documentType, content, filename, uploader);

	setDocumentVersion(meeting, documentType);

	setPhdMeeting(meeting);
	super.setDocumentType(documentType);
	super.setRemarks(remarks);
	super.setUploader(uploader);
	super.setDocumentAccepted(true);

	final Group roleGroup = new RoleGroup(RoleType.ACADEMIC_ADMINISTRATIVE_OFFICE);

	final PhdIndividualProgramProcess individualProgramProcess = meeting.getMeetingProcess().getThesisProcess()
		.getIndividualProgramProcess();
	final PhdProgram phdProgram = individualProgramProcess.getPhdProgram();
	final Group coordinatorGroup = new CurrentDegreeCoordinatorsGroup(phdProgram.getDegree());

	final Group group = new GroupUnion(roleGroup, coordinatorGroup);
	super.init(getVirtualPath(), filename, filename, Collections.EMPTY_SET, content, group);
	storeToContentManager();
    }

    protected void setDocumentVersion(PhdMeeting meeting, PhdIndividualProgramDocumentType documentType) {
	if (documentType.isVersioned()) {
	    final Set<PhdMeetingMinutesDocument> documents = meeting.getDocumentsSet();
	    super.setDocumentVersion(documents.isEmpty() ? 1 : documents.size() + 1);
	} else {
	    super.setDocumentVersion(1);
	}
    }

    @Override
    public PhdProgramProcess getPhdProgramProcess() {
	return getPhdMeeting().getMeetingProcess().getThesisProcess().getIndividualProgramProcess();
    }

}