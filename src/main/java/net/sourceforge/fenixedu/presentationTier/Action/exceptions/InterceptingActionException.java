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
 * InterceptingActionException.java
 *
 * March 2nd, 2003, 17h38
 */

package net.sourceforge.fenixedu.presentationTier.Action.exceptions;

/**
 * 
 * @author Luis Cruz & Sara Ribeiro
 */

public class InterceptingActionException extends FenixActionException {

    public static String key = "error.exception.intercepting.lesson";

    public InterceptingActionException(Throwable cause) {
        super(key, cause);
    }

    public InterceptingActionException(Object value, Throwable cause) {
        super(key, value, cause);
    }

    public InterceptingActionException(Object[] values, Throwable cause) {
        super(key, values, cause);
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        InterceptingActionException.key = key;
    }

}