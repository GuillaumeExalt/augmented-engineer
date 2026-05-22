package com.it.exalt.belair.application.order.controller;

public interface ModifyDrinkOrderNotificationPublisher {
    void notifyBarman(String orderId);
}
