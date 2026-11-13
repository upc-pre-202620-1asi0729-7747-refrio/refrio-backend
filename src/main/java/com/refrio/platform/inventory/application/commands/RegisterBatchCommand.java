package com.refrio.platform.inventory.application.commands;
public record RegisterBatchCommand(String productId,int quantity,String receptionDate,String expirationDate,String supplier,String location,String weight,String notes) {}
