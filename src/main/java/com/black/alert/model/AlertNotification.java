package com.black.alert.model;


import org.apache.commons.lang3.builder.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AlertNotification(
    UUID id,
    UUID userId,
    UUID productId,
    UUID bundleId,
    String productName,
    String image,
    BigDecimal price,
    BigDecimal threshold,
    Instant createdDate,
    Instant lastModifiedDate) {

  public AlertNotification {}
}
