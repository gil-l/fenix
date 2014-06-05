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
/*
 * Created on Dec 14, 2003 by jpvl
 *  
 */
package net.sourceforge.fenixedu.applicationTier.Filtro.student.tests;

import java.util.Calendar;

/**
 * @author jpvl
 */
public class ReadStudentTestForCorrectionFilter extends ReadStudentTestBaseFilter {

    public static final ReadStudentTestForCorrectionFilter instance = new ReadStudentTestForCorrectionFilter();

    public ReadStudentTestForCorrectionFilter() {
    }

    /*
     * (non-Javadoc)
     * 
     * @seeServidorAplicacao.Filtro.student.tests.StudentReadTestToDoFilter#
     * testIfCanReadTest(java.util.Calendar, java.util.Calendar,
     * java.util.Calendar)
     */
    @Override
    protected boolean canReadTest(Calendar now, Calendar beginDate, Calendar endDate) {
        return endDate.before(now);
    }
}