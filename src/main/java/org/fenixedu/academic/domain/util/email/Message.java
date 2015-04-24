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
package org.fenixedu.academic.domain.util.email;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.util.Email;
import org.fenixedu.academic.domain.util.EmailAddressList;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class Message extends Message_Base {

    static final public Comparator<Message> COMPARATOR_BY_CREATED_DATE_OLDER_FIRST = new Comparator<Message>() {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getCreated().compareTo(o2.getCreated());
        }
    };

    static final public Comparator<Message> COMPARATOR_BY_CREATED_DATE_OLDER_LAST = new Comparator<Message>() {
        @Override
        public int compare(Message o1, Message o2) {
            return o2.getCreated().compareTo(o1.getCreated());
        }
    };

    public static final int NUMBER_OF_SENT_EMAILS_TO_STAY = 500;

    public Message() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public Message(final Sender sender, String to, String subject, String body) {
        this(sender, sender.getReplyTosSet(), null, subject, body, to);
    }

    public Message(final Sender sender, final Collection<? extends ReplyTo> replyTos, final Collection<Group> tos,
            final Collection<Group> ccs, final Collection<Group> recipientsBccs, final String subject, final String body,
            final Set<String> bccs) {
        this(sender, replyTos, recipientsBccs, subject, body, bccs);
        if (tos != null) {
            for (final Group recipient : tos) {
                addTos(recipient.toPersistentGroup());
            }
        }
        if (ccs != null) {
            for (final Group recipient : ccs) {
                addCcs(recipient.toPersistentGroup());
            }
        }
    }

    public Message(final Sender sender, final Collection<? extends ReplyTo> replyTos, final Collection<Group> tos,
            final Collection<Group> ccs, final Collection<Group> recipientsBccs, final String subject, final String body,
            final Set<String> bccs, final String htmlBody) {
        this(sender, replyTos, recipientsBccs, subject, body, bccs);
        if (tos != null) {
            for (final Group recipient : tos) {
                addTos(recipient.toPersistentGroup());
            }
        }
        if (ccs != null) {
            for (final Group recipient : ccs) {
                addCcs(recipient.toPersistentGroup());
            }
        }
    }

    public Message(final Sender sender, final Group recipient, final String subject, final String body) {
        this(sender, sender.getConcreteReplyTos(), Collections.singleton(recipient), subject, body, new EmailAddressList(
                Collections.EMPTY_LIST).toString());
    }

    public Message(final Sender sender, final Collection<? extends ReplyTo> replyTos, final Collection<Group> recipients,
            final String subject, final String body, final Set<String> bccs) {
        this(sender, replyTos, recipients, subject, body, new EmailAddressList(bccs).toString());
    }

    public Message(final Sender sender, final Collection<? extends ReplyTo> replyTos, final Collection<Group> recipients,
            final String subject, final String body, final String bccs) {
        super();
        final Bennu rootDomainObject = Bennu.getInstance();
        setRootDomainObject(rootDomainObject);
        setRootDomainObjectFromPendingRelation(rootDomainObject);
        setSender(sender);
        if (replyTos != null) {
            for (final ReplyTo replyTo : replyTos) {
                addReplyTos(replyTo);
            }
        }
        if (recipients != null) {
            for (final Group recipient : recipients) {
                addRecipients(recipient.toPersistentGroup());
            }
        }
        setSubject(subject);
        setBody(body);
        setBccs(bccs);
        final Person person = AccessControl.getPerson();
        setPerson(person);
        setCreated(new DateTime());
    }

    public Message(final Sender sender, final Collection<? extends ReplyTo> replyTos, final Collection<Group> recipients,
            final String subject, final String body, final String bccs, final String htmlBody) {
        super();
        final Bennu rootDomainObject = Bennu.getInstance();
        setRootDomainObject(rootDomainObject);
        setRootDomainObjectFromPendingRelation(rootDomainObject);
        setSender(sender);
        if (replyTos != null) {
            for (final ReplyTo replyTo : replyTos) {
                addReplyTos(replyTo);
            }
        }
        if (recipients != null) {
            for (final Group recipient : recipients) {
                addRecipients(recipient.toPersistentGroup());
            }
        }
        setSubject(subject);
        setBody(body);
        setHtmlBody(htmlBody);
        setBccs(bccs);
        final Person person = AccessControl.getPerson();
        setPerson(person);
        setCreated(new DateTime());
    }

    public void safeDelete() {
        if (getSent() == null) {
            delete();
        }
    }

    public void delete() {
        for (final PersistentGroup recipient : getRecipientsSet()) {
            removeRecipients(recipient);
        }
        for (final PersistentGroup recipient : getTosSet()) {
            removeTos(recipient);
        }
        for (final PersistentGroup recipient : getCcsSet()) {
            removeCcs(recipient);
        }
        for (final ReplyTo replyTo : getReplyTosSet()) {
            replyTo.safeDelete();
        }
        for (final Email email : getEmailsSet()) {
            email.delete();
        }

        setSender(null);
        setPerson(null);
        setRootDomainObjectFromPendingRelation(null);
        setRootDomainObject(null);
        deleteDomainObject();
    }

    public String getRecipientsAsText() {
        final StringBuilder stringBuilder = new StringBuilder();
        recipients2Text(stringBuilder, getRecipientsSet());
        if (getBccs() != null && !getBccs().isEmpty()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(getBccs());
        }
        return stringBuilder.toString();
    }

    public String getRecipientsAsToText() {
        return recipients2Text(getTosSet());
    }

    public String getRecipientsAsCcText() {
        return recipients2Text(getCcsSet());
    }

    protected static String recipients2Text(final Set<PersistentGroup> recipients) {
        final StringBuilder stringBuilder = new StringBuilder();
        recipients2Text(stringBuilder, recipients);
        return stringBuilder.toString();
    }

    protected static void recipients2Text(final StringBuilder stringBuilder, final Set<PersistentGroup> recipients) {
        for (final PersistentGroup recipient : recipients) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(recipient.getPresentationName());
        }
    }

    public String getRecipientsGroupMembersInText() {
        StringBuilder builder = new StringBuilder();

        Collection<PersistentGroup> recipients = getRecipientsSet();
        for (PersistentGroup recipient : recipients) {
            builder.append(getMembersEmailInText(recipient));
        }

        return builder.toString();
    }

    protected static Set<String> getRecipientAddresses(Set<PersistentGroup> recipients) {
        final Set<String> emailAddresses = new HashSet<String>();
        for (final PersistentGroup recipient : recipients) {
            addDestinationEmailAddresses(recipient, emailAddresses);
        }
        return emailAddresses;
    }

    protected Set<String> getDestinationBccs() {
        final Set<String> emailAddresses = new HashSet<String>();
        if (getBccs() != null && !getBccs().isEmpty()) {
            for (final String emailAddress : getBccs().replace(',', ' ').replace(';', ' ').split(" ")) {
                final String trimmed = emailAddress.trim();
                if (!trimmed.isEmpty()) {
                    emailAddresses.add(emailAddress);
                }
            }
        }
        for (final PersistentGroup recipient : getRecipientsSet()) {
            addDestinationEmailAddresses(recipient, emailAddresses);
        }
        return emailAddresses;
    }

    private static String getMembersEmailInText(PersistentGroup recipient) {
        if (recipient == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        Set<User> elements = recipient.getMembers();

        for (User user : elements) {
            builder.append(user.getPerson().getName()).append(" (").append(user.getPerson().getEmailForSendingEmails())
                    .append(")").append("\n");
        }

        return builder.toString();
    }

    private static void addDestinationEmailAddresses(PersistentGroup recipient, final Set<String> emailAddresses) {
        for (final User user : recipient.getMembers()) {
            final EmailAddress emailAddress = user.getPerson().getEmailAddressForSendingEmails();
            if (emailAddress != null) {
                final String value = emailAddress.getValue();
                if (value != null && !value.isEmpty()) {
                    emailAddresses.add(value);
                }
            }
        }
    }

    protected String[] getReplyToAddresses(final Person person) {
        final String[] replyToAddresses = new String[getReplyTosSet().size()];
        int i = 0;
        for (final ReplyTo replyTo : getReplyTosSet()) {
            replyToAddresses[i++] = replyTo.getReplyToAddress(person);
        }
        return replyToAddresses;
    }

    public int dispatch() {
        final Person person = getPerson();
        final Set<String> destinationBccs = getDestinationBccs();
        final Set<String> tos = getRecipientAddresses(getTosSet());
        final Set<String> ccs = getRecipientAddresses(getCcsSet());
        createEmailBatch(person, tos, ccs, split(destinationBccs));
        return destinationBccs.size() + tos.size() + ccs.size();
    }

    @Atomic(mode = TxMode.WRITE)
    private void createEmailBatch(final Person person, final Set<String> tos, final Set<String> ccs,
            Set<Set<String>> destinationBccs) {
        if (getRootDomainObjectFromPendingRelation() != null) {
            for (final Set<String> bccs : destinationBccs) {
                if (!bccs.isEmpty()) {
                    new Email(getReplyToAddresses(person), Collections.emptySet(), Collections.emptySet(), bccs, this);
                }
            }
            if (!tos.isEmpty() || !ccs.isEmpty()) {
                new Email(getReplyToAddresses(person), tos, ccs, Collections.emptySet(), this);
            }
            setRootDomainObjectFromPendingRelation(null);
            setSent(new DateTime());
        }
    }

    private Set<Set<String>> split(final Set<String> destinations) {
        final Set<Set<String>> result = new HashSet<Set<String>>();
        int i = 0;
        Set<String> subSet = new HashSet<String>();
        for (final String destination : destinations) {
            if (i++ == 50) {
                result.add(subSet);
                subSet = new HashSet<String>();
                i = 1;
            }
            subSet.add(destination);
        }
        result.add(subSet);
        return result;
    }

    public int getPossibleRecipientsCount() {
        return (int) getRecipientsSet().stream().flatMap(g -> g.getMembers().stream()).distinct().count();
    }

    public int getRecipientsWithEmailCount() {
        return (int) getRecipientsSet().stream().flatMap(g -> g.getMembers().stream()).distinct()
                .filter(u -> u.getPerson().getEmailAddressForSendingEmails() != null).count();
    }

    public int getSentMailsCount() {
        return (int) getEmailsSet().stream().filter(e -> e.getConfirmedAddresses() != null)
                .flatMap(e -> e.getConfirmedAddresses().toCollection().stream()).distinct().count();
    }

    public int getFailedMailsCount() {
        return (int) getEmailsSet().stream().filter(e -> e.getFailedAddresses() != null)
                .flatMap(e -> e.getFailedAddresses().toCollection().stream()).distinct().count();
    }

    public String getFromName() {
        return getSender().getFromName().replace(",", "");
    }

    public String getFromAddress() {
        return getSender().getFromAddress();
    }

}
