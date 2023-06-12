package com.black.alert.model;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record AlertRequest(UUID productId, UUID userId, BigDecimal threshold, Set<String> tags) {

  public AlertRequest {

    Assert.notNull(productId, "Product ID cannot be null");
    Assert.notNull(userId, "User ID cannot be null");
    Assert.notNull(threshold, "Threshold  cannot be null");
    Assert.notNull(tags, "Tags cannot be null");
  }
}
