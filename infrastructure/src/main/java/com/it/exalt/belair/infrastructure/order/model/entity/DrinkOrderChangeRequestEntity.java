package com.it.exalt.belair.infrastructure.order.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "drink_order_change_requests")
public class DrinkOrderChangeRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "changeRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderColumn(name = "line_position")
    private List<DrinkOrderChangeRequestLineEntity> requestedLines = new ArrayList<>();

    protected DrinkOrderChangeRequestEntity() {
    }

    public DrinkOrderChangeRequestEntity(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public List<DrinkOrderChangeRequestLineEntity> getRequestedLines() {
        return List.copyOf(requestedLines);
    }

    public void replaceRequestedLines(List<DrinkOrderChangeRequestLineEntity> newRequestedLines) {
        requestedLines.clear();
        newRequestedLines.forEach(this::addRequestedLine);
    }

    private void addRequestedLine(DrinkOrderChangeRequestLineEntity requestedLine) {
        requestedLine.attachTo(this);
        requestedLines.add(requestedLine);
    }
}
