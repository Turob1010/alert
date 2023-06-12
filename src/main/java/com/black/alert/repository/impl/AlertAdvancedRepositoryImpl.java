package com.black.alert.repository.impl;


import com.black.alert.domain.AlertEntity;
import com.black.alert.model.AlertAdvancedRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.black.alert.util.Constants.COLLECTION_ALERT;


@Repository
public class AlertAdvancedRepositoryImpl implements AlertAdvancedRepository {
  private final ReactiveMongoOperations mongo;

  public AlertAdvancedRepositoryImpl(ReactiveMongoOperations mongo) {
    this.mongo = mongo;
  }

  @Override
  public Flux<AlertEntity> sortingByCreatedDateOrLastModifiedDate(
      final UUID userId, final boolean sortByCreatedDate, final Sort.Direction type) {
    final Criteria criteria = new Criteria();

    final List<Criteria> andCriteria = new ArrayList<>();

    if (userId != null) {
      andCriteria.add(Criteria.where("userId").is(userId));
    }

    if (!andCriteria.isEmpty()) {
      criteria.andOperator(andCriteria);
    }

    final Query query = Query.query(criteria);
    if (sortByCreatedDate) {
      query.with(Sort.by(type, "createdDate"));
    } else {
      query.with(Sort.by(type, "lastModifiedDate"));
    }
    return mongo.find(query, AlertEntity.class, COLLECTION_ALERT);
  }
}
