package com.black.alert.repository;


import com.black.alert.domain.MessageEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<MessageEntity, UUID> {

  Mono<Boolean> existsByUserId(final UUID userId);

  Mono<MessageEntity> findByUserId(final UUID userId);

  Mono<MessageEntity> findByChatId(final String chatId);
}
