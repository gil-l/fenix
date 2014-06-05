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
/*
 * Created on 29/Fev/2004
 */
package net.sourceforge.fenixedu.domain.credits;

import net.sourceforge.fenixedu.domain.credits.event.CreditsEvent;

import org.fenixedu.bennu.core.domain.Bennu;

/**
 * @author jpvl
 */
public abstract class CreditLine extends CreditLine_Base implements
        net.sourceforge.fenixedu.domain.credits.event.ICreditsEventOriginator {

    public CreditLine() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    protected abstract CreditsEvent getCreditEventGenerated();

    @Deprecated
    public boolean hasBennu() {
        return getRootDomainObject() != null;
    }

}
