package com.it.exalt.belair.application.order.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderResponse;
import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;

public final class ModifyDrinkOrderHttpMapper {

    public ModifyDrinkOrderCommand toCommand(DrinkOrder order, ModifyDrinkOrderRequest request) {
        return new ModifyDrinkOrderCommand(
                order,
                updatedLines(order.lines(), request),
                tokenValue(request.couts(), true),
                tokenValue(request.couts(), false),
                tokenValue(request.soldes(), true),
                tokenValue(request.soldes(), false)
        );
    }

    public ModifyDrinkOrderResponse toAcceptedResponse(ModifyDrinkOrderResult result) {
        return new ModifyDrinkOrderResponse(result.order().orderId(), "DEMANDE_CHANGEMENT_EN_ATTENTE", null, null);
    }

    public ModifyDrinkOrderResponse toOkResponse(ModifyDrinkOrderResult result) {
        return new ModifyDrinkOrderResponse(
                result.order().orderId(),
                result.order().status().name(),
                result.order().lines().stream().map(this::toLineView).toList(),
                null
        );
    }

    public ModifyDrinkOrderResponse toErrorResponse(String message) {
        return new ModifyDrinkOrderResponse(null, null, null, message);
    }

    private ModifyDrinkOrderResponse.LineView toLineView(DrinkOrderLine line) {
        return new ModifyDrinkOrderResponse.LineView(line.articleName(), line.quantity());
    }

    private List<DrinkOrderLine> updatedLines(List<DrinkOrderLine> originalLines, ModifyDrinkOrderRequest request) {
        Map<String, Integer> quantities = new LinkedHashMap<>();
        for (DrinkOrderLine line : originalLines) {
            quantities.put(line.articleName(), line.quantity());
        }
        for (ModifyDrinkOrderRequest.LineChange retrait : lineChanges(request.retraits())) {
            quantities.computeIfPresent(retrait.article(), (ignored, quantity) -> quantity - retrait.quantite());
        }
        for (ModifyDrinkOrderRequest.LineChange ajout : lineChanges(request.ajouts())) {
            quantities.merge(ajout.article(), ajout.quantite(), Integer::sum);
        }
        return quantities.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new DrinkOrderLine(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<ModifyDrinkOrderRequest.LineChange> lineChanges(List<ModifyDrinkOrderRequest.LineChange> changes) {
        return changes == null ? List.of() : changes;
    }

    private int tokenValue(ModifyDrinkOrderRequest.TokenValues values, boolean drink) {
        if (values == null) {
            return 0;
        }
        return drink ? values.boisson() : values.nourriture();
    }
}
