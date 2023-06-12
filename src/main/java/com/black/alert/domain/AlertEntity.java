package com.black.alert.domain;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.black.alert.util.Constants.COLLECTION_ALERT;

@Document(collection = COLLECTION_ALERT)
public class AlertEntity implements Serializable {

  @CreatedBy private String createdBy;

  @CreatedDate private Instant createdDate;

  @Id private UUID id;

  @LastModifiedBy private String lastModifiedBy;

  @LastModifiedDate private Instant lastModifiedDate;

  private UUID productId;
  private BigDecimal threshold; // When this threshold is reached, an alert will be executed
  private UUID userId;

  private Set<String> tags;

  public AlertEntity() {
    this.tags = new HashSet<>();
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(final Instant createdDate) {
    this.createdDate = createdDate;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(final String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(final Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public UUID getProductId() {
    return productId;
  }

  public void setProductId(final UUID productId) {
    this.productId = productId;
  }

  public BigDecimal getThreshold() {
    return threshold;
  }

  public void setThreshold(final BigDecimal threshold) {
    this.threshold = threshold;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(final UUID userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "AlertEntity{"
        + "createdBy='"
        + createdBy
        + '\''
        + ", createdDate="
        + createdDate
        + ", id='"
        + id
        + '\''
        + ", lastModifiedBy='"
        + lastModifiedBy
        + '\''
        + ", lastModifiedDate="
        + lastModifiedDate
        + ", productId='"
        + productId
        + '\''
        + ", threshold="
        + threshold
        + ", tags="
        + tags
        + ", userId='"
        + userId
        + '\''
        + '}';
  }
}
