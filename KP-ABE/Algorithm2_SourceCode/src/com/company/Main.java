package com.company;

import com.company.abe.circuit.FLTCCDDefaultCircuit;
import com.company.abe.circuit.FLTCCDDefaultCircuit.FLTCCDDefaultGate;
import com.company.abe.generators.FLTCCDKeyPairGenerator;
import com.company.abe.generators.FLTCCDParametersGenerator;
import com.company.abe.generators.FLTCCDSecretKeyGenerator;
import com.company.abe.kem.FLTCCDKEMEngine;
import com.company.abe.kem.results.FLTCCDKEMEngineDecryptionResult;
import com.company.abe.kem.results.FLTCCDKEMEngineEncryptionResult;
import com.company.abe.parameters.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;

import java.security.SecureRandom;

import static com.company.abe.circuit.FLTCCDDefaultCircuit.FLTCCDGateType.*;
import static org.junit.Assert.*;

public class Main {

    private static final int rBits = 160;
    private static final int qBits = 512;

    private AsymmetricCipherKeyPair setup(int n) {
        FLTCCDKeyPairGenerator setup = new FLTCCDKeyPairGenerator();
        setup.init(new FLTCCDKeyPairGenerationParameters(
                new SecureRandom(),
                new FLTCCDParametersGenerator().init(
                        PairingFactory.getPairing(new TypeACurveGenerator(rBits, qBits).generate()), n
                ).generateParameters()
        ));
        return setup.generateKeyPair();
    }

    private FLTCCDKEMEngineEncryptionResult encaps(CipherParameters publicKey, String w) {
        FLTCCDKEMEngine kem = new FLTCCDKEMEngine(true, new FLTCCDEncryptionParameters((FLTCCDPublicKeyParameters) publicKey, w));

        return (FLTCCDKEMEngineEncryptionResult) kem.process();
    }

    public CipherParameters keyGen(CipherParameters publicKey, CipherParameters masterSecretKey, FLTCCDDefaultCircuit circuit, FLTCCDKEMEngineEncryptionResult encryptionResult) {
        FLTCCDSecretKeyGenerator keyGen = new FLTCCDSecretKeyGenerator();
        keyGen.init(new FLTCCDSecretKeyGenerationParameters(
                (FLTCCDPublicKeyParameters) publicKey,
                (FLTCCDMasterSecretKeyParameters) masterSecretKey,
                circuit, encryptionResult
        ));
        return keyGen.generateKey();
    }

    public Element decaps(CipherParameters secretKey, String w) {
        FLTCCDKEMEngine kem = new FLTCCDKEMEngine(false, new FLTCCDDecryptionParameters((FLTCCDSecretKeyParameters) secretKey, w));

        FLTCCDKEMEngineDecryptionResult decryptionResult = (FLTCCDKEMEngineDecryptionResult) kem.process();

        return decryptionResult.getKey();
    }

    public static void main(String[] args) {
        int n = 4;
        int q = 5;

        FLTCCDDefaultCircuit circuit = new FLTCCDDefaultCircuit(n, q, 3, new FLTCCDDefaultGate[]{
                new FLTCCDDefaultGate(INPUT, 0, 1),
                new FLTCCDDefaultGate(INPUT, 1, 1),
                new FLTCCDDefaultGate(INPUT, 2, 1),
                new FLTCCDDefaultGate(INPUT, 3, 1),

                new FLTCCDDefaultGate(FO, 4, 2, new int[]{2}),
                new FLTCCDDefaultGate(KN, 5, 3, new int[]{1, 4}, 1),
                new FLTCCDDefaultGate(KN, 6, 3, new int[]{3, 4}, 2),
                new FLTCCDDefaultGate(KN, 7, 4, new int[]{0, 5}, 2),
                new FLTCCDDefaultGate(KN, 8, 3, new int[]{6, 7}, 1)
        });

        Main main = new Main();

        // Setup phase
        AsymmetricCipherKeyPair keyPair = main.setup(n);

        String assignment = "1111";

        // Encryption phase
        FLTCCDKEMEngineEncryptionResult encryptionResult = main.encaps(keyPair.getPublic(), assignment);

        // Key Generation phase
        CipherParameters secretKey = main.keyGen(keyPair.getPublic(), keyPair.getPrivate(), circuit, encryptionResult);

        assignment = "1111";

        // Decryption phase
        assertEquals(encryptionResult.getYs(), main.decaps(secretKey, assignment));
    }
}
