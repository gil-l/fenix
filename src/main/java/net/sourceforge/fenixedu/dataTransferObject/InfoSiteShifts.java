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
 * Created on 5/Mai/2003
 *
 * 
 */
package net.sourceforge.fenixedu.dataTransferObject;

import java.util.List;

/**
 * @author João Mota
 * 
 * 
 */
public class InfoSiteShifts extends DataTranferObject implements ISiteComponent {

    private String infoExecutionPeriodName;

    private String infoExecutionYearName;

    private List shifts;

    private InfoShift oldShift;

    /**
     * @return
     */
    public List getShifts() {
        return shifts;
    }

    /**
     * @param list
     */
    public void setShifts(List list) {
        shifts = list;
    }

    /**
     * @return InfoShift
     */
    public InfoShift getOldShift() {
        return oldShift;
    }

    /**
     * @param InfoShift
     */
    public void setOldShift(InfoShift oldShift) {
        this.oldShift = oldShift;
    }

    /**
     * @return
     */
    public String getInfoExecutionPeriodName() {
        return infoExecutionPeriodName;
    }

    /**
     * @return
     */
    public String getInfoExecutionYearName() {
        return infoExecutionYearName;
    }

    /**
     * @param string
     */
    public void setInfoExecutionPeriodName(String string) {
        infoExecutionPeriodName = string;
    }

    /**
     * @param string
     */
    public void setInfoExecutionYearName(String string) {
        infoExecutionYearName = string;
    }

}