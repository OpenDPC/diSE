package com.company.abe.generators;

import com.company.abe.parameters.FLTCCDKeyPairGenerationParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class FLTCCDAsymmetricCipherKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private FLTCCDKeyPairGenerationParameters params;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {

    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        return null;
    }
}
