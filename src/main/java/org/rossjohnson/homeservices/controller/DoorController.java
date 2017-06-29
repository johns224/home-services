package org.rossjohnson.homeservices.controller;

import org.rossjohnson.homeservices.DoorEvent;
import org.rossjohnson.homeservices.repository.DoorEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;

/**
 * Created by ross on 6/27/2017.
 */
@RestController
@RequestMapping("/homes/{homeName}/doors")
public class DoorController {

    private DoorEventRepository repository;

    @Autowired
    DoorController(DoorEventRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{doorName}")
    Collection<DoorEvent> getDoorEvents(@RequestParam(value = "type", defaultValue = "opened") String type,
                                        @PathVariable String doorName,
                                        @PathVariable String homeName) {

        return repository.findAll(Example.of(
                        new DoorEvent(homeName, doorName, DoorEvent.Type.valueOf(type)))
        );

    }

    @RequestMapping(method = RequestMethod.POST, value = "/{doorName}")
    DoorEvent addDoorEvent(@RequestParam(value = "type", required = false, defaultValue = "opened") String type,
                           @PathVariable String doorName,
                           @PathVariable String homeName) {

        DoorEvent event = new DoorEvent(homeName, doorName, DoorEvent.Type.valueOf(type), new Date());
        repository.save(event);

        return event;
    }
}
