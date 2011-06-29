package net.sourceforge.fenixedu.domain.phd.individualProcess.activities;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.domain.caseHandling.PreConditionNotValidException;
import net.sourceforge.fenixedu.domain.caseHandling.Process;
import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramProcess;
import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramProcessState;
import net.sourceforge.fenixedu.domain.phd.alert.PhdAlert;
import net.sourceforge.fenixedu.domain.phd.alert.PhdPublicPresentationSeminarAlert;
import net.sourceforge.fenixedu.domain.phd.seminar.PublicPresentationSeminarProcess;
import net.sourceforge.fenixedu.domain.phd.seminar.PublicPresentationSeminarProcessStateType;

public class ExemptPublicPresentationSeminarComission extends PhdIndividualProgramProcessActivity {

    @Override
    protected void activityPreConditions(PhdIndividualProgramProcess process, IUserView userView) {
	if (process.hasSeminarProcess() || process.getActiveState() != PhdIndividualProgramProcessState.WORK_DEVELOPMENT) {
	    throw new PreConditionNotValidException();
	}
    }

    @Override
    protected PhdIndividualProgramProcess executeActivity(PhdIndividualProgramProcess process, IUserView userView, Object object) {

	final PublicPresentationSeminarProcess seminarProcess = Process.createNewProcess(userView,
		PublicPresentationSeminarProcess.class, object);

	seminarProcess.setIndividualProgramProcess(process);
	seminarProcess.createState(PublicPresentationSeminarProcessStateType.EXEMPTED, userView.getPerson(), null);

	discardPublicSeminarAlerts(process);

	return process;
    }

    private void discardPublicSeminarAlerts(final PhdIndividualProgramProcess process) {
	for (final PhdAlert alert : process.getActiveAlerts()) {
	    if (alert instanceof PhdPublicPresentationSeminarAlert) {
		alert.discard();
	    }
	}
    }

}