package org.rossjohnson.homeservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rossjohnson.homeservices.model.PlexPayload;
import org.rossjohnson.homeservices.vera.VeraController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/plex")
public class PlexWebHookController {

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	@Autowired
	private VeraController veraController;

	@RequestMapping(path = "/webhook", method = RequestMethod.POST)
	public void processHook(MultipartHttpServletRequest request,
							HttpServletResponse response,
							@RequestParam("files") MultipartFile[] files ) throws Exception {

		String payloadString = request.getParameter("payload");

		//System.out.println(payloadString);
		PlexPayload payload = jacksonObjectMapper.readValue(payloadString, PlexPayload.class);
		//System.out.println(payload.getPlayer().getTitle() + " - " + payload.getEvent());

		if ("media.play".equals(payload.getEvent())) {
			if (payload.getPlayer().getTitle().contains("theater")) {
				System.out.println(new Date() + " - turning off lights in theater");
				veraController.runScene("1");
			}
		}


	}
}
