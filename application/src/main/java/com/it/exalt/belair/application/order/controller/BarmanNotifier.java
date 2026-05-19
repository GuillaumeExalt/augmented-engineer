package com.it.exalt.belair.application.order.controller;

public interface BarmanNotifier {
    void notifyPendingChangeRequest(String orderId);
}
