package com.company.abe.parameters;

import com.company.abe.circuit.FLTCCDDefaultCircuit;
import com.company.abe.kem.results.FLTCCDKEMEngineEncryptionResult;
import it.unisa.dia.gas.jpbc.Element;

import java.util.List;
import java.util.Map;

public class FLTCCDSecretKeyParameters extends FLTCCDKeyParameters {
    private FLTCCDDefaultCircuit circuit;
    private Map<Integer, List<Element>> d;
    private Map<Integer, List<Element>> p;
    private FLTCCDKEMEngineEncryptionResult encryptionResult;

    public FLTCCDSecretKeyParameters(FLTCCDParameters parameters, FLTCCDDefaultCircuit circuit,
                                     Map<Integer, List<Element>> d, Map<Integer, List<Element>> p,
                                     FLTCCDKEMEngineEncryptionResult encryptionResult) {
        super(true, parameters);
        this.circuit = circuit;
        this.d = d;
        this.p = p;
        this.encryptionResult = encryptionResult;
    }

    public FLTCCDDefaultCircuit getCircuit() {
        return circuit;
    }

    public List<Element> getDElementsAt(int index) {
        return this.d.get(index);
    }
    public List<Element> getPElementsAt(int index) {
        return this.p.get(index);
    }

    public FLTCCDKEMEngineEncryptionResult getEncryptionResult() {
        return encryptionResult;
    }

    public void setEncryptionResult(FLTCCDKEMEngineEncryptionResult encryptionResult) {
        this.encryptionResult = encryptionResult;
    }
}
