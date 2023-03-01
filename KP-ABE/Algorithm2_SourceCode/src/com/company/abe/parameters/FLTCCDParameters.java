package com.company.abe.parameters;


import it.unisa.dia.gas.jpbc.Pairing;
import org.bouncycastle.crypto.CipherParameters;

public class FLTCCDParameters implements CipherParameters {
    private Pairing pairing;
    private int n;

    public FLTCCDParameters(Pairing pairing, int n) {
        this.pairing = pairing;
        this.n = n;
    }

    public Pairing getPairing() {
        return this.pairing;
    }

    public int getN() {
        return this.n;
    }
}
