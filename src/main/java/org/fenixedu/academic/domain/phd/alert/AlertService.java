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
package org.fenixedu.academic.domain.phd.alert;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.phd.InternalPhdParticipant;
import org.fenixedu.academic.domain.phd.PhdIndividualProgramProcess;
import org.fenixedu.academic.domain.phd.PhdParticipant;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.messaging.core.domain.Message;
import org.fenixedu.messaging.core.domain.Message.MessageBuilder;
import org.fenixedu.messaging.core.domain.MessageTemplate;
import org.fenixedu.messaging.core.domain.Sender;
import org.fenixedu.messaging.core.template.DeclareMessageTemplate;
import org.fenixedu.messaging.core.template.TemplateParameter;
import org.joda.time.LocalDate;

import com.google.common.collect.ImmutableMap;

//XXX HTML is not currently used though the interface supports it
@DeclareMessageTemplate(id = "org.fenixedu.academic.phd.alert.wrapper", description = "org.fenixedu.academic.phd.alert.wrapper.description", subject = "org.fenixedu.academic.phd.alert.wrapper.subject", text = "org.fenixedu.academic.phd.alert.wrapper.text", html = "org.fenixedu.academic.phd.alert.wrapper.html", parameters = {
        @TemplateParameter(id = "process", description = "org.fenixedu.academic.phd.alert.wrapper.parameter.process"),
        @TemplateParameter(id = "subject", description = "org.fenixedu.academic.phd.alert.wrapper.parameter.subject"),
        @TemplateParameter(id = "text", description = "org.fenixedu.academic.phd.alert.wrapper.parameter.text"),
        @TemplateParameter(id = "html", description = "org.fenixedu.academic.phd.alert.wrapper.parameter.html") }, bundle = Bundle.PHD)
public class AlertService {

    static void sendMessage(PhdAlert alert, Group bcc, String... bccs) {
        //XXX In an existing alert we expect the body and subject to have already been wrapped or not according to it's type's use
        MessageBuilder builder = Message.from(alert.getSender());
        if (bcc != null) {
            builder.bcc(bcc);
        }
        if (bccs != null) {
            builder.singleBcc(bccs);
        }
        builder.subject(alert.getSubject()).textBody(alert.getBody()).send();
    }

    static void sendMessage(PhdAlert alert, Group bcc) {
        sendMessage(alert,bcc,null);
    }

    static void sendMessage(PhdAlert alert, String... bccs) {
        sendMessage(alert,null,bccs);
    }

    static Message sendMessage(PhdIndividualProgramProcess process, boolean displayProcessInfo, Sender sender,
            LocalizedString subject, LocalizedString body, Group bcc, String... bccs) {
        MessageBuilder builder = Message.from(sender);
        if (bccs != null) {
            builder.singleBcc(bccs);
        }
        if (bcc != null) {
            builder.bcc(bcc);
        }
        if (displayProcessInfo) {
            builder.template("org.fenixedu.academic.phd.alert.wrapper").parameter("process", process)
                    .parameter("subject", subject).parameter("text", body).and();
        } else {
            builder.subject(subject).textBody(body);
        }
        return builder.send();
    }

    static void sendMessage(PhdIndividualProgramProcess process, boolean displayProcessInfo, LocalizedString subject,
            LocalizedString body, Group bcc, String... bccs) {
        sendMessage(process, displayProcessInfo, process.getAdministrativeOffice().getUnit().getSender(), subject, body, bcc,
                bccs);
    }

    static void sendMessage(PhdIndividualProgramProcess process, boolean displayProcessInfo, LocalizedString subject,
            LocalizedString body, Group bcc) {
        sendMessage(process, displayProcessInfo, process.getAdministrativeOffice().getUnit().getSender(), subject, body, bcc,
                null);
    }

    static void sendMessage(PhdIndividualProgramProcess process, boolean displayProcessInfo, LocalizedString subject,
            LocalizedString body, String... bccs) {
        sendMessage(process, displayProcessInfo, process.getAdministrativeOffice().getUnit().getSender(), subject, body, null,
                bccs);
    }

