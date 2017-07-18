package org.rossjohnson.homeservices.repository;

import org.rossjohnson.homeservices.DoorEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Temporal;


import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

public interface DoorEventRepository extends JpaRepository<DoorEvent, Long> {

    List<DoorEvent> findByEventDate(@Temporal(TemporalType.DATE) Date eventDate);

    List<DoorEvent> findByDoorNameAndTypeAndEventDateBetween(String doorName, DoorEvent.Type type, Date startDate, Date endDate);

    List<DoorEvent> findByDoorNameAndEventDateBetween(String doorName, Date startDate, Date endDate);

    List<DoorEvent> findByTypeAndEventDateBetween(DoorEvent.Type type, Date startDate, Date endDate);

    List<DoorEvent> findByEventDateBetween(Date startDate, Date endDate);
}
