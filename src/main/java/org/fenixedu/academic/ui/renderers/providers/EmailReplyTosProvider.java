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

import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.academic.util.MessagingUtils;
import org.fenixedu.messaging.domain.ReplyTo;
import org.fenixedu.messaging.domain.Sender;
import org.fenixedu.messaging.ui.EmailBean;

import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class EmailReplyTosProvider implements DataProvider {

    @Override
    public Object provide(final Object source, final Object currentValue) {
        final EmailBean emailBean = (EmailBean) source;
        final Sender sender = emailBean.getSender();
        final Set<ReplyTo> replyTos = new TreeSet<ReplyTo>(MessagingUtils.REPLY_TO_COMPARATOR_BY_ADDRESS);
        if (sender != null) {
            replyTos.addAll(sender.getReplyTos());
        }
        return replyTos;
    }

    @Override
    public Converter getConverter() {
        return null;
    }

}