    static LocalizedString wrapAsAlertBody(PhdAlert alert, LocalizedString body) {
        return MessageTemplate.get("org.fenixedu.academic.phd.alert.wrapper")
                .getCompiledTextBody(ImmutableMap.of("process", alert.getProcess(), "text", body));
    }

    static LocalizedString wrapAsAlertSubject(PhdAlert alert, LocalizedString subject) {
        return MessageTemplate.get("org.fenixedu.academic.phd.alert.wrapper")
                .getCompiledSubject(ImmutableMap.of("process", alert.getProcess(), "subject", subject));
    }

    private static LocalizedString getMessageFromResource(String key) {
        return BundleUtil.getLocalizedString(Bundle.PHD, key);
    }

    static public void alertGuiders(PhdIndividualProgramProcess process, String subjectKey, String bodyKey,
            boolean displayProcessInfo) {
        alertGuiders(process, AlertMessage.create(subjectKey), AlertMessage.create(bodyKey), displayProcessInfo);
    }

    static public void alertGuiders(PhdIndividualProgramProcess process, AlertMessage subject, AlertMessage body,
            boolean displayProcessInfo) {
        alertGuiders(process, subject.getMessage(), body.getMessage(), displayProcessInfo);
    }

    //TODO refactor AlertMessage... is it still worth to keep? what work can be done by it with the template?
    //FIXME I forgot the prefixed option. it must now come in general for the whole and not for each content.
    //FIXME also, how do we treat this now? wrapped boolean params instead of the alertmessage params?
    //FIXME AlertMessage also standardized the message format templating

    //TODO have PhDAlert class decide on wrapped? new slot

    //TODO Alert

    //FIXME for external participants we do not know any language preferences so alerts must have both languages? uh oh

    static private void alertGuiders(PhdIndividualProgramProcess process, LocalizedString subject, LocalizedString body,
            boolean displayProcessInfo) {

        Map<Boolean, List<PhdParticipant>> p =
                process.getGuidingsAndAssistantGuidings().stream().collect(Collectors.partitioningBy(PhdParticipant::isInternal));

        p.get(false).forEach(guider -> {
            guider.ensureExternalAccess();
            sendMessage(process, displayProcessInfo, subject, body, guider.getEmail());
        });

        final Set<Person> toNotify =
                p.get(true).stream().map(InternalPhdParticipant.class::cast).map(InternalPhdParticipant::getPerson)
                        .collect(Collectors.toSet());

        if (!toNotify.isEmpty()) {
            createPhdCustomAlert(process, subject, body, Person.convertToUserGroup(toNotify), false, displayProcessInfo);
        }

    }

    static public void alertStudent(PhdIndividualProgramProcess process, String subjectKey, String bodyKey,
            boolean displayProcessInfo) {
        createPhdCustomAlert(process, subjectKey, bodyKey, process.getPerson().getUser().groupOf(), false, displayProcessInfo);
    }

    static public void alertStudent(PhdIndividualProgramProcess process, AlertMessage subject, AlertMessage body,
            boolean displayProcessInfo) {
        createPhdCustomAlert(process, subject, body, process.getPerson().getUser().groupOf(), false, displayProcessInfo);
    }

    static public void alertAcademicOffice(PhdIndividualProgramProcess process, String subjectKey, String bodyKey,
            boolean displayProcessInfo) {
        alertAcademicOffice(process, AcademicOperationType.MANAGE_PHD_PROCESSES, subjectKey, bodyKey, displayProcessInfo);
    }

    static public void alertAcademicOffice(PhdIndividualProgramProcess process, AcademicOperationType permissionType,
            String subjectKey, String bodyKey, boolean displayProcessInfo) {
        Group target = AcademicAuthorizationGroup.get(permissionType, process.getPhdProgram());
        createPhdCustomAlert(process, subjectKey, bodyKey, target, true, displayProcessInfo);
    }

    static public void alertAcademicOffice(PhdIndividualProgramProcess process, AcademicOperationType permissionType,
            AlertMessage subject, AlertMessage body, boolean displayProcessInfo) {
        Group target = AcademicAuthorizationGroup.get(permissionType, process.getPhdProgram());
        createPhdCustomAlert(process, subject, body, target, true, displayProcessInfo);
    }

