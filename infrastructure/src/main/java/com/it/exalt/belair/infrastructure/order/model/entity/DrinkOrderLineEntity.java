package com.it.exalt.belair.infrastructure.order.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "drink_order_lines")
public class DrinkOrderLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private DrinkOrderEntity order;

    @Column(name = "article_name", nullable = false)
    private String articleName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected DrinkOrderLineEntity() {
    }

    public DrinkOrderLineEntity(String articleName, int quantity) {
        this.articleName = articleName;
        this.quantity = quantity;
    }

    public String getArticleName() {
        return articleName;
    }

    public int getQuantity() {
        return quantity;
    }

    void attachTo(DrinkOrderEntity order) {
        this.order = order;
    }
}