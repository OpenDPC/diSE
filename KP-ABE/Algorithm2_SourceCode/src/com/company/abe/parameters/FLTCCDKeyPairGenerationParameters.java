package com.company.abe.parameters;

import org.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class FLTCCDKeyPairGenerationParameters extends KeyGenerationParameters {
    private FLTCCDParameters parameters;

    public FLTCCDKeyPairGenerationParameters(SecureRandom secureRandom, FLTCCDParameters parameters) {
        super(secureRandom, 0);

        this.parameters = parameters;
    }

    public FLTCCDParameters getParameters() {
        return parameters;
    }
}
