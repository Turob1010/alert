package com.black.alert.context.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static java.util.Objects.requireNonNull;

@ConfigurationProperties("application")
public record ApplicationProperties(
    @NestedConfigurationProperty ProductProperties product,
    @NestedConfigurationProperty UserProperties user,
    @NestedConfigurationProperty AuthServerProperties authServer,
    @NestedConfigurationProperty BotProperties telegramBot,
    @NestedConfigurationProperty HomeProperties home) {

  public ApplicationProperties(
      ProductProperties product,
      UserProperties user,
      AuthServerProperties authServer,
      BotProperties telegramBot,
      HomeProperties home) {

    this.product = requireNonNull(product, "Product properties cannot be null");
    this.user = requireNonNull(user, "User properties cannot be null");
    this.authServer = requireNonNull(authServer, "Auth server properties cannot be null");
    this.telegramBot = requireNonNull(telegramBot, "Telegram bot properties cannot be null");
    this.home = requireNonNull(home, "Home properties cannot be null");
  }
}
