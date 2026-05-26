package com.it.exalt.belair.infrastructure.order.mapper;

import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.infrastructure.order.model.entity.DrinkOrderChangeRequestEntity;
import com.it.exalt.belair.infrastructure.order.model.entity.DrinkOrderChangeRequestLineEntity;

public final class DrinkOrderChangeRequestEntityMapper {
    private static final String PENDING_STATUS = "EN_ATTENTE";

    public DrinkOrderChangeRequestEntity toPendingEntity(DrinkOrderChangeRequest changeRequest) {
        DrinkOrderChangeRequestEntity entity = new DrinkOrderChangeRequestEntity(
                changeRequest.orderId(),
                PENDING_STATUS
        );
        entity.replaceRequestedLines(changeRequest.requestedLines().stream().map(this::toEntity).toList());
        return entity;
    }

    private DrinkOrderChangeRequestLineEntity toEntity(DrinkOrderLine line) {
        return new DrinkOrderChangeRequestLineEntity(line.articleName(), line.quantity());
    }
}
