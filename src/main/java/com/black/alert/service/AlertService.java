package com.black.alert.service;

import com.black.alert.context.properties.ApplicationProperties;
import com.black.alert.domain.AlertEntity;
import com.black.alert.exeption.AlreadyExistsException;
import com.black.alert.exeption.DoesNotMatchException;
import com.black.alert.model.Alert;
import com.black.alert.model.AlertNotification;
import com.black.alert.model.AlertRequest;
import com.black.alert.model.ProductNotification;
import com.black.alert.repository.AlertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AlertService {

  private static final Logger LOGGER = LogManager.getLogger();

  private final AlertRepository alertRepository;
  private final ObjectMapper objectMapper;
  private final WebClient webClientProduct;

  public AlertService(
      final AlertRepository alertRepository,
      final ObjectMapper objectMapper,
      final ApplicationProperties properties,
      final WebClient.Builder webClientBuilder) {

    this.alertRepository = alertRepository;
    this.objectMapper = objectMapper;
    this.webClientProduct = webClientBuilder.baseUrl(properties.product().baseUrl()).build();
  }

  public Mono<Alert> add(final AlertRequest request) {
    Assert.notNull(request, "Alert request cannot be null");

    return alertRepository
        .existsByUserIdAndProductId(request.userId(), request.productId())
        .flatMap(
            alertExists -> {
              if (alertExists) {
                return Mono.error(new AlreadyExistsException("Such alert already exists"));
              }
              return userExists(request.userId());
            })
        .flatMap(
            userExists -> {
              if (!userExists) {
                return Mono.error(new NoSuchElementException("No such User found"));
              }
              return productExists(request.productId());
            })
        .flatMap(
            productExists -> {
              if (!productExists) {
                return Mono.error(new NoSuchElementException("No Such product found"));
              }
              final Instant now = Instant.now();
              final AlertEntity entity = new AlertEntity();
              entity.setId(UUID.randomUUID());
              entity.setUserId(request.userId());
              entity.setProductId(request.productId());
              entity.setTags(request.tags());
              entity.setThreshold(request.threshold());
              entity.setCreatedBy("system");
              entity.setCreatedDate(now);
              entity.setLastModifiedDate(now);
              return alertRepository.save(entity);
            })
        .doOnNext(entity -> LOGGER.info("Alert  added with [{}] ID", entity.getId()))
        .map(alertEntity -> objectMapper.convertValue(alertEntity, Alert.class));
  }

  public Mono<Alert> findById(final UUID id) {
    Assert.notNull(id, "Alert ID cannot be null");

    return alertRepository
        .findById(id)
        .map(entity -> objectMapper.convertValue(entity, Alert.class));
  }

  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "Alert ID cannot be null");

    return alertRepository
        .deleteById(id)
        .doOnSuccess(success -> LOGGER.info("Alert with [{}] ID is deleted", id));
  }

  public Mono<Alert> update(final UUID id, final AlertRequest partialUpdate) {

    return alertRepository
        .findById(id)
        .switchIfEmpty(
            Mono.error(
                new DoesNotMatchException(String.format("Alert ID [%s] does not match", id))))
        .map(alert -> {
           alert.setProductId(partialUpdate.productId());
           alert.setUserId(partialUpdate.userId());
           alert.setThreshold(partialUpdate.threshold());
           alert.setTags(partialUpdate.tags());
            return alertRepository.save(alert);
        })
        .map(alertEntity -> objectMapper.convertValue(alertEntity, Alert.class))
        .doOnSuccess(alert -> LOGGER.info("Following alert is updated: [{}]", partialUpdate));
  }

  public Flux<AlertNotification> findByUserIdAndSortedByDate(
      final UUID userId, final boolean sortByCreatedDate, final Sort.Direction type) {
    return alertRepository
        .sortingByCreatedDateOrLastModifiedDate(userId, sortByCreatedDate, type)
        .flatMap(alert -> Mono.just(alert).zipWith(fetchProduct(alert.getProductId())))
        .map(
            tuples -> {
              final AlertEntity alert = tuples.getT1();
              final ProductNotification product = tuples.getT2();
              return new AlertNotificationBuilder()
                  .id(alert.getId())
                  .userId(alert.getUserId())
                  .productId(alert.getProductId())
                  .bundleId(product.bundleId())
                  .productName(product.name())
                  .price(product.minPrice())
                  .threshold(alert.getThreshold())
                  .image(product.image())
                  .createdDate(alert.getCreatedDate())
                  .lastModifiedDate(alert.getLastModifiedDate())
                  .build();
            });
  }

  public Flux<AlertNotification> findByUserId(final UUID userId) {
    Assert.notNull(userId, "User ID cannot be null");

    return alertRepository
        .findByUserId(userId)
        .flatMap(alert -> Mono.just(alert).zipWith(fetchProduct(alert.getProductId())))
        .map(
            tuples -> {
              final AlertEntity alert = tuples.getT1();
              final ProductNotification product = tuples.getT2();
              return new AlertNotificationBuilder()
                  .id(alert.getId())
                  .userId(alert.getUserId())
                  .productId(alert.getProductId())
                  .bundleId(product.bundleId())
                  .productName(product.name())
                  .price(product.minPrice())
                  .image(product.image())
                  .threshold(alert.getThreshold())
                  .createdDate(alert.getCreatedDate())
                  .lastModifiedDate(alert.getLastModifiedDate())
                  .build();
            })
        .sort(Comparator.comparing(AlertNotification::createdDate).reversed());
  }

  public Mono<Boolean> existsByUserIdAndByProductId(final UUID userId, final UUID productId) {
    return alertRepository.existsByUserIdAndProductId(userId, productId);
  }

  private Mono<Boolean> userExists(final UUID userId) {
    return webClientProduct
        .get()
        .uri("/user/exists/{id}", userId)
        .retrieve()
        .bodyToMono(Boolean.class);
  }

  private Mono<Boolean> productExists(final UUID productId) {
    return webClientProduct
        .get()
        .uri("/products/{id}", productId)
        .retrieve()
        .bodyToMono(String.class)
        .map(response -> Boolean.TRUE)
        .onErrorResume(
            WebClientResponseException.class,
            ex ->
                ex.getRawStatusCode() == 404
                    ? Mono.just(Boolean.FALSE)
                    : Mono.error(new RuntimeException("Error on calling product service")));
  }

  private Mono<ProductNotification> fetchProduct(final UUID id) {
    return webClientProduct
        .get()
        .uri("/products/alert/{id}", id)
        .retrieve()
        .bodyToMono(ProductNotification.class);
  }

  public Mono<Void> all() {
    return alertRepository.deleteAll();
  }
}
