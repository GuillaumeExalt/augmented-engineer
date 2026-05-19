package com.it.exalt.belair.domain.order.port.out;

import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;

public interface DrinkOrderChangeRequestRepository {
    void save(DrinkOrderChangeRequest changeRequest);
}
