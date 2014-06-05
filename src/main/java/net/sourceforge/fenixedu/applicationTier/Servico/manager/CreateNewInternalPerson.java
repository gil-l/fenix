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
package net.sourceforge.fenixedu.applicationTier.Servico.manager;

import java.util.Set;

import net.sourceforge.fenixedu.dataTransferObject.person.InternalPersonBean;
import net.sourceforge.fenixedu.domain.Employee;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.person.RoleType;
import pt.ist.fenixframework.Atomic;

public class CreateNewInternalPerson {

    @Atomic
    public static Person run(final InternalPersonBean bean) {
        final Person person = new Person(bean, false, true); // validate
        // physical address
        // only
        final Set<RoleType> roleTypes = bean.getRelationTypes();
        attributeRoles(person, roleTypes);
        return person;
    }

    @Atomic
    public static void attributeRoles(final Person person, final Set<RoleType> roleTypes) {
        if (roleTypes.isEmpty()) {
            throw new DomainException("error.create.internal.person.relation.type.none");
        }

        if (roleTypes.contains(RoleType.EMPLOYEE) || roleTypes.contains(RoleType.RESEARCHER)
                || roleTypes.contains(RoleType.TEACHER) || roleTypes.contains(RoleType.GRANT_OWNER)) {
            createEmployee(person);
        }
    }

    private static void createEmployee(final Person person) {
        if (!person.hasEmployee()) {
            final Integer number = Employee.getNextEmployeeNumber();
            new Employee(person, number);
        }
    }

}
