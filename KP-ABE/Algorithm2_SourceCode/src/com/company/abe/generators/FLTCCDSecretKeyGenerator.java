package com.company.abe.generators;

import com.company.abe.circuit.FLTCCDDefaultCircuit;
import com.company.abe.parameters.FLTCCDMasterSecretKeyParameters;
import com.company.abe.parameters.FLTCCDPublicKeyParameters;
import com.company.abe.parameters.FLTCCDSecretKeyGenerationParameters;
import com.company.abe.parameters.FLTCCDSecretKeyParameters;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.company.abe.circuit.FLTCCDDefaultCircuit.*;
import static com.google.common.collect.Lists.*;

public class FLTCCDSecretKeyGenerator {
    private FLTCCDSecretKeyGenerationParameters params;
    private Pairing pairing;
    private FLTCCDDefaultCircuit circuit;

    public FLTCCDSecretKeyGenerator() {
    }

    public void init(KeyGenerationParameters params) {
        this.params = (FLTCCDSecretKeyGenerationParameters)params;
        this.pairing = this.params.getMasterSecretKeyParameters().getParameters().getPairing();
        this.circuit = this.params.getCircuit();
    }

    public CipherParameters generateKey() {
        FLTCCDMasterSecretKeyParameters msk = this.params.getMasterSecretKeyParameters();
        FLTCCDPublicKeyParameters pp = this.params.getPublicKeyParameters();

        FLTCCDDefaultCircuit circuit = this.circuit;

        // create S and P mapping
        final Map<Integer, List<Element>> s = new HashMap<>();
        final Map<Integer, List<Element>> p = new HashMap<>();


        // Put y to the output gate for the S mapping
        Element y = msk.getY();

        // Parse the gates in top-down order
        List<FLTCCDDefaultGate> topDownGates = reverse(newArrayList(circuit.iterator()));
        for (FLTCCDDefaultGate gate : topDownGates) {
            switch (gate.getType()) {
                case OR: {
                    List<Element> elements = getSimpleGateElements(s, y, topDownGates, gate);

                    s.put(this.circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex()), elements);
                    s.put(this.circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex()), elements);

                    break;
                }
                case AND: {
                    List<Element> elements = getSimpleGateElements(s, y, topDownGates, gate);

                    List<Element> l1 = Lists.newArrayList();
                    List<Element> l2 = Lists.newArrayList();

                    for (Element element : elements) {
                        Element x1 = pairing.getZr()
                                .newRandomElement();
                        Element x2 = pairing.getZr()
                                .newElement(x1.duplicate().toBigInteger().negate().add(element.toBigInteger()));

                        l1.add(x1);
                        l2.add(x2);
                    }

                    s.put(this.circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex()), l1);
                    s.put(this.circuit.getWireIndex(gate.getInputIndexAt(1), gate.getIndex()), l2);

                    break;
                }
                case FO: {
                    Map<Integer, List<Element>> elements = getFOGateElements(s, y, topDownGates, gate);
                    List<Element> sElements = Lists.newArrayList();

                    for (Map.Entry<Integer, List<Element>> entry : elements.entrySet()) {
                        List<Element> pElements = Lists.newArrayList();
                        for (Element element : entry.getValue()) {
                            Element x1 = pairing.getZr()
                                    .newRandomElement();
                            Element x2 = pairing.getZr()
                                    .newElement(x1.duplicate().toBigInteger().negate().add(element.toBigInteger()));

                            sElements.add(x1);
                            pElements.add(params.getPublicKeyParameters().getGroupGenerator().duplicate().powZn(x2));
                        }

                        p.put(entry.getKey(), pElements);
                    }

                    s.put(this.circuit.getWireIndex(gate.getInputIndexAt(0), gate.getIndex()), sElements);

                    break;
                }
                case KN: {
                    List<Element> inputElements = getSimpleGateElements(s, y, topDownGates, gate);

                    for (int i = 0; i < gate.getInputSize(); i++) {
                        s.put(this.circuit.getWireIndex(gate.getInputIndexAt(i), gate.getIndex()), Lists.newArrayList());
                    }

                    for (Element inputElement : inputElements) {
                        // generate polynomial of K degree
                        // The polynomial has the following form: a0 + a_1 * x + ... + a_(k-1) * x^(k-1)
                        final List<Element> polynomial = Lists.newArrayList();
                        IntStream.range(0, gate.getK())
                                .forEach(value -> polynomial.add(pairing.getZr().newRandomElement()));
                        polynomial.set(0, inputElement);

                        List<Element> elements = IntStream.range(1, gate.getInputSize() + 1)
                                .mapToObj(value -> evaluatePolynomial(polynomial, value))
                                .collect(Collectors.toList());

                        for (int j = 0; j < gate.getInputSize(); j++) {
                            s.get(this.circuit.getWireIndex(gate.getInputIndexAt(j), gate.getIndex())).add(elements.get(j));
                        }
                    }

                    break;
                }
                case INPUT: {

                    break;
                }
                default: break;
            }
        }

        Map<Integer, List<Element>> d = Maps.newHashMap();
        for (int i = 0; i < circuit.getN(); i++) {
            d.put(i, Lists.newArrayList());
            List<Element> elements = getSimpleGateElements(s, y, topDownGates, this.circuit.getGateAt(i));

            for (Element element : elements) {
                Element dElement = params.getPublicKeyParameters()
                        .getGroupGenerator()
                        .duplicate()
                        .powZn(element.div(params.getMasterSecretKeyParameters().getTAt(i)));

                d.get(i).add(dElement);
            }
        }

        return new FLTCCDSecretKeyParameters(pp.getParameters(), circuit, d, p, params.getEncryptionResult());
    }

    private Element getBetaProduct(int value, int k) {
        Element element = pairing.getZr().newOneElement();
        Element zeroElement = pairing.getZr().newZeroElement();
        Element xiElement = pairing.getZr().newElement(value);
        for (int i = 1; i <= k; i++) {
            if (value == i) {
                continue;
            }
            Element xjElement = pairing.getZr().newElement(i);
            element.mul(zeroElement
                            .duplicate()
                            .sub(xjElement)
                            .div(xiElement.duplicate().sub(xjElement))
                    );
        }

        return element.duplicate();
    }

    private Element evaluatePolynomial(List<Element> polynomial, int value) {
        Element element = pairing.getZr().newZeroElement();
        for (int i = 0; i < polynomial.size(); i++) {
            Element elementValue = pairing.getZr().newElement(value).powZn(pairing.getZr().newElement(i));
            element = element.add(polynomial.get(i).duplicate().mul(elementValue));
        }

        return element;
    }

    private List<Element> getSimpleGateElements(Map<Integer, List<Element>> s, Element y, List<FLTCCDDefaultGate> topDownGates, FLTCCDDefaultGate gate) {
        List<Element> elements = Lists.newArrayList();
        if (gate.getIndex() == this.circuit.getOutputGate().getIndex()) {
            elements = Lists.newArrayList(y);
        } else {
            for (FLTCCDDefaultGate outputGate : topDownGates) {
                if (outputGate.getType() == FLTCCDGateType.INPUT) {
                    continue;
                }
                for (int i = 0; i < outputGate.getInputSize(); i++) {
                    if (outputGate.getInputIndexAt(i) == gate.getIndex()) {
                        elements = s.get(this.circuit.getWireIndex(gate.getIndex(), outputGate.getIndex()));
                        break;
                    }
                }
            }
        }
        return elements;
    }

    private Map<Integer, List<Element>> getFOGateElements(Map<Integer, List<Element>> s, Element y, List<FLTCCDDefaultGate> topDownGates, FLTCCDDefaultGate gate) {
        Map<Integer, List<Element>> elements = Maps.newHashMap();

        if (gate.getIndex() == this.circuit.getOutputGate().getIndex()) {
            Assert.fail();
        } else {
            for (FLTCCDDefaultGate outputGate : topDownGates) {
                if (outputGate.getType() == FLTCCDGateType.INPUT) {
                    continue;
                }
                for (int i = 0; i < outputGate.getInputSize(); i++) {
                    if (outputGate.getInputIndexAt(i) == gate.getIndex()) {
                        elements.put(this.circuit.getWireIndex(gate.getIndex(), outputGate.getIndex()), s.get(this.circuit.getWireIndex(gate.getIndex(), outputGate.getIndex())));
                    }
                }
            }
        }

        return elements;
    }
}
