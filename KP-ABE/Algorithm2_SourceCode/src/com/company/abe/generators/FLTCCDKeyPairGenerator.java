package com.company.abe.generators;

import com.company.abe.parameters.FLTCCDKeyPairGenerationParameters;
import com.company.abe.parameters.FLTCCDMasterSecretKeyParameters;
import com.company.abe.parameters.FLTCCDParameters;
import com.company.abe.parameters.FLTCCDPublicKeyParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.junit.Assert;

public class FLTCCDKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    FLTCCDKeyPairGenerationParameters parameters;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.parameters = (FLTCCDKeyPairGenerationParameters) keyGenerationParameters;
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        FLTCCDParameters parameters = this.parameters.getParameters();
        Pairing pairing = parameters.getPairing();

        Assert.assertTrue(pairing.getG1().getOrder().isProbablePrime(99));
        Assert.assertTrue(pairing.getG2().getOrder().isProbablePrime(99));

        Element groupGenerator = pairing.getG1().newRandomElement().getImmutable();

        Element y = pairing.getZr().newRandomElement().getImmutable();
        int n = parameters.getN();

        Element[] ts = new Element[n];
        for(int i = 0; i < ts.length; ++i) {
            ts[i] = pairing.getZr().newRandomElement();
        }

        Element[] capitalTs = new Element[n];
        for(int i = 0; i < ts.length; ++i) {
            capitalTs[i] = groupGenerator.duplicate().powZn(ts[i]);
        }

        Element capitalY = pairing.pairing(groupGenerator.duplicate(), groupGenerator.duplicate()).powZn(y);

        return new AsymmetricCipherKeyPair(new FLTCCDPublicKeyParameters(parameters, capitalY, capitalTs, groupGenerator), new FLTCCDMasterSecretKeyParameters(parameters, y, ts));    }
}
