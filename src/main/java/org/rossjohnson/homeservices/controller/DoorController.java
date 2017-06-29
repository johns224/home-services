package org.rossjohnson.homeservices.controller;

import org.rossjohnson.homeservices.DoorEvent;
import org.rossjohnson.homeservices.repository.DoorEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;

@RestController
@RequestMapping("/homes/{homeName}/doors")
public class DoorController {

    private DoorEventRepository repository;

    @Autowired
    DoorController(DoorEventRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{doorName}")
    Collection<DoorEvent> getDoorEvents(@RequestParam(value = "type", required = false) String type,
                                        @PathVariable String doorName,
                                        @PathVariable String homeName) {

        DoorEvent example = ( type == null ? new DoorEvent(homeName, doorName) :
                new DoorEvent(homeName, doorName, DoorEvent.Type.valueOf(type.toLowerCase())));

        return repository.findAll(Example.of(example));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{doorName}")
    DoorEvent addDoorEvent(@RequestParam(value = "type", required = false, defaultValue = "opened") String type,
                           @PathVariable String doorName,
                           @PathVariable String homeName) {

        DoorEvent event = new DoorEvent(homeName, doorName, DoorEvent.Type.valueOf(type.toLowerCase()), new Date());
        repository.save(event);

        return event;
    }
}
