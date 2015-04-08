package org.fenixedu.academic.util;

import java.util.Comparator;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Installation;
import org.fenixedu.academic.domain.accessControl.CoordinatorGroup;
import org.fenixedu.academic.domain.accessControl.StudentGroup;
import org.fenixedu.academic.domain.accessControl.TeacherGroup;
import org.fenixedu.academic.domain.accessControl.TeacherResponsibleOfExecutionCourseGroup;
import org.fenixedu.academic.domain.accessControl.UnitGroup;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.messaging.domain.Message.MessageBuilder;
import org.fenixedu.messaging.domain.MessageDeletionPolicy;
import org.fenixedu.messaging.domain.MessagingSystem;
import org.fenixedu.messaging.domain.ReplyTo;
import org.fenixedu.messaging.domain.Sender;

public class MessagingUtils {
    private MessageDeletionPolicy defaultMessageDeletionPolicy;

    private static MessagingUtils instance;

    public static final Comparator<ReplyTo> COMPARATOR_BY_ADDRESS = new Comparator<ReplyTo>() {
        @Override
        public int compare(final ReplyTo replyTo1, final ReplyTo replyTo2) {
            String address1 = replyTo1.getAddress();
            address1 = address1 == null ? "" : address1;
            String address2 = replyTo1.getAddress();
            address1 = address2 == null ? "" : address2;
            address1.compareTo(address2);
        }

    };

    private MessagingUtils() {
        defaultMessageDeletionPolicy = MessageDeletionPolicy.keepAmountOfMessages(500);
    }

    public static MessagingUtils getInstance() {
        return instance != null ? instance : (instance = new MessagingUtils());
    }

    public static Sender sender(Degree d) {

        String name = String.format("%s (%s: %s)", Unit.getInstitutionAcronym(), d.getSigla(), "Coordenação");

        Sender sender =
                new Sender(name, Installation.getInstance().getInstituitionalEmailAddress("noreply"), CoordinatorGroup.get(d),
                        MessagingUtils.getInstance().getDefaultMessageDeletionPolicy());
        sender.addCurrentUserReplyTo();

        for (CycleType cycleType : d.getDegreeType().getCycleTypes()) {
            sender.addRecipient(StudentGroup.get(d, cycleType));
        }
        sender.addRecipient(CoordinatorGroup.get(d));
        sender.addRecipient(TeacherGroup.get(d));
        sender.addRecipient(StudentGroup.get(d, null));
        sender.addRecipient(RoleType.TEACHER.actualGroup());
        sender.addRecipient(StudentGroup.get());
        return sender;
    }

    public static Sender sender(ExecutionCourse ec) {
        Sender sender = ec.getSender();
        if (sender != null) {
            String name = Unit.getInstitutionAcronym();
            String degreeName = ec.getDegreePresentationString();
            String courseName = ec.getNome();

            if (ec.getExecutionPeriod() != null && ec.getExecutionPeriod().getQualifiedName() != null) {
                String period = ec.getExecutionPeriod().getQualifiedName().replace('/', '-');
                name += String.format(" (%s: %s, %s)", Unit.getInstitutionAcronym(), degreeName, courseName, period);
            } else {
                name += String.format(" (%s: %s)", degreeName, courseName);
            }

            sender =
                    new Sender(name, Installation.getInstance().getInstituitionalEmailAddress("noreply"), TeacherGroup.get(ec),
                            MessagingUtils.getInstance().getDefaultMessageDeletionPolicy());
            sender.addCurrentUserReplyTo();
            sender.addReplyTo(ec.getEmail());
            sender.addRecipient(TeacherGroup.get(ec));
            sender.addRecipient(StudentGroup.get(ec));
            sender.addRecipient(TeacherResponsibleOfExecutionCourseGroup.get(ec));
            ec.setSender(sender);
        } else {
            return sender;
        }
        return sender;
    }

    public static Sender sender(Unit u) {
        Sender sender =
                new Sender(String.format("%s (%s)", Unit.getInstitutionAcronym(), u.getName()), Installation.getInstance()
                        .getInstituitionalEmailAddress("noreply"), UnitGroup.recursiveWorkers(u), MessagingUtils.getInstance()
                        .getDefaultMessageDeletionPolicy());
        sender.addCurrentUserReplyTo();
        return sender;
    }

    public static void systemMessage(String subject, String body, String bccs) {
        Sender sender = MessagingSystem.getInstance().getSystemSender();
        MessageBuilder builder = new MessageBuilder(sender, subject, body);
        builder.replyTo(sender.getReplyTos());
        builder.bcc(bccs);
        builder.send();
    }

    public MessageDeletionPolicy getDefaultMessageDeletionPolicy() {
        return defaultMessageDeletionPolicy;
    }
}
