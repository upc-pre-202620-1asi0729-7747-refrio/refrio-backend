package com.refrio.platform.publicportal.application.commands;
public record SubmitLeadCommand(String companyName,String contactName,String email,String phone,
                               String interestedPlan,String notes) {}
