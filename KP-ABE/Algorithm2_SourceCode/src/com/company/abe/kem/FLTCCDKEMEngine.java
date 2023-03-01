package com.company.abe.kem;

import com.company.abe.circuit.FLTCCDDefaultCircuit;
import com.company.abe.kem.results.FLTCCDKEMEngineDecryptionResult;
import com.company.abe.kem.results.FLTCCDKEMEngineEncryptionResult;
import com.company.abe.kem.results.FLTCCDKEMEngineResult;
import com.company.abe.parameters.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

import static com.company.abe.circuit.FLTCCDDefaultCircuit.*;
import static com.company.abe.circuit.FLTCCDDefaultCircuit.FLTCCDGateType.INPUT;
import static com.google.common.collect.Lists.newArrayList;

public class FLTCCDKEMEngine {
    private boolean forEncryption;
    private FLTCCDKeyParameters keyParameters;
    private Pairing pairing;

    public FLTCCDKEMEngine(boolean forEncryption, FLTCCDKeyParameters keyParameters) {
        this.forEncryption = forEncryption;
        this.keyParameters = keyParameters;

        if (this.forEncryption) {
            if (!(this.keyParameters instanceof FLTCCDEncryptionParameters)) {
                throw new IllegalArgumentException("FLTCCDEncryptionParameters are required for encryption.");
            }
        } else if (!(this.keyParameters instanceof FLTCCDDecryptionParameters)) {
            throw new IllegalArgumentException("GGHSW13SecretKeyParameters are required for decryption.");
        }

        this.pairing = keyParameters.getParameters().getPairing();
    }

    public FLTCCDKEMEngineResult process() {
        String assignment;
        if (this.keyParameters instanceof FLTCCDDecryptionParameters) {
            FLTCCDKEMEngineDecryptionResult decryptionResult = new FLTCCDKEMEngineDecryptionResult();


            // decryption phase
            FLTCCDDecryptionParameters decKey = (FLTCCDDecryptionParameters) this.keyParameters;
            FLTCCDSecretKeyParameters sk = decKey.getSecretKey();
            assignment = decKey.getAssignment();

            List<Element> e = decKey.getSecretKey().getEncryptionResult().getE();

            Map<Integer, List<Element>> vA = Maps.newHashMap();
            for (int i = 0; i < sk.getCircuit().getN(); i++) {
                List<Element> elements = Lists.newArrayList();

                for (int j = 0; j < decKey.getSecretKey().getDElementsAt(i).size(); j++) {
                    if (assignment.charAt(i) == '1') {
                        try {
                            Element element1 = e.get(i).duplicate();
                            Element element2 = decKey.getSecretKey().getDElementsAt(i).get(j);
                            Element element = pairing.pairing(element1, element2);

                            elements.add(element);
                        } catch (Exception e1) {
                            elements.add(null);
                        }
                    } else {
                        elements.add(null);
                    }
                }
                vA.put(i, elements);
            }

            Element key = reconstruct(decKey.getSecretKey().getCircuit(), decKey.getSecretKey(), vA, decKey.getSecretKey().getEncryptionResult().getGs());
            decryptionResult.setKey(key);

            return decryptionResult;
        } else {
            FLTCCDKEMEngineEncryptionResult encryptionResult = new FLTCCDKEMEngineEncryptionResult();
            // encryption phase
            FLTCCDEncryptionParameters encKey = (FLTCCDEncryptionParameters) this.keyParameters;
            FLTCCDPublicKeyParameters publicKey = encKey.getPublicKey();
            assignment = encKey.getAssignment();

            Element s = this.pairing.getZr().newRandomElement().getImmutable();
            int n = publicKey.getParameters().getN();
            List<Element> e = Lists.newArrayList();

            for(int i = 0; i < n; ++i) {
                if (assignment.charAt(i) == '1') {
                    e.add(publicKey.getCapitalTAt(i).powZn(s));
                } else {
                    e.add(null);
                }
            }

            Element ys = encKey.getPublicKey().getY().duplicate().powZn(s);

            encryptionResult.setE(e);
            encryptionResult.setYs(ys);
            encryptionResult.setGs(publicKey.getGroupGenerator().duplicate().powZn(s));

            return encryptionResult;
        }
    }

