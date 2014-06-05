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
package net.sourceforge.fenixedu.applicationTier.Servico.commons.searchers;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.organizationalStructure.Party;
import net.sourceforge.fenixedu.domain.organizationalStructure.UnitName;

import org.joda.time.YearMonthDay;

public class SearchAllActiveParties extends SearchParties<Party> {

    @Override
    protected Collection<Party> search(String value, int size) {
        ArrayList<Party> result = new ArrayList<Party>();

        if (value.length() > 3 && value.substring(0, 3).equals("ist")) {
            Person person = Person.readPersonByUsername(value);
            if (person != null) {
                result.add(person);
            }
        } else {
            result.addAll(Person.findPerson(value, size));
        }

        YearMonthDay currentDate = new YearMonthDay();
        for (UnitName unitName : UnitName.find(value, size)) {
            if (unitName.getUnit().isActive(currentDate)) {
                result.add(unitName.getUnit());
            }
        }

        return result;
    }

}
