package org.rossjohnson.homeservices;

import org.rossjohnson.homeservices.repository.DoorEventRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class GraphiteEventDumper {

	private DoorEventRepository repository;


	@Autowired
	public GraphiteEventDumper(DoorEventRepository repository) {
		this.repository = repository;
	}

	public void run() {
		//GraphiteEventDumper dumper = new
	}
}
