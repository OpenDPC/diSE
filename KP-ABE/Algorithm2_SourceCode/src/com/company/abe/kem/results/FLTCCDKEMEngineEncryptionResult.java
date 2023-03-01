package com.company.abe.kem.results;

import it.unisa.dia.gas.jpbc.Element;

import java.util.List;

public class FLTCCDKEMEngineEncryptionResult implements FLTCCDKEMEngineResult {
    private Element ys;
    private Element gs;
    private List<Element> e;

    public Element getYs() {
        return ys;
    }

    public void setYs(Element ys) {
        this.ys = ys;
    }

    public List<Element> getE() {
        return e;
    }

    public void setE(List<Element> e) {
        this.e = e;
    }

    public Element getGs() {
        return gs;
    }

    public void setGs(Element gs) {
        this.gs = gs;
    }
}
