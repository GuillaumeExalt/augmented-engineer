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
import com.it.exalt.belair.application.order.model.out.ModifyDrinkOrderResponse;
import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.InsufficientFestivalierTokenBalanceException;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderCommand;
import com.it.exalt.belair.domain.order.model.ModifyDrinkOrderResult;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.domain.order.usecase.ModifyDrinkOrderUseCase;

@RestController
@RequestMapping(path = "/commandes", produces = MediaType.APPLICATION_JSON_VALUE)
public final class ModifyDrinkOrderController {
    private final ModifyDrinkOrderUseCase useCase;
    private final DrinkOrderRepository orderRepository;
    private final ModifyDrinkOrderNotificationPublisher notificationPublisher;
    private final ModifyDrinkOrderMapper mapper;

    public ModifyDrinkOrderController(
            ModifyDrinkOrderUseCase useCase,
            DrinkOrderRepository orderRepository,
            ModifyDrinkOrderNotificationPublisher notificationPublisher
    ) {
        this(useCase, orderRepository, notificationPublisher, new ModifyDrinkOrderMapper());
    }

    ModifyDrinkOrderController(
            ModifyDrinkOrderUseCase useCase,
            DrinkOrderRepository orderRepository,
            ModifyDrinkOrderNotificationPublisher notificationPublisher,
            ModifyDrinkOrderMapper mapper
    ) {
        this.useCase = useCase;
        this.orderRepository = orderRepository;
        this.notificationPublisher = notificationPublisher;
        this.mapper = mapper;
    }

    @PatchMapping(path = "/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchOrder(
            @PathVariable("orderId") String orderId,
            @RequestHeader("X-Festivalier-Id") String festivalierId,
            @RequestBody ModifyDrinkOrderRequest request
    ) {
        DrinkOrder order = orderRepository.findById(orderId).orElseThrow();
        ModifyDrinkOrderCommand command = mapper.toCommand(order, request);

        try {
            ModifyDrinkOrderResult result = useCase.handle(command);
            ModifyDrinkOrderResponse response = mapper.toResponse(orderId, result, command);
            if (result.hasChangeRequest()) {
                notificationPublisher.notifyBarman(orderId);
                return ResponseEntity.accepted().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (InsufficientFestivalierTokenBalanceException exception) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ModifyDrinkOrderErrorResponse(exception.getMessage()));
        }
    }
}
