package org.rossjohnson.homeservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rossjohnson.homeservices.model.PlexPayload;
import org.rossjohnson.homeservices.service.HomeAssistantService;
import org.rossjohnson.homeservices.service.VeraService;
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
	private VeraService veraService;

	@Autowired
	private HomeAssistantService haController;

	@RequestMapping(path = "/webhook", method = RequestMethod.POST)
	public void processHook(MultipartHttpServletRequest request,
							HttpServletResponse response,
							@RequestParam("files") MultipartFile[] files ) throws Exception {

		PlexPayload payload = jacksonObjectMapper.readValue(request.getParameter("payload"), PlexPayload.class);

		if ("media.play".equals(payload.getEvent())) {
			if (payload.getPlayer().getTitle().contains("theater")) {
				System.out.println(new Date() + " - turning off lights in theater");
				//veraService.runScene("1");

				haController.runVideoStartedScene();
			}
		}


	}
}
