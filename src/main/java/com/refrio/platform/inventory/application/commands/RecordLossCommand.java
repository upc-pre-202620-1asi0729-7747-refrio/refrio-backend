package com.refrio.platform.inventory.application.commands;
public record RecordLossCommand(String batchId,int quantity,String cause) {}
