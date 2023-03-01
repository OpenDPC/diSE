package com.company.abe.parameters;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

public class FLTCCDPublicKeyParameters extends FLTCCDKeyParameters {
    private Element capitalY;
    private Element[] capitalTs;
    private Element groupGenerator;

    public FLTCCDPublicKeyParameters(FLTCCDParameters parameters, Element capitalY, Element[] capitalTs, Element groupGenerator) {
        super(false, parameters);
        this.capitalY = capitalY.getImmutable();
        this.capitalTs = ElementUtils.cloneImmutable(capitalTs);
        this.groupGenerator = groupGenerator;
    }

    public Element getY() {
        return capitalY;
    }

    public Element getCapitalTAt(int index) {
        return this.capitalTs[index];
    }

    public Element getGroupGenerator() {
        return groupGenerator;
    }
}
