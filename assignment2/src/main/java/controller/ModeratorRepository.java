package controller;

import domain.Moderator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "moderators", path = "moderators")
public interface ModeratorRepository extends MongoRepository<Moderator, String> {

    Moderator findById(@Param("id") Integer id);
}
