package org.fenixedu.academic.domain.util;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.messaging.core.domain.Sender;

import com.google.common.base.Strings;

/**
 * Created by gill on 5/9/17.
 */
public class MessageUtil {

    //XXX temporary class to centralize Academic's messgaing quirks until they are better integraterd elsewhere

    public static String getReplyToWithCurrentUserDefault(Sender sender) {
        String replyTo = sender.getReplyTo();
        return Strings.isNullOrEmpty(sender.getReplyTo()) ? Authenticate.getUser().getEmail() : replyTo;
    }
}
