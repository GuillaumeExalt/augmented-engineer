package com.it.exalt.belair.application.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it.exalt.belair.application.order.mapper.ModifyDrinkOrderHttpMapper;
import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderResponse;
import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.InsufficientFestivalierTokenBalanceException;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.domain.order.usecase.ModifyDrinkOrderUseCase;

@RestController
@RequestMapping(path = "/commandes", produces = MediaType.APPLICATION_JSON_VALUE)
public final class ModifyDrinkOrderController {
    private final ModifyDrinkOrderUseCase useCase;
    private final DrinkOrderRepository drinkOrderRepository;
    private final ModifyDrinkOrderHttpMapper mapper;
    private final BarmanNotifier barmanNotifier;

    public ModifyDrinkOrderController(
            ModifyDrinkOrderUseCase useCase,
            DrinkOrderRepository drinkOrderRepository,
            BarmanNotifier barmanNotifier
    ) {
        this.useCase = useCase;
        this.drinkOrderRepository = drinkOrderRepository;
        this.mapper = new ModifyDrinkOrderHttpMapper();
        this.barmanNotifier = barmanNotifier;
    }

    @PatchMapping(path = "/{commandeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModifyDrinkOrderResponse> patchCommande(
            @PathVariable("commandeId") String commandeId,
            @RequestHeader(value = "X-Festivalier-Id", required = false) String festivalierId,
            @RequestBody ModifyDrinkOrderRequest request
    ) {
        if (festivalierId == null || festivalierId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapper.toErrorResponse("NON_AUTHENTIFIE"));
        }

        DrinkOrder order = drinkOrderRepository.findById(commandeId)
                .orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapper.toErrorResponse("COMMANDE_INTROUVABLE"));
        }

        if (!festivalierId.equals(order.festivalierId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(mapper.toErrorResponse("ACCES_INTERDIT"));
        }

        if (hasInvalidLineChanges(request)) {
            return ResponseEntity.badRequest()
                    .body(mapper.toErrorResponse("LIGNE_INVALIDE: article requis et quantite strictement positive"));
        }

        try {
            ModifyDrinkOrderResult result = useCase.handle(mapper.toCommand(order, request));
            if (result.hasChangeRequest()) {
                barmanNotifier.notifyPendingChangeRequest(result.order().orderId());
                return ResponseEntity.accepted().body(mapper.toAcceptedResponse(result));
            }
            return ResponseEntity.ok(mapper.toOkResponse(result));
        } catch (InsufficientFestivalierTokenBalanceException exception) {
            return ResponseEntity.badRequest().body(mapper.toErrorResponse(exception.getMessage()));
        }
    }

    private boolean hasInvalidLineChanges(ModifyDrinkOrderRequest request) {
        return request == null
                || hasInvalidLineChanges(request.ajouts())
                || hasInvalidLineChanges(request.retraits());
    }

    private boolean hasInvalidLineChanges(List<ModifyDrinkOrderRequest.LineChange> changes) {
        if (changes == null) {
            return false;
        }
        return changes.stream().anyMatch(change -> change == null
                || change.article() == null
                || change.article().isBlank()
                || change.quantite() <= 0);
    }
}
