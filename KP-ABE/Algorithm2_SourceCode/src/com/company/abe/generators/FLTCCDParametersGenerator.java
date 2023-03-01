package com.company.abe.generators;

import com.company.abe.parameters.FLTCCDParameters;
import it.unisa.dia.gas.jpbc.Pairing;

public class FLTCCDParametersGenerator {
    private Pairing pairing;
    private int n;

    public FLTCCDParametersGenerator() {
    }

    public FLTCCDParametersGenerator init(Pairing pairing, int n) {
        this.pairing = pairing;
        this.n = n;
        return this;
    }

    public FLTCCDParameters generateParameters() {
        return new FLTCCDParameters(this.pairing, this.n);
    }
}
