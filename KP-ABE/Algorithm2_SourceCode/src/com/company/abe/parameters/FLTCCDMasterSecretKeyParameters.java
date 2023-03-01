package com.company.abe.parameters;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

public class FLTCCDMasterSecretKeyParameters extends FLTCCDKeyParameters {
    private Element y;
    private Element[] ts;

    public FLTCCDMasterSecretKeyParameters(FLTCCDParameters parameters, Element y, Element[] ts) {
        super(false, parameters);
        this.y = y.getImmutable();
        this.ts = ElementUtils.cloneImmutable(ts);
    }

    public Element getY() {
        return y;
    }

    public Element getTAt(int index) {
        return this.ts[index];
    }
}
