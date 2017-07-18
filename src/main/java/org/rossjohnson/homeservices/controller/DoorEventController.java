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
import java.util.Calendar;
import java.util.Collections;
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
			return findByDate(doorName, type == null ? null : DoorEvent.Type.valueOf(type.toLowerCase()), onDate);
		} else {
			DoorEvent event = new DoorEvent(homeName);
			event.setType(type == null ? null : DoorEvent.Type.valueOf(type.toLowerCase()));
			event.setDoorName(doorName);

			return repository.findAll(Example.of(event));
		}
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
		try {
			Date startDate = new SimpleDateFormat("MM-dd-yyyy").parse(onDate);
			Date endDate = getEndDate(startDate);
			LOG.info("Looking for door events between " + startDate + " and " + endDate);

            if (doorName == null || type == null) {
                if (doorName != null) {
                    return repository.findByDoorNameAndEventDateBetween(doorName, startDate, endDate);
                }
                else if (type != null) {
                    return repository.findByTypeAndEventDateBetween(type, startDate, endDate);
                }
                else {
                    return repository.findByEventDateBetween(startDate, endDate);
                }
            }
			return repository.findByDoorNameAndTypeAndEventDateBetween(doorName, type, startDate, endDate);
		}
        catch (ParseException e) {
			LOG.error("Can't convert " + onDate + " to date.  Use format MM-dd-yyyy", e);
		}
		return Collections.EMPTY_LIST;
	}

	private Date getEndDate(Date startDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.DATE, 1);
		return c.getTime();
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
