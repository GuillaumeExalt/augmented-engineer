package com.it.exalt.belair.application.order.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest.ArticleDelta;
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderLinePayload;
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderPayload;
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderSuccessResponse;
import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;

public final class ModifyDrinkOrderMapper {

    public ModifyDrinkOrderCommand toCommand(DrinkOrder existingOrder, ModifyDrinkOrderRequest request) {
        List<DrinkOrderLine> updatedLines = applyArticleDeltas(existingOrder, request);
        return new ModifyDrinkOrderCommand(
                existingOrder,
                updatedLines,
                updatedDrinkTokenCost(updatedLines),
                0,
                request.availableDrinkTokens(),
                0
        );
    }

    public ModifyDrinkOrderSuccessResponse toSuccessResponse(String processingStatus, DrinkOrder order) {
        return new ModifyDrinkOrderSuccessResponse(
                processingStatus,
                new ModifyDrinkOrderPayload(
                        order.orderId(),
                        order.status().name(),
                        order.lines().stream()
                                .map(this::toLinePayload)
                                .toList()
                )
        );
    }

    private ModifyDrinkOrderLinePayload toLinePayload(DrinkOrderLine line) {
        return new ModifyDrinkOrderLinePayload(line.articleName(), line.quantity());
    }

    private List<DrinkOrderLine> applyArticleDeltas(DrinkOrder existingOrder, ModifyDrinkOrderRequest request) {
        Map<String, Integer> updatedQuantities = new LinkedHashMap<>();
        for (DrinkOrderLine line : existingOrder.lines()) {
            updatedQuantities.put(line.articleName(), line.quantity());
        }
        for (ArticleDelta retrait : request.retraits()) {
            updatedQuantities.computeIfPresent(retrait.article(), (article, quantity) -> quantity - retrait.quantite());
        }
        for (ArticleDelta ajout : request.ajouts()) {
            updatedQuantities.merge(ajout.article(), ajout.quantite(), Integer::sum);
        }
        return updatedQuantities.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new DrinkOrderLine(entry.getKey(), entry.getValue()))
                .toList();
    }

    private int updatedDrinkTokenCost(List<DrinkOrderLine> updatedLines) {
        return updatedLines.stream()
                .mapToInt(DrinkOrderLine::quantity)
                .sum();
    }
}
