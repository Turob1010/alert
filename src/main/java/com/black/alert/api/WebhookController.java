package com.black.alert.api;


import com.black.alert.model.TelegramUpdate;
import com.black.alert.service.WebhookService;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/telegram/v1")
public class WebhookController {

  private final WebhookService webhookService;

  public WebhookController(WebhookService webhookService) {
    this.webhookService = webhookService;
  }

  @PostMapping
  Mono<TelegramUpdate> updates(@RequestBody final Update update) {
    return webhookService.telegramUpdate(update);
  }




}