    private Element reconstruct(FLTCCDDefaultCircuit circuit, FLTCCDSecretKeyParameters secretKey, Map<Integer, List<Element>> vA, Element gs) {
        Map<Integer, List<Element>> r = Maps.newHashMap();

        List<FLTCCDDefaultGate> bottomUpGates = newArrayList(circuit.iterator());
        for (FLTCCDDefaultGate gate : bottomUpGates) {
            if (gate.getType() == INPUT) {
                // assign to each wire that connects to an input gate the vA value.
                for (FLTCCDDefaultGate outputGate : bottomUpGates) {
                    if (outputGate.getType() == INPUT) {
                        continue;
                    }
                    for (int i = 0; i < outputGate.getInputSize(); i++) {
                        if (outputGate.getInputIndexAt(i) == gate.getIndex()) {
                            r.put(circuit.getWireIndex(gate.getIndex(), outputGate.getIndex()), vA.get(gate.getIndex()));
                        }
                    }
                }
            } else {
                switch (gate.getType()) {
                    case OR: {
                        int outputGateIndex = getOutputGateIndex(bottomUpGates, gate);

                        Assert.assertEquals(r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).size(),
                                r.get(circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex())).size());
                        List<Element> elements = Lists.newArrayList();
                        for (int i = 0; i < r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).size(); i++) {
                            if (r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).get(i) == null) {
                                if (r.get(circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex())).get(i) == null) {
                                    elements.add(null);
                                } else {
                                    elements.add(r.get(circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex())).get(i).duplicate());
                                }
                            } else {
                                elements.add(r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).get(i).duplicate());
                            }
                        }

                        if (outputGateIndex == -1) {
                            return elements.get(0);
                        }
                        r.put(circuit.getWireIndex(gate.getIndex(), outputGateIndex), elements);

                        break;
                    }
                    case INPUT:
                        break;
                    case AND: {
                        int outputGateIndex = getOutputGateIndex(bottomUpGates, gate);

                        Assert.assertEquals(r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).size(),
                                r.get(circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex())).size());

                        List<Element> elements = Lists.newArrayList();
                        for (int i = 0; i < r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).size(); i++) {
                            if (r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).get(i) == null ||
                                    r.get(circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex())).get(i) == null) {
                                elements.add(null);
                            } else {
                                Element element1 = r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex())).get(i).duplicate();
                                Element element2 = r.get(circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex())).get(i).duplicate();

                                elements.add(element1.mul(element2));
                            }
                        }

                        if (outputGateIndex == -1) {
                            return elements.get(0);
                        }

                        r.put(circuit.getWireIndex(gate.getIndex(), outputGateIndex), elements);

                        break;
                    }
                    case FO: {
                        List<Integer> foGateIndexes = getFOGateIndexes(bottomUpGates, gate);

                        // splitting
                        Map<Integer, List<Element>> splitRs = Maps.newHashMap();
                        List<Element> gateRs = r.get(circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex()));
                        for (Integer foGateIndex : foGateIndexes) {
                            int wireIndex = circuit.getWireIndex(gate.getIndex(), foGateIndex);
                            int listSize = secretKey.getPElementsAt(wireIndex).size();

                            splitRs.put(wireIndex, gateRs.subList(0, listSize));
                            gateRs = gateRs.subList(listSize, gateRs.size());
                        }

                        for (Integer foGateIndex : foGateIndexes) {
                            int wireIndex = circuit.getWireIndex(gate.getIndex(), foGateIndex);

                            List<Element> elements = Lists.newArrayList();
                            for (int j = 0; j < splitRs.get(wireIndex).size(); j++) {
                                if (splitRs.get(wireIndex).get(j) == null) {
                                    elements.add(null);
                                    continue;
                                }
                                Element element = splitRs.get(wireIndex).get(j).duplicate().mul(pairing.pairing(secretKey.getPElementsAt(wireIndex).get(j), gs));
                                elements.add(element);
                            }
                            r.put(wireIndex, elements);
                        }

                        break;
                    }
                    case KN: {
                        int outputGateIndex = getOutputGateIndex(bottomUpGates, gate);

                        int size = -1;
                        for (int i = 0; i < gate.getInputSize(); i++) {
                            if (size == -1) {
                                size = r.get(circuit.getWireIndex(gate.getInputIndexAt(i), gate.getIndex())).size();
                            }
                            Assert.assertEquals(size, r.get(circuit.getWireIndex(gate.getInputIndexAt(i), gate.getIndex())).size());
                        }

                        r.put(circuit.getWireIndex(gate.getIndex(), outputGateIndex), Lists.newArrayList());
                        for (int i = 0; i < size; i++) {
                            Element element;
                            List<Integer> ks = Lists.newArrayList();
                            List<Element> elements = Lists.newArrayList();
                            for (int j = 0; j < gate.getInputSize(); j++) {
                                if (r.get(circuit.getWireIndex(gate.getInputIndexAt(j), gate.getIndex())).get(i) == null
                                        || ks.size() >= gate.getK()) {
                                    continue;
                                }

                                ks.add(j + 1);
                                elements.add(r.get(circuit.getWireIndex(gate.getInputIndexAt(j), gate.getIndex())).get(i));
                            }
                            Assert.assertEquals(elements.size(), gate.getK());

                            for (int j = 0; j < ks.size(); j++) {
                                element = pairing.getZr().newOneElement();
                                Element xjElement = pairing.getZr().newElement(ks.get(j));

                                for (int jj = 0; jj < gate.getK(); jj++) {
                                    if (jj == j) {
                                        continue;
                                    }

                                    Element zeroElement = pairing.getZr().newZeroElement();
                                    Element xjjElement = pairing.getZr().newElement(ks.get(jj));

                                    element.mul(zeroElement.duplicate()
                                            .sub(xjjElement)
                                            .div(xjElement.duplicate().sub(xjjElement))
                                    );
                                }

                                elements.get(j).powZn(element);
                            }

                            element = elements.get(0);
                            for (int j = 1; j < gate.getK(); j++) {
                                element = element.mul(elements.get(j));
                            }

                            r.get(circuit.getWireIndex(gate.getIndex(), outputGateIndex)).add(element);
                        }

                        break;
                    }
                    default:
                        break;
                }
            }
        }


        return r.get(circuit.getWireIndex(circuit.getOutputGate().getIndex(), -1)).get(0);
    }

    private int getOutputGateIndex(List<FLTCCDDefaultGate> bottomUpGates, FLTCCDDefaultGate gate) {
        int outputGateIndex = -1;
        for (FLTCCDDefaultGate outputGate : bottomUpGates) {
            if (outputGate.getType() == INPUT) {
                continue;
            }
            for (int i = 0; i < outputGate.getInputSize(); i++) {
                if (outputGate.getInputIndexAt(i) == gate.getIndex()) {
                    outputGateIndex = outputGate.getIndex();

                    break;
                }
            }
        }

        return outputGateIndex;
    }

    private List<Integer> getFOGateIndexes(List<FLTCCDDefaultGate> bottomUpGates, FLTCCDDefaultGate gate) {
        List<Integer> foGateIndexes = Lists.newArrayList();
        for (FLTCCDDefaultGate outputGate : Lists.reverse(bottomUpGates)) {
            if (outputGate.getType() == INPUT) {
                continue;
            }
            for (int i = 0; i < outputGate.getInputSize(); i++) {
                if (outputGate.getInputIndexAt(i) == gate.getIndex()) {
                    foGateIndexes.add(outputGate.getIndex());
                }
            }
        }

        return foGateIndexes;
    }
}
