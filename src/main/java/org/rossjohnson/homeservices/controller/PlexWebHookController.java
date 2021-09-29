package org.rossjohnson.homeservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.rossjohnson.homeservices.model.PlexPayload;
import org.rossjohnson.homeservices.service.HomeAssistantService;
import org.rossjohnson.homeservices.service.VeraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/plex")
public class PlexWebHookController {

	public static final org.apache.commons.logging.Log LOG = LogFactory.getLog(PlexWebHookController.class);

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
				LOG.info("Turning off lights in theater");
//				LOG.info("Is home assistant controller null? " + (haController == null));
				//veraService.runScene("1");

				haController.runVideoStartedScene();
			}
		}


	}
}
