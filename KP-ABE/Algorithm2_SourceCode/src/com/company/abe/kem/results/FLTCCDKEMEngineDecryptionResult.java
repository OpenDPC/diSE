package com.company.abe.kem.results;

import it.unisa.dia.gas.jpbc.Element;

public class FLTCCDKEMEngineDecryptionResult implements FLTCCDKEMEngineResult {
    private Element key;

    public Element getKey() {
        return key;
    }

    public void setKey(Element key) {
        this.key = key;
    }
}
