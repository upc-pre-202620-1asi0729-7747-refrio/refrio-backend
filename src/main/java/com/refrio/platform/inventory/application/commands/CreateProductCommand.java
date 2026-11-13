package com.refrio.platform.inventory.application.commands;
public record CreateProductCommand(String name,String category,String warehouse,String expirationDate,double initialTemperature) {}
