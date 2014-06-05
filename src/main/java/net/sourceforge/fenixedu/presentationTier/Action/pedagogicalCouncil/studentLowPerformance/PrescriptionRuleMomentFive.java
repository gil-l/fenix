/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.fenixedu.presentationTier.Action.pedagogicalCouncil.studentLowPerformance;

import java.math.BigDecimal;

import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.PrescriptionEnum;

class PrescriptionRuleMomentFive extends PrescriptionRuleGenericMoment {

    public PrescriptionRuleMomentFive() {
        super();
    }

    @Override
    public BigDecimal getMinimumEcts() {
        return new BigDecimal(55);
    }

    @Override
    public PrescriptionEnum getPrescriptionEnum() {
        return PrescriptionEnum.MOMENT5;
    }

    @Override
    public int getNumberOfEntriesStudentInSecretary() {
        return 3;
    }

    @Override
    public ExecutionYear getRegistrationStart(ExecutionYear executionYear) {
        return executionYear.getPreviousExecutionYear().getPreviousExecutionYear();
    }

}
