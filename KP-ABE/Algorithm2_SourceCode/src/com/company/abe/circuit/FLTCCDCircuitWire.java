package com.company.abe.circuit;

public class FLTCCDCircuitWire {
    private int inputGateIndex;
    private int outputGateIndex;

    public int getInputGateIndex() {
        return inputGateIndex;
    }

    public void setInputGateIndex(int inputGateIndex) {
        this.inputGateIndex = inputGateIndex;
    }

    public int getOutputGateIndex() {
        return outputGateIndex;
    }

    public void setOutputGateIndex(int outputGateIndex) {
        this.outputGateIndex = outputGateIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FLTCCDCircuitWire that = (FLTCCDCircuitWire) o;

        if (inputGateIndex != that.inputGateIndex) return false;
        return outputGateIndex == that.outputGateIndex;
    }

    @Override
    public int hashCode() {
        int result = inputGateIndex;
        result = 31 * result + outputGateIndex;
        return result;
    }
}
