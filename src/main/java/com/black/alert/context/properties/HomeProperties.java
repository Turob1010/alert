package com.black.alert.context.properties;

import org.springframework.boot.context.properties.ConstructorBinding;

public record HomeProperties(String homeUrl) {

  public HomeProperties(final String homeUrl) {
    this.homeUrl = homeUrl;
  }
}
