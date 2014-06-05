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
/**
 * 
 */
package net.sourceforge.fenixedu.presentationTier.renderers.providers.candidacy;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.fenixedu.domain.candidacy.CandidacySituationType;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.EnumConverter;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class SituationTypesForCandidacySelection implements DataProvider {

    @Override
    public Object provide(Object source, Object currentValue) {

        Collection<CandidacySituationType> result = new ArrayList<CandidacySituationType>();
        result.add(CandidacySituationType.ADMITTED);
        result.add(CandidacySituationType.SUBSTITUTE);
        result.add(CandidacySituationType.NOT_ADMITTED);

        return result;
    }

    @Override
    public Converter getConverter() {
        return new EnumConverter();
    }

}
