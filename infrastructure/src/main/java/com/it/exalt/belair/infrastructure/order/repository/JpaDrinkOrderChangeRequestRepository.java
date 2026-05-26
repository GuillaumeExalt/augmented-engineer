package com.it.exalt.belair.infrastructure.order.repository;

import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import com.it.exalt.belair.domain.order.model.DrinkOrderChangeRequest;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderChangeRequestRepository;
import com.it.exalt.belair.infrastructure.order.mapper.DrinkOrderChangeRequestEntityMapper;

public final class JpaDrinkOrderChangeRequestRepository implements DrinkOrderChangeRequestRepository {
    private final EntityManagerFactory entityManagerFactory;
    private final Consumer<DrinkOrderChangeRequest> notificationPublisher;
    private final DrinkOrderChangeRequestEntityMapper mapper;

    public JpaDrinkOrderChangeRequestRepository(EntityManagerFactory entityManagerFactory) {
        this(entityManagerFactory, ignored -> { });
    }

    public JpaDrinkOrderChangeRequestRepository(
            EntityManagerFactory entityManagerFactory,
            Consumer<DrinkOrderChangeRequest> notificationPublisher
    ) {
        this(entityManagerFactory, notificationPublisher, new DrinkOrderChangeRequestEntityMapper());
    }

    public JpaDrinkOrderChangeRequestRepository(
            EntityManagerFactory entityManagerFactory,
            Consumer<DrinkOrderChangeRequest> notificationPublisher,
            DrinkOrderChangeRequestEntityMapper mapper
    ) {
        this.entityManagerFactory = entityManagerFactory;
        this.notificationPublisher = notificationPublisher;
        this.mapper = mapper;
    }

    @Override
    public void save(DrinkOrderChangeRequest changeRequest) {
        executeInTransaction(entityManager -> {
            entityManager.persist(mapper.toPendingEntity(changeRequest));
            return null;
        });
        notificationPublisher.accept(changeRequest);
    }

    private <T> T executeInTransaction(Function<EntityManager, T> work) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            T result = work.apply(entityManager);
            transaction.commit();
            return result;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }
}
