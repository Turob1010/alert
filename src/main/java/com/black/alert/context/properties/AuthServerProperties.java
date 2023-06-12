package com.black.alert.context.properties;

import org.springframework.boot.context.properties.ConstructorBinding;

import static java.util.Objects.requireNonNull;

@ConstructorBinding
public record AuthServerProperties(String baseUrl) {

  public AuthServerProperties(String baseUrl) {
    this.baseUrl = requireNonNull(baseUrl);
  }
}
