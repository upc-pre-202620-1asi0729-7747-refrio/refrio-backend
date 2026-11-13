package com.refrio.platform.operations.application.ports;
/** Anti-corruption port: Operations must not own IAM account internals. */
public interface RecipientIdentityPort {
    boolean isExistingMinorista(String userId);
}
