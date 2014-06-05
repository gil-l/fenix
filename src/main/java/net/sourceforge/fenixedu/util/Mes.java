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
/**
 * 
 * Autor: Ricardo Nortadas
 *  
 */

package net.sourceforge.fenixedu.util;

public class Mes extends FenixUtil {

    public static final int JANEIRO = 1;

    public static final int FEVEREIRO = 2;

    public static final int MARCO = 3;

    public static final int ABRIL = 4;

    public static final int MAIO = 5;

    public static final int JUNHO = 6;

    public static final int JULHO = 7;

    public static final int AGOSTO = 8;

    public static final int SETEMBRO = 9;

    public static final int OUTUBRO = 10;

    public static final int NOVEMBRO = 11;

    public static final int DEZEMBRO = 12;

    private Integer _Mes;

    public Mes() {
    }

    public Mes(int Mes) {
        this._Mes = new Integer(Mes);
    }

    public Mes(Integer Mes) {
        this._Mes = Mes;
    }

    public Integer getMes() {
        return this._Mes;
    }

    public void setMes(int Mes) {
        this._Mes = new Integer(Mes);
    }

    public void setMes(Integer Mes) {
        this._Mes = Mes;
    }

    @Override
    public boolean equals(Object obj) {
        boolean resultado = false;
        if (obj instanceof Mes) {
            Mes Mes = (Mes) obj;
            resultado = (this.getMes().intValue() == Mes.getMes().intValue());
        }
        return resultado;
    }

    @Override
    public String toString() {
        int mes = this._Mes.intValue();
        switch (mes) {
        case JANEIRO:
            return "Janeiro";
        case FEVEREIRO:
            return "Fevereiro";
        case MARCO:
            return "Março";
        case ABRIL:
            return "Abril";
        case MAIO:
            return "Maio";
        case JUNHO:
            return "Junho";
        case JULHO:
            return "Julho";
        case AGOSTO:
            return "Agosto";
        case SETEMBRO:
            return "Setembro";
        case OUTUBRO:
            return "Outubro";
        case NOVEMBRO:
            return "Novembro";
        case DEZEMBRO:
            return "Dezembro";
        }
        return "Erro: Invalid month";
    }

    public String toAbbreviationString() {
        int mes = this._Mes.intValue();
        switch (mes) {
        case JANEIRO:
            return "Jan.";
        case FEVEREIRO:
            return "Fev.";
        case MARCO:
            return "Mar.";
        case ABRIL:
            return "Abr.";
        case MAIO:
            return "Mai.";
        case JUNHO:
            return "Jun.";
        case JULHO:
            return "Jul.";
        case AGOSTO:
            return "Ago.";
        case SETEMBRO:
            return "Set.";
        case OUTUBRO:
            return "Out.";
        case NOVEMBRO:
            return "Nov.";
        case DEZEMBRO:
            return "Dez.";
        }
        return "Erro: Invalid month";
    }
}