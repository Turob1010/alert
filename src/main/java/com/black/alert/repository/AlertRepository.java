package com.black.alert.repository;

import com.black.alert.domain.AlertEntity;
import com.black.alert.model.AlertAdvancedRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface AlertRepository
    extends AlertAdvancedRepository, ReactiveMongoRepository<AlertEntity, UUID> {

  Mono<Boolean> existsByUserIdAndProductId(UUID userId, UUID productId);

  Flux<AlertEntity> findByUserId(final UUID userId);

  Mono<Boolean> existsByUserId(final UUID userId);

  Mono<AlertEntity> findByProductIdAndUserIdAndThresholdGreaterThanEqual(
      UUID productId, UUID userId, BigDecimal threshold);

  Flux<AlertEntity> findByProductId(final UUID productId);
}
