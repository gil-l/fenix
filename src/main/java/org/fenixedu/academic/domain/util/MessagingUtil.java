package org.fenixedu.academic.domain.util;

import java.util.Set;

import org.fenixedu.academic.domain.Installation;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.messaging.domain.Message;
import org.fenixedu.messaging.domain.Message.MessageBuilder;
import org.fenixedu.messaging.domain.MessageDeletionPolicy;
import org.fenixedu.messaging.domain.MessagingSystem;
import org.fenixedu.messaging.domain.Sender;

public class MessagingUtil {

    public static MessageDeletionPolicy defaultDeletionPolicy;

    public static MessageDeletionPolicy getDefaultMessageDeletionPolicy() {
        if (defaultDeletionPolicy == null) {
            defaultDeletionPolicy = MessageDeletionPolicy.keepAmountOfMessages(500);
        }
        return defaultDeletionPolicy;
    }

    public static Sender createInstitutionalSender(String name, Group members) {
        Sender s =
                new Sender(String.format("%s (%s)", Unit.getInstitutionAcronym(), name), Installation.getInstance()
                        .getInstituitionalEmailAddress("noreply"), members, getDefaultMessageDeletionPolicy());
        s.addCurrentUserReplyTo();
        return s;
    }

    public static Message sendSystemMessage(String subject, String body, Set<String> bccs) {
        return sendNoReplyMessage(MessagingSystem.getInstance().getSystemSender(), subject, body, bccs);
    }

    public static Message sendSystemMessage(String subject, String body, Group... bccs) {
        return sendNoReplyMessage(MessagingSystem.getInstance().getSystemSender(), subject, body, bccs);
    }

    public static Message sendSystemMessage(String subject, String body, Set<String> bccs, Group... recipients) {
        return sendNoReplyMessage(MessagingSystem.getInstance().getSystemSender(), subject, body, bccs, recipients);
    }

    public static Message sendNoReplyMessage(Sender sender, String subject, String body, Set<String> bccs) {
        MessageBuilder builder = new MessageBuilder(sender, subject, body);
        builder.bcc(bccs);
        return builder.send();
    }

    public static Message sendNoReplyMessage(Sender sender, String subject, String body, Set<String> bccs, Group... recipients) {
        MessageBuilder builder = new MessageBuilder(sender, subject, body);
        builder.bcc(bccs);
        builder.bcc(recipients);
        return builder.send();
    }

    public static Message sendNoReplyMessage(Sender sender, String subject, String body, Group... bccs) {
        MessageBuilder builder = new MessageBuilder(sender, subject, body);
        builder.bcc(bccs);
        return builder.send();
    }

}
