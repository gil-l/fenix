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
package net.sourceforge.fenixedu.presentationTier.renderers.providers.student;

import java.util.Set;

import net.sourceforge.fenixedu.dataTransferObject.student.RegistrationSelectExecutionYearBean;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.degreeStructure.CourseGroup;
import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class CourseGroupsProviderForRegistrationAcademicServiceRequest implements DataProvider {

    @Override
    public Object provide(Object source, Object currentValue) {
        final RegistrationSelectExecutionYearBean bean = ((RegistrationSelectExecutionYearBean) source);
        final DegreeCurricularPlan degreeCurricularPlan = bean.getRegistration().getLastDegreeCurricularPlan();
        final Set<CourseGroup> courseGroups = degreeCurricularPlan.getAllCoursesGroups();
        courseGroups.removeAll(degreeCurricularPlan.getRoot().getCycleCourseGroups());
        courseGroups.remove(degreeCurricularPlan.getRoot());
        return courseGroups;
    }

    @Override
    public Converter getConverter() {
        return new DomainObjectKeyConverter();
    }

}
