package com.it.exalt.belair.infrastructure.order.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "drink_orders")
public class DrinkOrderEntity {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Column(name = "festivalier_id", nullable = false)
    private String festivalierId;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderColumn(name = "line_position")
    private List<DrinkOrderLineEntity> lines = new ArrayList<>();

    protected DrinkOrderEntity() {
    }

    public DrinkOrderEntity(String orderId, String festivalierId, String status) {
        this.orderId = orderId;
        this.festivalierId = festivalierId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getFestivalierId() {
        return festivalierId;
    }

    public String getStatus() {
        return status;
    }

    public List<DrinkOrderLineEntity> getLines() {
        return List.copyOf(lines);
    }

    public void updateStatus(String updatedStatus) {
        this.status = updatedStatus;
    }

    public void replaceLines(List<DrinkOrderLineEntity> newLines) {
        lines.clear();
        newLines.forEach(this::addLine);
    }

    private void addLine(DrinkOrderLineEntity line) {
        line.attachTo(this);
        lines.add(line);
    }
}