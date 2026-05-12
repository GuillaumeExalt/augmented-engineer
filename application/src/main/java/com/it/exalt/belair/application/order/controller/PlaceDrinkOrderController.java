package com.it.exalt.belair.application.order.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it.exalt.belair.application.order.model.in.PlaceDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.out.PlaceDrinkOrderResponse;
import com.it.exalt.belair.domain.order.model.CreatedOrder;
import com.it.exalt.belair.domain.order.model.PlaceDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.RequestedItem;
import com.it.exalt.belair.domain.order.usecase.PlaceDrinkOrderUseCase;

@RestController
@RequestMapping(path = "/commandes", produces = MediaType.APPLICATION_JSON_VALUE)
public final class PlaceDrinkOrderController {
    private final PlaceDrinkOrderUseCase useCase;

    public PlaceDrinkOrderController(PlaceDrinkOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlaceDrinkOrderResponse> postCommandes(
            @RequestHeader(name = "X-Festivalier-Id", required = false) String authenticatedFestivalierId,
            @RequestAttribute(name = "availableArticles", required = false) Map<String, Integer> availableArticles,
            @RequestBody(required = false) PlaceDrinkOrderRequest request
    ) {
        if (authenticatedFestivalierId == null || authenticatedFestivalierId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new PlaceDrinkOrderResponse("", "NON_AUTHENTIFIE"));
        }

        if (request == null || request.articles() == null || request.articles().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new PlaceDrinkOrderResponse("", "REQUETE_INVALIDE"));
        }

        PlaceDrinkOrderRequest.RequestedArticle requestedArticle = request.articles().get(0);
        CreatedOrder createdOrder = useCase.handle(new PlaceDrinkOrderCommand(
                authenticatedFestivalierId,
                new HashMap<>(availableArticles == null ? Map.of() : availableArticles),
                new RequestedItem(toCatalogArticleName(requestedArticle.id()), requestedArticle.quantite())
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PlaceDrinkOrderResponse(createdOrder.orderId(), createdOrder.status().name()));
    }

    private String toCatalogArticleName(String articleId) {
        String normalizedId = articleId == null ? "" : articleId.trim();
        if (normalizedId.isEmpty()) {
            return normalizedId;
        }

        StringBuilder catalogArticleName = new StringBuilder();
        for (String segment : normalizedId.split("-")) {
            if (segment.isBlank()) {
                continue;
            }
            if (!catalogArticleName.isEmpty()) {
                catalogArticleName.append(' ');
            }
            catalogArticleName
                    .append(Character.toUpperCase(segment.charAt(0)))
                    .append(segment.substring(1).toLowerCase(Locale.ROOT));
        }
        return catalogArticleName.toString();
    }
}