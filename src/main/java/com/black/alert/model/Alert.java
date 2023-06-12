package com.black.alert.model;




import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record Alert(
    UUID id,
    UUID productId,
    UUID userId,
    BigDecimal threshold,
    Set<String> tags,
    Instant createdDate,
    Instant lastModifiedDate) {
  public Alert {}
}
