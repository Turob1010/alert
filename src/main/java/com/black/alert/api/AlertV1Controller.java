package com.black.alert.api;

import com.black.alert.model.Alert;
import com.black.alert.model.AlertNotification;
import com.black.alert.model.AlertRequest;
import com.black.alert.service.AlertService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/main/alert")
public class AlertV1Controller {

  private static final Logger LOGGER = LogManager.getLogger();

  private final AlertService alertService;

  public AlertV1Controller(AlertService alertService) {
    this.alertService = alertService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Mono<Alert> create(@RequestBody final AlertRequest request) {
    return alertService.add(request);
  }

  @DeleteMapping("/{alertId}")
  Mono<Void> delete(@PathVariable final UUID alertId) {
    LOGGER.debug("Deleting a alert corresponding to {} ID", alertId);

    return alertService.delete(alertId);
  }

  @DeleteMapping("/")
  Mono<Void> all() {
    LOGGER.debug("Deleting a alert corresponding to");

    return alertService.all();
  }

  @GetMapping(path = "/exist/alert/{id}/{productId}")
  Mono<Boolean> existsIsAlert(@PathVariable final UUID id, @PathVariable final UUID productId) {
    return alertService.existsByUserIdAndByProductId(id, productId);
  }

  @GetMapping(path = "/api/{userId}")
  Flux<AlertNotification> findByUserIdAndSortByDate(
      @PathVariable final UUID userId,
      @RequestParam final boolean sortByCreatedDate,
      @RequestParam final Sort.Direction type) {
    return alertService.findByUserIdAndSortedByDate(userId, sortByCreatedDate, type);
  }

  @GetMapping("/{id}")
  Mono<Alert> findById(@PathVariable final UUID id) {
    return alertService.findById(id);
  }

  @GetMapping("/{userId}")
  Flux<AlertNotification> findByUserIdAndSortedByName(@PathVariable final UUID userId) {
    return alertService.findByUserId(userId);
  }

  @PutMapping("/update/alert/{id}")
  Mono<Alert> update(@PathVariable UUID id, @RequestBody AlertRequest partialUpdate) {
    return alertService.update(id, partialUpdate);
  }
}
