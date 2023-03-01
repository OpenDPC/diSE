package com.company.abe.parameters;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class FLTCCDKeyParameters extends AsymmetricKeyParameter {
    public FLTCCDParameters parameters;
    public FLTCCDKeyParameters(boolean isPrivate, FLTCCDParameters parameters) {
        super(isPrivate);

        this.parameters = parameters;
    }

    public FLTCCDParameters getParameters() {
        return parameters;
    }
}
