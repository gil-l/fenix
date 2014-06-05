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
package net.sourceforge.fenixedu.dataTransferObject;

import org.fenixedu.spaces.domain.Space;

/**
 * @author Luis Cruz
 * 
 * 
 */
public class InfoBuilding extends InfoObject {

    private String name;

    public void copyFromDomain(Space building) {
        super.copyFromDomain(building);
        if (building != null) {
            setName(building.getName());
        }
    }

    public static InfoBuilding newInfoFromDomain(Space building) {
        InfoBuilding infoBuilding = null;
        if (building != null) {
            infoBuilding = new InfoBuilding();
            infoBuilding.copyFromDomain(building);
        }

        return infoBuilding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}