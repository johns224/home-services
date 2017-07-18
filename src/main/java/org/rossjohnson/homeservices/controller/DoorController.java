package org.rossjohnson.homeservices.controller;

import org.apache.commons.logging.LogFactory;
import org.rossjohnson.homeservices.DoorEvent;
import org.rossjohnson.homeservices.repository.DoorEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/homes/{homeName}/doors")
public class DoorController {

    public static final org.apache.commons.logging.Log LOG = LogFactory.getLog(DoorController.class);
    private DoorEventRepository repository;

    @Autowired
    DoorController(DoorEventRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{doorName}")
    List<DoorEvent> getDoorEvents(@RequestParam(value = "type", required = false) String type,
                                  @RequestParam(value = "date", required = false) String onDate,
                                        @PathVariable String homeName,
                                        @PathVariable String doorName) {

        if (onDate != null) {
            return findByDate(homeName, doorName, onDate);
        }
        else {
            DoorEvent example = (
                    type == null ?
                            new DoorEvent(homeName, doorName) :
                            new DoorEvent(homeName, doorName, DoorEvent.Type.valueOf(type.toLowerCase()))
            );
            return repository.findAll(Example.of(example)); //, new PageRequest(1, 50)).getContent();
        }
    }

    private List<DoorEvent> findByDate(String homeName, String doorName, String onDate) {
        try {
            Date eventDate = new SimpleDateFormat("MM-dd-yyyy").parse(onDate);
            LOG.info("Looking for " + doorName + " door events on " + eventDate);
            //return repository.findByHomeNameAndDoorNameAndEventDate(homeName, doorName, eventDate);
            return repository.findByEventDate(eventDate);
        } catch (ParseException e) {
            LOG.error("Can't convert " + onDate + " to date.  Use format MM-dd-yyyy", e);
        }
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{doorName}/{id}")
    DoorEvent getDoorEvent(@PathVariable String doorName, @PathVariable String id) {

        LOG.info("looking for door event " + Long.parseLong(id));
        return repository.findOne(Long.parseLong(id));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{doorName}")
    ResponseEntity addDoorEvent(@RequestParam(value = "type", required = false, defaultValue = "opened") String type,
                           @PathVariable String doorName,
                           @PathVariable String homeName) {

        DoorEvent event = new DoorEvent(homeName, doorName, DoorEvent.Type.valueOf(type.toLowerCase()), new Date());
        repository.save(event);
        LOG.info("Created " + event);

        return createResponse(doorName, homeName, event);
    }

    private ResponseEntity createResponse(@PathVariable String doorName, @PathVariable String homeName, DoorEvent event) {
        try {
            URI uri = new URI("/homes/" + homeName + "/doors/" + doorName + "/" + event.getId());
            return ResponseEntity.created(uri).body(event);
        }
        catch (URISyntaxException e) {
            LOG.error("Error creating URI for event " + event, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
