package com.black.alert.context.properties;

import org.springframework.boot.context.properties.ConstructorBinding;

import static java.util.Objects.requireNonNull;

@ConstructorBinding
public record ProductProperties(String baseUrl, String productUrl) {

  public ProductProperties(final String baseUrl, final String productUrl) {

    this.baseUrl = requireNonNull(baseUrl);
    this.productUrl = requireNonNull(productUrl);
  }
}
