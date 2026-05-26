package com.it.exalt.belair.application.order.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it.exalt.belair.application.order.mapper.ModifyDrinkOrderMapper;
import com.it.exalt.belair.application.order.model.in.ModifyDrinkOrderRequest;
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderErrorResponse;
import com.it.exalt.belair.domain.order.model.InsufficientFestivalierTokenBalanceException;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.domain.order.usecase.ModifyDrinkOrderUseCase;

@RestController
@RequestMapping(path = "/commandes", produces = MediaType.APPLICATION_JSON_VALUE)
public final class ModifyDrinkOrderController {
    private final ModifyDrinkOrderUseCase useCase;
    private final DrinkOrderRepository drinkOrderRepository;
    private final ModifyDrinkOrderMapper mapper;

    public ModifyDrinkOrderController(
            ModifyDrinkOrderUseCase useCase,
            DrinkOrderRepository drinkOrderRepository
    ) {
        this(useCase, drinkOrderRepository, new ModifyDrinkOrderMapper());
    }

    public ModifyDrinkOrderController(
            ModifyDrinkOrderUseCase useCase,
            DrinkOrderRepository drinkOrderRepository,
            ModifyDrinkOrderMapper mapper
    ) {
        this.useCase = useCase;
        this.drinkOrderRepository = drinkOrderRepository;
        this.mapper = mapper;
    }

    @PatchMapping(path = "/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchCommande(
            @PathVariable("orderId") String orderId,
            @RequestHeader(name = "X-Festivalier-Id", required = false) String authenticatedFestivalierId,
            @RequestBody ModifyDrinkOrderRequest request
    ) {
        try {
            ModifyDrinkOrderResult result = useCase.handle(mapper.toCommand(
                    drinkOrderRepository.findById(orderId).orElseThrow(),
                    request
            ));

            if (result.hasChangeRequest()) {
                return ResponseEntity.accepted()
                        .body(mapper.toSuccessResponse("DEMANDE_CHANGEMENT_EN_ATTENTE", result.order()));
            }

            return ResponseEntity.ok(mapper.toSuccessResponse("MODIFICATION_APPLIQUEE", result.order()));
        } catch (InsufficientFestivalierTokenBalanceException exception) {
            return ResponseEntity.badRequest()
                    .body(new ModifyDrinkOrderErrorResponse(
                            exception.getMessage(),
                            "Le cout modifie depasse les soldes disponibles"
                    ));
        }
    }
}
