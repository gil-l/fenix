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
package net.sourceforge.fenixedu.applicationTier.Filtro;

/**
 * This class is responsible for verifying if a given user has the authorization
 * to run a service with certain attributes.
 * 
 * @author Nuno Nunes & Joana Mota
 * @version
 */

import net.sourceforge.fenixedu.domain.person.RoleType;

public class CoordinatorAuthorizationFilter extends AuthorizationByRoleFilter {

    // the singleton of this class
    public final static CoordinatorAuthorizationFilter instance = new CoordinatorAuthorizationFilter();

    /**
     * The singleton access method of this class.
     * 
     * @return Returns the instance of this class responsible for the
     *         authorization access to services.
     */
    public static Filtro getInstance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ServidorAplicacao.Filtro.AuthorizationByRoleFilter#getRoleType()
     */
    @Override
    protected RoleType getRoleType() {
        return RoleType.COORDINATOR;
    }
}