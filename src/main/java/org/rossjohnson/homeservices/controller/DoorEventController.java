package org.rossjohnson.homeservices.controller;

import org.apache.commons.logging.LogFactory;
import org.rossjohnson.homeservices.DateParser;
import org.rossjohnson.homeservices.DoorEvent;
import org.rossjohnson.homeservices.repository.DoorEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/doorEvents")
public class DoorEventController {

    public static final org.apache.commons.logging.Log LOG = LogFactory.getLog(DoorController.class);
    private DoorEventRepository repository;

    @Autowired
    DoorEventController(DoorEventRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<DoorEvent> getDoorEvents(@RequestParam(value = "type", required = false) String type,
                                  @RequestParam(value = "date", required = false) String onDate,
                                  @RequestParam(value = "doorName", required = false) String doorName,
                                  @RequestParam(value = "homeName", required = false, defaultValue = "kerriell") String homeName) {

        if (onDate != null) {
            return findByDate(doorName,
                    type == null ? null : DoorEvent.Type.valueOf(type.toLowerCase()),
                    onDate);
        } else {
            DoorEvent event = new DoorEvent(homeName);
            event.setType(type == null ? null : DoorEvent.Type.valueOf(type.toLowerCase()));
            event.setDoorName(doorName);
            return repository.findAll(Example.of(event));
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/graphite")
    public String dump() throws Exception {
        List events = repository.findAll();
        System.out.println("About to write " + events.size() + " records");
        StringBuffer buf = new StringBuffer();
        PrintWriter out = new PrintWriter("home-services-data.txt");
        try {
            for (Object e : events) {
                buf.append(createMessage((DoorEvent) e)).append("\n");
            }
            out.write(buf.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            out.flush();
            out.close();
        }
        return "Wrote " + events.size() + " records";
    }

    private String createMessage(DoorEvent de) {
        return "door." + de.getHomeName() + "." + de.getDoorName() + " " +
                (de.getType().equals(DoorEvent.Type.opened) ? "1" : "0") + " " +
                de.getEventDate().getTime() / 1000;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    DoorEvent getDoorEvent(@PathVariable String id) {

        return repository.findOne(Long.parseLong(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity addDoorEvent(@RequestParam(value = "type", required = false, defaultValue = "opened") String type,
                                @RequestParam(value = "homeName", required = false, defaultValue = "kerriell") String homeName,
                                @RequestParam String doorName) {

        DoorEvent event = new DoorEvent(homeName, doorName, DoorEvent.Type.valueOf(type.toLowerCase()), new Date());
        repository.save(event);
        LOG.info("Created " + event);

        return createResponse(event);
    }

    private List<DoorEvent> findByDate(String doorName, DoorEvent.Type type, String onDate) {
        DateParser dp = new DateParser();
        Date startDate = dp.parseDate(onDate);
        Date endDate = dp.getDayAfter(startDate);
        LOG.info(String.format("Looking for door events between %s and %s", startDate, endDate));

        if (doorName == null || type == null) {
            if (doorName != null) {
                return repository.findByDoorNameAndEventDateBetween(doorName, startDate, endDate);
            } else if (type != null) {
                return repository.findByTypeAndEventDateBetween(type, startDate, endDate);
            } else {
                return repository.findByEventDateBetween(startDate, endDate);
            }
        }
        return repository.findByDoorNameAndTypeAndEventDateBetween(doorName, type, startDate, endDate);
    }

    private ResponseEntity createResponse(DoorEvent event) {
        try {
            URI uri = new URI("/doorEvents/" + event.getId());
            return ResponseEntity.created(uri).body(event);
        } catch (URISyntaxException e) {
            LOG.error("Error creating URI for event " + event, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