    static public void alertCoordinators(PhdIndividualProgramProcess process, String subjectKey, String bodyKey,
            boolean displayProcessInfo) {
        Group target = Person.convertToUserGroup(process.getCoordinatorsFor(ExecutionYear.readCurrentExecutionYear()));
        createPhdCustomAlert(process, subjectKey, bodyKey, target, false, displayProcessInfo);
    }

    static public void alertCoordinators(PhdIndividualProgramProcess process, AlertMessage subject, AlertMessage body,
            boolean displayProcessInfo) {
        Group target = Person.convertToUserGroup(process.getCoordinatorsFor(ExecutionYear.readCurrentExecutionYear()));
        createPhdCustomAlert(process, subject, body, target, false, displayProcessInfo);
    }

    static public void alertResponsibleCoordinators(PhdIndividualProgramProcess process, AlertMessage subject, AlertMessage body,
            boolean displayProcessInfo) {
        Group target = Person.convertToUserGroup(process.getResponsibleCoordinatorsFor(ExecutionYear.readCurrentExecutionYear()));
        createPhdCustomAlert(process, subject, body, target, false, displayProcessInfo);
    }

    static private void createPhdCustomAlert(PhdIndividualProgramProcess process, String subjectKey, String bodyKey, Group target,
            boolean shared, boolean displayProcessInfo) {
        createPhdCustomAlert(process, AlertMessage.create(subjectKey), AlertMessage.create(bodyKey), target, shared,
                displayProcessInfo);
    }

    static private void createPhdCustomAlert(PhdIndividualProgramProcess process, AlertMessage subject, AlertMessage body,
            Group target, boolean shared, boolean displayProcessInfo) {
        createPhdCustomAlert(process, subject.getMessage(), body.getMessage(), target, shared, displayProcessInfo);
    }

    static private void createPhdCustomAlert(PhdIndividualProgramProcess process, LocalizedString subject, LocalizedString body,
            Group target, boolean shared, boolean displayProcessInfo) {
        new PhdCustomAlert(process, target, subject, body, true, new LocalDate(), false, shared, displayProcessInfo);
    }

    static public void alertParticipants(PhdIndividualProgramProcess process, LocalizedString subject, LocalizedString body,
            boolean displayProcessInfo, PhdParticipant... participants) {

        Map<Boolean, List<PhdParticipant>> p =
                Stream.of(participants).collect(Collectors.partitioningBy(PhdParticipant::isInternal));

        p.get(false).stream().map(PhdParticipant::getEmail)
                .forEach(email -> sendMessage(process, displayProcessInfo, subject, body, email));

        final Set<Person> toNotify =
                p.get(true).stream().map(InternalPhdParticipant.class::cast).map(InternalPhdParticipant::getPerson)
                        .collect(Collectors.toSet());

        if (!toNotify.isEmpty()) {
            createPhdCustomAlert(process, subject, body, Person.convertToUserGroup(toNotify), false, displayProcessInfo);
        }
    }

    static public class AlertMessage {
        private String label;
        private Object[] args;
        private boolean isKey = true;

        public AlertMessage label(String label) {
            this.label = label;
            return this;
        }

        public AlertMessage args(Object... args) {
            this.args = args;
            return this;
        }

        public AlertMessage isKey(boolean value) {
            this.isKey = value;
            return this;
        }

        public LocalizedString getMessage() {
            if (isKey) {
                LocalizedString template = getMessageFromResource(label);
                for (Locale l : template.getLocales()) {
                    template = template.with(l, MessageFormat.format(template.getContent(l), args));
                }
                return template;
            } else {
                return new LocalizedString(Locale.getDefault(), label);
            }
        }

        static public AlertMessage create(String label, Object... args) {
            return new AlertMessage().label(label).args(args);
        }

        static public LocalizedString get(String label, Object... args) {
            return new AlertMessage().label(label).args(args).getMessage();
        }
    }
}
