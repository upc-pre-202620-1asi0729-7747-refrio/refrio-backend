package com.refrio.platform.alerting.application.commands;
public record ManageIncidentCommand(String alertId,String stage,String cause,String action,
                                    String decision,String verification,String subjectId,String audience) {}
