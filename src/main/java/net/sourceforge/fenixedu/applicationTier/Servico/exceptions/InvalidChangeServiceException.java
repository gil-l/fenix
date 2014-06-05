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
package net.sourceforge.fenixedu.applicationTier.Servico.exceptions;

/**
 * 
 * @author Nuno Nunes (nmsn@rnl.ist.utl.pt) Joana Mota (jccm@rnl.ist.utl.pt)
 */
public class InvalidChangeServiceException extends FenixServiceException {

    /**
     *  
     */
    public InvalidChangeServiceException() {

    }

    /**
     * @param s
     */
    public InvalidChangeServiceException(String s) {
        super(s);

    }

    /**
     * @param cause
     */
    public InvalidChangeServiceException(Throwable cause) {
        super(cause);

    }

    /**
     * @param message
     * @param cause
     */
    public InvalidChangeServiceException(String message, Throwable cause) {
        super(message, cause);

    }

    @Override
    public String toString() {
        String result = "[InvalidChangeServiceException\n";
        result += "message" + this.getMessage() + "\n";
        result += "cause" + this.getCause() + "\n";
        result += "]";
        return result;
    }
}