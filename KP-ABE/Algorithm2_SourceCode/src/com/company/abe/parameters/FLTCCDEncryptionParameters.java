package com.company.abe.parameters;

public class FLTCCDEncryptionParameters extends FLTCCDKeyParameters {
    private FLTCCDPublicKeyParameters publicKey;
    private String assignment;

    public FLTCCDEncryptionParameters(FLTCCDPublicKeyParameters publicKey, String assignment) {
        super(false, publicKey.getParameters());
        this.publicKey = publicKey;
        this.assignment = assignment;
    }

    public FLTCCDPublicKeyParameters getPublicKey() {
        return this.publicKey;
    }

    public String getAssignment()   {
        return this.assignment;
    }
}
