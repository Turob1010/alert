package com.black.alert.model;

import com.black.alert.domain.AlertEntity;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AlertAdvancedRepository {

  Flux<AlertEntity> sortingByCreatedDateOrLastModifiedDate(
      final UUID userId, final boolean sortByCreatedDate, final Sort.Direction type);
}
