package org.rossjohnson.homeservices.repository;

import org.rossjohnson.homeservices.DoorEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoorEventRepository extends JpaRepository<DoorEvent, Long> {

    List<DoorEvent> findByType(DoorEvent.Type type);

}
