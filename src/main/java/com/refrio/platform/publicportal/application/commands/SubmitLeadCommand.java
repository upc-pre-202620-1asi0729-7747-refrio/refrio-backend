package com.refrio.platform.publicportal.application.commands;
import jakarta.validation.constraints.*;
public record SubmitLeadCommand(@NotBlank @Size(max=120) String companyName,
    @NotBlank @Size(max=120) String contactName, @NotBlank @Email @Size(max=254) String email,
    @Size(max=32) String phone, @Size(max=32) String interestedPlan, @Size(max=1500) String notes) {}
