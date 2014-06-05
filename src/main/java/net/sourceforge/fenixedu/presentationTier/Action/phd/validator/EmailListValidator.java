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
package net.sourceforge.fenixedu.presentationTier.Action.phd.validator;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;
import pt.utl.ist.fenix.tools.util.EMail;

public class EmailListValidator extends HtmlValidator {

    public EmailListValidator() {
        setMessage("renderers.validator.list.emails");
    }

    public boolean validateEmailList(String emailListString) {
        if (!StringUtils.isEmpty(emailListString)) {
            String[] emails = emailListString.split(",");
            for (String emailString : emails) {
                final String email = emailString.trim();
                if (!email.matches(EMail.W3C_EMAIL_SINTAX_VALIDATOR)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void performValidation() {
        String text = getComponent().getValue();
        setValid(validateEmailList(text));
    }

}
