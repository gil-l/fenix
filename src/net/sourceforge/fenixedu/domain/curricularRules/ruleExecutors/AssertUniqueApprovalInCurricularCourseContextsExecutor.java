package net.sourceforge.fenixedu.domain.curricularRules.ruleExecutors;

import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.ExecutionPeriod;
import net.sourceforge.fenixedu.domain.curricularRules.ICurricularRule;
import net.sourceforge.fenixedu.domain.enrolment.EnrolmentContext;
import net.sourceforge.fenixedu.domain.enrolment.IDegreeModuleToEvaluate;

public class AssertUniqueApprovalInCurricularCourseContextsExecutor extends CurricularRuleExecutor {

    @Override
    protected RuleResult executeEnrolmentWithRules(final ICurricularRule curricularRule, final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {
	final CurricularCourse curricularCourse = (CurricularCourse) curricularRule.getDegreeModuleToApplyRule();
	final ExecutionPeriod executionPeriod = enrolmentContext.getExecutionPeriod();
	
	if (!curricularCourse.hasAnyActiveContext(enrolmentContext.getExecutionPeriod())) {
	    return RuleResult.createNA();
	}
	
	if (isApproved(enrolmentContext, curricularCourse, executionPeriod.getPreviousExecutionPeriod())) {
	    return RuleResult.createTrue(EnrolmentResultType.IMPOSSIBLE); 
	} else {
	    return RuleResult.createTrue();
	}
    }

    @Override
    protected RuleResult executeEnrolmentWithRulesAndTemporaryEnrolment(final ICurricularRule curricularRule, final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {
	final CurricularCourse curricularCourse = (CurricularCourse) curricularRule.getDegreeModuleToApplyRule();
	final ExecutionPeriod executionPeriod = enrolmentContext.getExecutionPeriod();

	if (!curricularCourse.hasAnyActiveContext(enrolmentContext.getExecutionPeriod())) {
	    return RuleResult.createNA();
	}
	
	if (isApproved(enrolmentContext, curricularCourse, executionPeriod.getPreviousExecutionPeriod())) {
	    return RuleResult.createTrue(EnrolmentResultType.IMPOSSIBLE); 
	    
	} else if (hasEnrolmentWithEnroledState(enrolmentContext, curricularCourse, executionPeriod.getPreviousExecutionPeriod())) {
	    return RuleResult.createTrue(EnrolmentResultType.TEMPORARY);
	    
	} else {
	    return RuleResult.createTrue();
	}
    }

    @Override
    protected RuleResult executeEnrolmentInEnrolmentEvaluation(final ICurricularRule curricularRule, final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {
	return RuleResult.createNA();
    }

}
