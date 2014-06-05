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
package net.sourceforge.fenixedu.domain.accounting.events.serviceRequests;

import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.accounting.EntryType;
import net.sourceforge.fenixedu.domain.accounting.EventType;
import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.phd.serviceRequests.documentRequests.PhdDiplomaRequest;
import net.sourceforge.fenixedu.domain.serviceRequests.AcademicServiceRequest;
import pt.utl.ist.fenix.tools.resources.LabelFormatter;

public class PhdDiplomaRequestEvent extends PhdDiplomaRequestEvent_Base {

    protected PhdDiplomaRequestEvent() {
        super();
    }

    protected PhdDiplomaRequestEvent(final AdministrativeOffice administrativeOffice, final EventType eventType,
            final Person person, final AcademicServiceRequest academicServiceRequest) {
        this();

        if (!academicServiceRequest.isDiploma()) {
            throw new DomainException("error.PhdRegistryDiplomaRequestEvent.request.is.not.diploma");
        }

        if (!EventType.BOLONHA_PHD_DIPLOMA_REQUEST.equals(eventType)) {
            throw new DomainException("error.PhdRegistryDiplomaRequestEvent.eventType.is.not.phd.diploma");
        }

        super.init(administrativeOffice, eventType, person, academicServiceRequest);
    }

    final static public PhdDiplomaRequestEvent create(final AdministrativeOffice administrativeOffice, final Person person,
            final PhdDiplomaRequest phdDiplomaRequest) {
        return new PhdDiplomaRequestEvent(administrativeOffice, phdDiplomaRequest.getEventType(), person, phdDiplomaRequest);
    }

    @Override
    public LabelFormatter getDescription() {
        final LabelFormatter result = super.getDescription();
        fillDescription(result);
        return result;
    }

    @Override
    final public LabelFormatter getDescriptionForEntryType(final EntryType entryType) {
        final LabelFormatter labelFormatter = new LabelFormatter();
        labelFormatter.appendLabel(entryType.name(), LabelFormatter.ENUMERATION_RESOURCES);
        fillDescription(labelFormatter);

        return labelFormatter;
    }

    private void fillDescription(final LabelFormatter labelFormatter) {
        labelFormatter.appendLabel(" (");
        final PhdDiplomaRequest request = (PhdDiplomaRequest) getAcademicServiceRequest();
        labelFormatter.appendLabel(request.getPhdIndividualProgramProcess().getPhdProgram().getName().getPreferedContent());
        labelFormatter.appendLabel(")");
    }

}
