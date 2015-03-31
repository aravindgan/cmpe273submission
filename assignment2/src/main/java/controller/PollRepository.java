package controller;

import domain.Poll;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.ArrayList;

@RepositoryRestResource(collectionResourceRel = "polls", path = "polls")
public interface PollRepository extends MongoRepository<Poll, String> {

    Poll findById(@Param("id") String id);
    ArrayList<Poll> findByModeratorId(@Param("moderatorId") int moderatorId);

}

