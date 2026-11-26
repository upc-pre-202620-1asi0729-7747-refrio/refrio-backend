package com.refrio.platform.publicportal;

import com.refrio.platform.publicportal.interfaces.rest.LeadsController;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LeadsControllerValidationTest {
    @Test void invalidEmailAndLongCompanyAreRejected() {
        try(var factory=Validation.buildDefaultValidatorFactory()) {
            var validator=factory.getValidator();
            var request=new LeadsController.LeadRequest("a".repeat(121),"Ana","invalid-email","","Basico","");
            assertFalse(validator.validate(request).isEmpty());
        }
    }
}
