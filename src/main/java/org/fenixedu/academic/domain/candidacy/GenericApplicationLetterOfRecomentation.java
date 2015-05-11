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
package org.fenixedu.academic.domain.candidacy;

import org.fenixedu.academic.domain.util.MessagingUtil;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixframework.Atomic;

public class GenericApplicationLetterOfRecomentation extends GenericApplicationLetterOfRecomentation_Base {

    public GenericApplicationLetterOfRecomentation(GenericApplicationRecomentation recomentation, String displayName,
            String fileName, byte[] content) {
        super();
        init(fileName, displayName, content, NobodyGroup.get());
        setRecomentation(recomentation);
        sendEmailForRecommendationUploadNotification();
    }

    @Atomic
    public void deleteFromApplication() {
        delete();
    }

    @Override
    protected void disconnect() {
        setRecomentation(null);
        super.disconnect();
    }

    public void sendEmailForRecommendationUploadNotification() {
        final String subject =
                BundleUtil.getString(Bundle.CANDIDATE, "label.application.recomentation.upload.notification.email.subject");
        final String body =
                BundleUtil.getString(Bundle.CANDIDATE, "label.application.recomentation.upload.notification.email.body",
                        getRecomentation().getName(), getRecomentation().getInstitution());

        MessagingUtil.sendSystemMessage(subject, body, getRecomentation().getGenericApplication().getEmail());
    }

}
