package com.company.abe.circuit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.company.abe.circuit.FLTCCDDefaultCircuit.FLTCCDGateType.KN;

public class FLTCCDDefaultCircuit {
    private int n;
    private int q;
    private int depth;
    private FLTCCDDefaultGate[] gates;
    private Map<FLTCCDCircuitWire, Integer> wireIndexMapping;

    public FLTCCDDefaultCircuit(int n, int q, int depth, FLTCCDDefaultGate[] gates) {
        this.n = n;
        this.q = q;
        this.depth = depth;
        this.gates = gates;
        FLTCCDDefaultGate[] arr$ = gates;
        int len$ = gates.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            FLTCCDDefaultGate gate = arr$[i$];
            gate.setCircuit(this);
        }

        Arrays.sort(gates, Comparator.comparingInt(FLTCCDDefaultGate::getIndex));
        List<FLTCCDDefaultGate> reversedGates = Lists.reverse(Lists.newArrayList(gates));
        Map<FLTCCDCircuitWire, Integer> wireIndexMapping = Maps.newHashMap();

        int currentIndex = 0;
        for (FLTCCDDefaultGate gate : reversedGates) {
            if (gate.getType() == FLTCCDGateType.INPUT) {
                continue;
            }
            for (int i = 0; i < gate.getInputSize(); i++) {
                FLTCCDCircuitWire circuitWire = new FLTCCDCircuitWire();
                circuitWire.setInputGateIndex(gate.getInputIndexAt(i));
                circuitWire.setOutputGateIndex(gate.getIndex());

                wireIndexMapping.put(circuitWire, currentIndex);

                currentIndex++;
            }
        }

        this.wireIndexMapping = wireIndexMapping;
    }

    public Integer getWireIndex(int inputGateIndex, int outputGateIndex) {
        FLTCCDCircuitWire circuitWire = new FLTCCDCircuitWire();
        circuitWire.setInputGateIndex(inputGateIndex);
        circuitWire.setOutputGateIndex(outputGateIndex);

        if (wireIndexMapping.containsKey(circuitWire)) {
            return wireIndexMapping.get(circuitWire);
        }

        return null;
    }

    public int getN() {
        return n;
    }

    public int getQ() {
        return q;
    }


    public int getDepth() {
        return depth;
    }

    public Iterator<FLTCCDDefaultGate> iterator() {
        return Arrays.asList(gates).iterator();
    }

    public FLTCCDDefaultGate getGateAt(int i) {
        return this.gates[i];
    }

    public FLTCCDDefaultGate getOutputGate() {
        return this.gates[this.n + this.q - 1];
    }

    public static class FLTCCDDefaultGate {
        private FLTCCDDefaultCircuit circuit;
        private FLTCCDGateType type;
        private int index;
        private int depth;
        private int[] inputs;
        private boolean value;
        private int k = -1;

        public FLTCCDDefaultGate(FLTCCDGateType type, int index, int depth) {
            this.type = type;
            this.index = index;
            this.depth = depth;
        }

        public FLTCCDDefaultGate(FLTCCDGateType type, int index, int depth, int[] inputs) {
            this.type = type;
            this.index = index;
            this.depth = depth;
            this.inputs = Arrays.copyOf(inputs, inputs.length);
        }

        public FLTCCDDefaultGate(FLTCCDGateType type, int index, int depth, int[] inputs, int k) {
            this.type = type;
            this.index = index;
            this.depth = depth;
            this.k = k;
            this.inputs = Arrays.copyOf(inputs, inputs.length);

            if (type == KN) {
                Assert.assertTrue(k > 0 && k <= inputs.length);
            }
        }

        public int getK() {
            return k;
        }

        public FLTCCDGateType getType() {
            return this.type;
        }

        public int getIndex() {
            return this.index;
        }

        public int getDepth() {
            return this.depth;
        }

        public int getInputIndexAt(int index) {
            return this.inputs[index];
        }

        public FLTCCDDefaultGate getInputAt(int index) {
            return this.circuit.getGateAt(this.getInputIndexAt(index));
        }

        public void set(boolean value) {
            this.value = value;
        }

        public boolean isSet() {
            return this.value;
        }

        public int getInputSize() {
            return this.inputs.length;
        }

        public FLTCCDDefaultGate evaluate() {
            switch(this.type) {
                case AND:
                    this.value = this.getInputAt(0).isSet() && this.getInputAt(1).isSet();
                    break;
                case OR:
                    this.value = this.getInputAt(0).isSet() || this.getInputAt(1).isSet();
                    break;
                default:
                    throw new IllegalStateException("Invalid type to be evaluated.");
            }

            return this;
        }

        public String toString() {
            return "Gate{type=" + this.type + ", index=" + this.index + ", depth=" + this.depth + ", inputs=" + Arrays.toString(this.inputs) + ", value=" + this.value + '}';
        }

        protected void setCircuit(FLTCCDDefaultCircuit circuit) {
            this.circuit = circuit;
        }
    }

    public enum FLTCCDGateType {
        INPUT,
        AND,
        OR,
        FO,
        KN
    }
}
