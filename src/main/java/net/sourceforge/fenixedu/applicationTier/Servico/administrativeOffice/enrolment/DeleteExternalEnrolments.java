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
package net.sourceforge.fenixedu.applicationTier.Servico.administrativeOffice.enrolment;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.domain.studentCurriculum.ExternalEnrolment;
import pt.ist.fenixframework.Atomic;

public class DeleteExternalEnrolments {

    @Atomic
    public static void run(final Registration registration, String[] externalEnrolmentIDs) throws FenixServiceException {
        for (final String externalEnrolmentID : externalEnrolmentIDs) {
            final ExternalEnrolment externalEnrolment = getExternalEnrolmentByID(registration, externalEnrolmentID);
            if (externalEnrolment == null) {
                throw new FenixServiceException("error.DeleteExternalEnrolments.externalEnrolmentID.doesnot.belong.to.student");
            }
            externalEnrolment.delete();
        }
    }

    private static ExternalEnrolment getExternalEnrolmentByID(final Registration registration, final String externalEnrolmentID) {
        for (final ExternalEnrolment externalEnrolment : registration.getExternalEnrolmentsSet()) {
            if (externalEnrolment.getExternalId().equals(externalEnrolmentID)) {
                return externalEnrolment;
            }
        }
        return null;
    }
}