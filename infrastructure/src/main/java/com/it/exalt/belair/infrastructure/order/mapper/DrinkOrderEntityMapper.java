package com.it.exalt.belair.infrastructure.order.mapper;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.infrastructure.order.model.entity.DrinkOrderEntity;
import com.it.exalt.belair.infrastructure.order.model.entity.DrinkOrderLineEntity;

public final class DrinkOrderEntityMapper {

    public DrinkOrderEntity toEntity(DrinkOrder order) {
        DrinkOrderEntity entity = new DrinkOrderEntity(
                order.orderId(),
                order.festivalierId(),
                order.status().name()
        );
        entity.replaceLines(order.lines().stream().map(this::toEntity).toList());
        return entity;
    }

    public DrinkOrder toDomain(DrinkOrderEntity entity) {
        return new DrinkOrder(
                entity.getOrderId(),
                entity.getFestivalierId(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getLines().stream().map(this::toDomain).toList()
        );
    }

    private DrinkOrderLineEntity toEntity(DrinkOrderLine line) {
        return new DrinkOrderLineEntity(line.articleName(), line.quantity());
    }

    private DrinkOrderLine toDomain(DrinkOrderLineEntity entity) {
        return new DrinkOrderLine(entity.getArticleName(), entity.getQuantity());
    }
}