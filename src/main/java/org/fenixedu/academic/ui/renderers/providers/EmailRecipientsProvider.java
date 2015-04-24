/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.ui.renderers.providers;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.util.email.EmailBean;
import org.fenixedu.academic.domain.util.email.Sender;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;

import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class EmailRecipientsProvider implements DataProvider {

    @Override
    public Object provide(final Object source, final Object currentValue) {
        final EmailBean emailBean = (EmailBean) source;
        final Sender sender = emailBean.getSender();
        final Set<PersistentGroup> recipients = new TreeSet<PersistentGroup>(new Comparator<PersistentGroup>() {
            @Override
            public int compare(PersistentGroup r1, PersistentGroup r2) {
                final int c = r1.getPresentationName().compareTo(r2.getPresentationName());
                return c == 0 ? DomainObjectUtil.COMPARATOR_BY_ID.compare(r1, r2) : c;
            }
        });
        recipients.addAll(emailBean.getRecipients());
        if (sender != null) {
            recipients.addAll(sender.getRecipientsSet());
        }
        return recipients;
    }

    @Override
    public Converter getConverter() {
        return null;
    }

}
