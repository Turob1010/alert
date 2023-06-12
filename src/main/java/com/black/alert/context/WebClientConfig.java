package com.black.alert.context;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static reactor.netty.resources.ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT;

@Configuration

@ConfigurationPropertiesScan
@OpenAPIDefinition(
    info =
        @Info(
            title = "Alert APIs v1.0",
            version = "1.0",
            description = "Alert APIs v1.0 Documentation"))
public class WebClientConfig {

  /**
   * Default connection pool does not have any maxIdleTime set, so connections become stale after
   * some time and you can see error messages like
   *
   * <p><code>
   * Caused by: io.netty.channel.unix.Errors$NativeIoException: readAddress(..) failed: Connection reset by peer
   * </code>
   *
   * <p>Instead we're going to free idle connections and re-use most fresh ones
   *
   * @see org.springframework.http.client.reactive.ReactorResourceFactory#connectionProviderSupplier
   * @return Webclient customizer
   */
  @Bean
  HttpClient httpClient() {
    return HttpClient.create(
            ConnectionProvider.builder("webflux")
                .evictInBackground(Duration.ofMinutes(1))
                .lifo()
                .maxConnections(500)
                .metrics(true)
                .maxIdleTime(Duration.ofMinutes(1))
                .maxLifeTime(Duration.ofMinutes(5))
                .pendingAcquireTimeout(Duration.ofMillis(DEFAULT_POOL_ACQUIRE_TIMEOUT))
                .build())
        .responseTimeout(Duration.of(20, ChronoUnit.SECONDS));
  }

  @Bean
  WebClientCustomizer webClientCustomizer() {
    return (webClientBuilder) ->
        webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient()));
  }
}
