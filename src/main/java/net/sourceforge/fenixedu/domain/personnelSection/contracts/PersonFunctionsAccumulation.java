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
package net.sourceforge.fenixedu.domain.personnelSection.contracts;

import java.math.BigDecimal;

import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class PersonFunctionsAccumulation extends PersonFunctionsAccumulation_Base {

    public PersonFunctionsAccumulation(final GiafProfessionalData giafProfessionalData, final LocalDate beginDate,
            final LocalDate endDate, final BigDecimal hours, final FunctionsAccumulation functionsAccumulation,
            final String functionsAccumulationGiafId, final ProfessionalRegime professionalRegime,
            final String professionalRegimeGiafId, final DateTime creationDate, final DateTime modifiedDate) {
        super();
        setRootDomainObject(Bennu.getInstance());
        setGiafProfessionalData(giafProfessionalData);
        setBeginDate(beginDate);
        setEndDate(endDate);
        setHours(hours);
        setFunctionsAccumulation(functionsAccumulation);
        setFunctionsAccumulationGiafId(functionsAccumulationGiafId);
        setProfessionalRegime(professionalRegime);
        setProfessionalRegimeGiafId(professionalRegimeGiafId);
        setCreationDate(creationDate);
        setModifiedDate(modifiedDate);
        setImportationDate(new DateTime());
    }

    public boolean isValid() {
        return getFunctionsAccumulation() != null && getBeginDate() != null
                && (getEndDate() == null || !getBeginDate().isAfter(getEndDate())) && getProfessionalRegime() != null;
    }

    @Deprecated
    public boolean hasProfessionalRegimeGiafId() {
        return getProfessionalRegimeGiafId() != null;
    }

    @Deprecated
    public boolean hasBennu() {
        return getRootDomainObject() != null;
    }

    @Deprecated
    public boolean hasBeginDate() {
        return getBeginDate() != null;
    }

    @Deprecated
    public boolean hasAnulationDate() {
        return getAnulationDate() != null;
    }

    @Deprecated
    public boolean hasHours() {
        return getHours() != null;
    }

    @Deprecated
    public boolean hasFunctionsAccumulation() {
        return getFunctionsAccumulation() != null;
    }

    @Deprecated
    public boolean hasCreationDate() {
        return getCreationDate() != null;
    }

    @Deprecated
    public boolean hasGiafProfessionalData() {
        return getGiafProfessionalData() != null;
    }

    @Deprecated
    public boolean hasModifiedDate() {
        return getModifiedDate() != null;
    }

    @Deprecated
    public boolean hasEndDate() {
        return getEndDate() != null;
    }

    @Deprecated
    public boolean hasProfessionalRegime() {
        return getProfessionalRegime() != null;
    }

    @Deprecated
    public boolean hasImportationDate() {
        return getImportationDate() != null;
    }

    @Deprecated
    public boolean hasFunctionsAccumulationGiafId() {
        return getFunctionsAccumulationGiafId() != null;
    }

}
