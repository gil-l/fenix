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
package net.sourceforge.fenixedu.domain.accounting.serviceAgreementTemplates;

import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;

public class AdministrativeOfficeServiceAgreementTemplate extends AdministrativeOfficeServiceAgreementTemplate_Base {

    private AdministrativeOfficeServiceAgreementTemplate() {
        super();
    }

    public AdministrativeOfficeServiceAgreementTemplate(AdministrativeOffice administrativeOffice) {
        this();
        init(administrativeOffice);
    }

    private void checkParameters(AdministrativeOffice administrativeOffice) {
        if (administrativeOffice == null) {
            throw new DomainException(
                    "error.accounting.serviceAgreementTemplates.AdministrativeOfficeServiceAgreementTemplate.administrativeOffice.cannot.be.null");
        }

    }

    protected void init(AdministrativeOffice administrativeOffice) {
        checkParameters(administrativeOffice);
        addAdministrativeOffice(administrativeOffice);
    }

    @Deprecated
    public java.util.Set<net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice> getAdministrativeOffice() {
        return getAdministrativeOfficeSet();
    }

    @Deprecated
    public boolean hasAnyAdministrativeOffice() {
        return !getAdministrativeOfficeSet().isEmpty();
    }

}
