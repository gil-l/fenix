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
package net.sourceforge.fenixedu.domain;

public class ContentManagementLog extends ContentManagementLog_Base {

    public ContentManagementLog() {
        super();
    }

    public ContentManagementLog(ExecutionCourse executionCourse, String description) {
        super();
        if (getExecutionCourse() == null) {
            setExecutionCourse(executionCourse);
        }
        setDescription(description);
    }

    public static ContentManagementLog createContentManagementLog(ExecutionCourse executionCourse, String description) {
        return new ContentManagementLog(executionCourse, description);
    }

    public static ContentManagementLog createLog(ExecutionCourse executionCourse, String bundle, String key, String... args) {
        final String label = generateLabelDescription(bundle, key, args);
        return createContentManagementLog(executionCourse, label);
    }

    @Override
    public ExecutionCourseLogTypes getExecutionCourseLogType() {
        return ExecutionCourseLogTypes.CONTENT;
    }
}
