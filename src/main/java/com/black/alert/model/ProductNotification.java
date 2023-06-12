package com.black.alert.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductNotification(
    UUID productId, UUID bundleId, String name, BigDecimal minPrice, String image) {}
