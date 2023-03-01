package com.company.abe.parameters;

public class FLTCCDDecryptionParameters extends FLTCCDKeyParameters {
    private FLTCCDSecretKeyParameters secretKey;
    private String assignment;

    public FLTCCDDecryptionParameters(FLTCCDSecretKeyParameters secretKey, String assignment) {
        super(true, secretKey.getParameters());
        this.secretKey = secretKey;
        this.assignment = assignment;
    }

    public FLTCCDSecretKeyParameters getSecretKey() {
        return secretKey;
    }

    public String getAssignment() {
        return assignment;
    }
}
