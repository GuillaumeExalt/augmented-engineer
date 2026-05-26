package com.it.exalt.belair.infrastructure.order.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import com.it.exalt.belair.domain.order.model.DrinkOrder;
import com.it.exalt.belair.domain.order.model.OrderStatus;
import com.it.exalt.belair.domain.order.port.out.DrinkOrderRepository;
import com.it.exalt.belair.infrastructure.order.mapper.DrinkOrderEntityMapper;
import com.it.exalt.belair.infrastructure.order.model.entity.DrinkOrderEntity;

public final class JpaDrinkOrderRepository implements DrinkOrderRepository {

    private final EntityManagerFactory entityManagerFactory;
    private final DrinkOrderEntityMapper mapper;

    public JpaDrinkOrderRepository(EntityManagerFactory entityManagerFactory) {
        this(entityManagerFactory, new DrinkOrderEntityMapper());
    }

    public JpaDrinkOrderRepository(EntityManagerFactory entityManagerFactory, DrinkOrderEntityMapper mapper) {
        this.entityManagerFactory = entityManagerFactory;
        this.mapper = mapper;
    }

    @Override
    public void save(DrinkOrder order) {
        executeInTransaction(entityManager -> {
            DrinkOrderEntity existingOrder = entityManager.find(DrinkOrderEntity.class, order.orderId());
            if (existingOrder == null) {
                entityManager.persist(mapper.toEntity(order));
                return null;
            }

            DrinkOrderEntity mappedOrder = mapper.toEntity(order);
            existingOrder.updateStatus(mappedOrder.getStatus());
            existingOrder.replaceLines(mappedOrder.getLines());
            return null;
        });
    }

    @Override
    public Optional<DrinkOrder> findById(String orderId) {
        return executeRead(entityManager -> Optional.ofNullable(entityManager.find(DrinkOrderEntity.class, orderId))
                .map(mapper::toDomain));
    }

    @Override
    public void updateStatus(String orderId, OrderStatus status) {
        executeInTransaction(entityManager -> {
            DrinkOrderEntity order = entityManager.find(DrinkOrderEntity.class, orderId);
            if (order != null) {
                order.updateStatus(status.name());
            }
            return null;
        });
    }

    @Override
    public List<DrinkOrder> findByFestivalierIdAndStatus(String festivalierId, OrderStatus status) {
        return executeRead(entityManager -> entityManager.createQuery(
                        "select distinct o from DrinkOrderEntity o left join fetch o.lines where o.festivalierId = :festivalierId and o.status = :status order by o.orderId",
                        DrinkOrderEntity.class
                )
                .setParameter("festivalierId", festivalierId)
                .setParameter("status", status.name())
                .getResultList()
                .stream()
                .map(mapper::toDomain)
                .toList());
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

    private <T> T executeRead(Function<EntityManager, T> work) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            return work.apply(entityManager);
        } finally {
            entityManager.close();
        }
    }
}