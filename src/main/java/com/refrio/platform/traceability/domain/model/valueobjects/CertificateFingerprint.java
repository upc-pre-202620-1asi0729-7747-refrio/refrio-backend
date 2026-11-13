package com.refrio.platform.traceability.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/** SHA-256 integrity checksum. This is not a qualified digital signature. */
public record CertificateFingerprint(String hex) {
    public CertificateFingerprint {
        if(hex==null||!hex.matches("[a-f0-9]{64}"))throw new DomainException("Huella SHA-256 no válida");
    }
    public static CertificateFingerprint of(String material){
        try {return new CertificateFingerprint(HexFormat.of().formatHex(
            MessageDigest.getInstance("SHA-256").digest(material.getBytes(StandardCharsets.UTF_8))));}
        catch(Exception e){throw new IllegalStateException("SHA-256 unavailable",e);}
    }
}
