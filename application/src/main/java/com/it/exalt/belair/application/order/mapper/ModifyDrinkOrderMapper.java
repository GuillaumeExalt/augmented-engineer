package com.it.exalt.belair.application.order.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderResponse;
import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.DrinkOrderLine;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;

public final class ModifyDrinkOrderMapper {
    public ModifyDrinkOrderCommand toCommand(DrinkOrder order, ModifyDrinkOrderRequest request) {
        List<DrinkOrderLine> updatedLines = updatedLines(order.lines(), request);
        return new ModifyDrinkOrderCommand(
                order,
                updatedLines,
                updatedDrinkTokenCost(updatedLines),
                updatedFoodTokenCost(updatedLines),
                request.soldeJetonsBoissonDisponibles(),
                request.soldeJetonsNourritureDisponibles()
        );
    }

    public ModifyDrinkOrderResponse toResponse(String orderId, ModifyDrinkOrderResult result, ModifyDrinkOrderCommand command) {
        if (result.hasChangeRequest()) {
            return new ModifyDrinkOrderResponse(
                    orderId,
                    "DEMANDE_CHANGEMENT_EN_ATTENTE",
                    toArticles(command.updatedLines())
            );
        }

        return new ModifyDrinkOrderResponse(
                orderId,
                result.order().status().name(),
                toArticles(result.order().lines())
        );
    }

    private List<ModifyDrinkOrderResponse.ArticleQuantity> toArticles(List<DrinkOrderLine> lines) {
        return lines.stream()
                .map(line -> new ModifyDrinkOrderResponse.ArticleQuantity(articleId(line.articleName()), line.quantity()))
                .toList();
    }

    private List<DrinkOrderLine> updatedLines(List<DrinkOrderLine> originalLines, ModifyDrinkOrderRequest request) {
        Map<String, Integer> quantitiesByArticleId = new LinkedHashMap<>();
        Map<String, String> articleNamesById = new HashMap<>();
        for (DrinkOrderLine originalLine : originalLines) {
            String articleId = articleId(originalLine.articleName());
            quantitiesByArticleId.put(articleId, originalLine.quantity());
            articleNamesById.put(articleId, originalLine.articleName());
        }
        applyChanges(quantitiesByArticleId, articleNamesById, request.ajouts(), 1);
        applyChanges(quantitiesByArticleId, articleNamesById, request.retraits(), -1);
        return quantitiesByArticleId.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new DrinkOrderLine(articleNamesById.get(entry.getKey()), entry.getValue()))
                .toList();
    }

    private void applyChanges(
            Map<String, Integer> quantitiesByArticleId,
            Map<String, String> articleNamesById,
            List<ModifyDrinkOrderRequest.ArticleChange> changes,
            int direction
    ) {
        if (changes == null) {
            return;
        }
        for (ModifyDrinkOrderRequest.ArticleChange change : changes) {
            if (change == null) {
                continue;
            }
            String articleId = change.id() == null ? "" : change.id();
            articleNamesById.putIfAbsent(articleId, articleName(articleId));
            quantitiesByArticleId.merge(articleId, direction * change.quantite(), Integer::sum);
        }
    }

    private int updatedDrinkTokenCost(List<DrinkOrderLine> updatedLines) {
        return updatedLines.stream()
                .filter(line -> "mojito".equals(articleId(line.articleName())))
                .mapToInt(DrinkOrderLine::quantity)
                .sum();
    }

    private int updatedFoodTokenCost(List<DrinkOrderLine> updatedLines) {
        return updatedLines.stream()
                .filter(line -> !"mojito".equals(articleId(line.articleName())))
                .mapToInt(DrinkOrderLine::quantity)
                .sum();
    }

    private String articleId(String articleName) {
        return articleName == null ? "" : articleName.toLowerCase(Locale.ROOT);
    }

    private String articleName(String articleId) {
        if (articleId == null || articleId.isBlank()) {
            return "";
        }
        return Character.toUpperCase(articleId.charAt(0)) + articleId.substring(1);
    }
}
