package net.sourceforge.fenixedu.applicationTier.Servico.site;

import net.sourceforge.fenixedu.domain.UnitSite;
import net.sourceforge.fenixedu.domain.organizationalStructure.Function;
import net.sourceforge.fenixedu.util.MultiLanguageString;

public class EditVirtualFunction extends ManageVirtualFunction {

    public void run(UnitSite site, Function function, MultiLanguageString name) {
	checkFunction(site, function);
	function.setTypeName(name);
    }
}
