package com.company.abe.parameters;

import com.company.abe.circuit.FLTCCDDefaultCircuit;
import com.company.abe.kem.results.FLTCCDKEMEngineEncryptionResult;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class FLTCCDSecretKeyGenerationParameters extends KeyGenerationParameters{
    private FLTCCDPublicKeyParameters publicKeyParameters;
    private FLTCCDMasterSecretKeyParameters masterSecretKeyParameters;
    private FLTCCDDefaultCircuit circuit;
    private FLTCCDKEMEngineEncryptionResult encryptionResult;

    public FLTCCDSecretKeyGenerationParameters(FLTCCDPublicKeyParameters publicKeyParameters,
                                               FLTCCDMasterSecretKeyParameters masterSecretKeyParameters,
                                               FLTCCDDefaultCircuit circuit,
                                               FLTCCDKEMEngineEncryptionResult encryptionResult) {
        super(null, 0);
        this.publicKeyParameters = publicKeyParameters;
        this.masterSecretKeyParameters = masterSecretKeyParameters;
        this.circuit = circuit;
        this.encryptionResult = encryptionResult;
    }

    public FLTCCDPublicKeyParameters getPublicKeyParameters() {
        return this.publicKeyParameters;
    }

    public FLTCCDMasterSecretKeyParameters getMasterSecretKeyParameters() {
        return this.masterSecretKeyParameters;
    }

    public FLTCCDDefaultCircuit getCircuit() {
        return this.circuit;
    }

    public FLTCCDKEMEngineEncryptionResult getEncryptionResult() {
        return encryptionResult;
    }
}
