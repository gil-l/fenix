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
package net.sourceforge.fenixedu.domain.time.calendarStructure;

import org.joda.time.DateTime;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class GradeSubmissionInSpecialSeasonCE extends GradeSubmissionInSpecialSeasonCE_Base {

    public GradeSubmissionInSpecialSeasonCE(AcademicCalendarEntry academicCalendarEntry, MultiLanguageString title,
            MultiLanguageString description, DateTime begin, DateTime end, AcademicCalendarRootEntry rootEntry) {

        super();
        super.initEntry(academicCalendarEntry, title, description, begin, end, rootEntry);
    }

    private GradeSubmissionInSpecialSeasonCE(AcademicCalendarEntry academicCalendarEntry, GradeSubmissionCE gradeSubmissionCE) {
        super();
        super.initVirtualEntry(academicCalendarEntry, gradeSubmissionCE);
    }

    @Override
    protected AcademicCalendarEntry createVirtualEntry(AcademicCalendarEntry parentEntry) {
        return new GradeSubmissionInSpecialSeasonCE(parentEntry, this);
    }
}
