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
package net.sourceforge.fenixedu.presentationTier.Action.phd.candidacy.publicProgram.institution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramProcessBean;
import net.sourceforge.fenixedu.domain.phd.PhdProgram;
import net.sourceforge.fenixedu.domain.phd.candidacy.InstitutionPhdCandidacyPeriod;
import net.sourceforge.fenixedu.domain.phd.candidacy.PhdProgramCandidacyProcessBean;
import net.sourceforge.fenixedu.presentationTier.renderers.providers.AbstractDomainObjectProvider;

public class PhdProgramsProviderForPublicCandidacy extends AbstractDomainObjectProvider {

    @Override
    public Object provide(Object source, Object current) {
        // return fromFocusArea(source);

        if (source instanceof PhdProgramCandidacyProcessBean) {
            PhdProgramCandidacyProcessBean bean = (PhdProgramCandidacyProcessBean) source;
            InstitutionPhdCandidacyPeriod phdCandidacyPeriod = (InstitutionPhdCandidacyPeriod) bean.getPhdCandidacyPeriod();

            return phdCandidacyPeriod.getPhdPrograms();

        } else if (source instanceof PhdIndividualProgramProcessBean) {
            PhdIndividualProgramProcessBean bean = (PhdIndividualProgramProcessBean) source;
            InstitutionPhdCandidacyPeriod publicPhdCandidacyPeriod =
                    (InstitutionPhdCandidacyPeriod) bean.getIndividualProgramProcess().getCandidacyProcess()
                            .getPublicPhdCandidacyPeriod();

            return publicPhdCandidacyPeriod.getPhdPrograms();
        }

        return Collections.EMPTY_LIST;
    }

    protected Object fromFocusArea(Object source) {
        PhdProgramCandidacyProcessBean bean = (PhdProgramCandidacyProcessBean) source;
        List<PhdProgram> activePhdProgramList = new ArrayList<PhdProgram>();
        InstitutionPhdCandidacyPeriod phdCandidacyPeriod = (InstitutionPhdCandidacyPeriod) bean.getPhdCandidacyPeriod();

        if (bean.getFocusArea() == null) {
            return Collections.EMPTY_LIST;
        }

        for (PhdProgram phdProgram : bean.getFocusArea().getPhdPrograms()) {
            if (phdCandidacyPeriod.getPhdPrograms().contains(phdProgram)) {
                activePhdProgramList.add(phdProgram);
            }
        }

        return activePhdProgramList;
    }

}
