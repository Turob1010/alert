package com.black.alert.context.properties;


import static java.util.Objects.requireNonNull;

public record ProductProperties(String baseUrl, String productUrl) {

  public ProductProperties(final String baseUrl, final String productUrl) {

    this.baseUrl = requireNonNull(baseUrl);
    this.productUrl = requireNonNull(productUrl);
  }
}
