package org.rossjohnson.homeservices.repository;

import org.rossjohnson.homeservices.DoorEvent;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

public class GraphiteRepository {

	private static final String graphiteHost = "192.168.1.150";

	public static final int graphitePort = 2003;


	public GraphiteRepository() {
		System.out.println("*************************************************************************");
		System.out.println("Created repository with host " + graphiteHost + ":" + graphitePort);
		System.out.println("*************************************************************************");
	}

	public static void main(String[] args) throws IOException {
		new GraphiteRepository().write(new DoorEvent("kerriell", "front", DoorEvent.Type.opened, new Date()));
	}

	public void write(DoorEvent event) throws IOException {
		Socket socket = new Socket(graphiteHost, graphitePort);
		try {
			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			String message = createMessage(event);
			writer.write(message);
			writer.flush();
			writer.close();
			System.out.println("wrote " + message);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

	private String createMessage(DoorEvent de) {
		return "door." + de.getHomeName() + "." + de.getDoorName() + " " +
				(de.getType().equals(DoorEvent.Type.opened) ? "1" : "0") + " " +
				de.getEventDate().getTime() / 1000;
	}

}
