package org.rossjohnson.homeservices.service;

import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HomeAssistantMQTTService implements HomeAssistantService {
	@Value("${mqtt.topic.video.started}")
	private String videoStartedTopic;

	@Value("${mqtt.host}")
	private String mqttHost;

	@Value("${mqtt.user}")
	private String mqttUser;

	@Value("${mqtt.pass}")
	private String mqttPass;

	@Value("${mqtt.port}")
	private int mqttPort;

	public static final org.apache.commons.logging.Log LOG = LogFactory.getLog(HomeAssistantMQTTService.class);

	@Override
	public void runVideoStartedScene() {
		try {
			String broker = "tcp://" + mqttHost + ":" + mqttPort;
			LOG.info("Using broker " + broker + " with user/pass "  + mqttUser + "/" + mqttPass);
			MqttClient client = new MqttClient(	broker, "homeServices", new MemoryPersistence());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(mqttUser);
			options.setPassword(mqttPass.toCharArray());
			client.connect(options);
			MqttMessage message = new MqttMessage("Playing".getBytes());
			message.setQos(1);
			LOG.info("Publishing '" + message.toString() + "' to " + broker + " on " + videoStartedTopic);
			client.publish(videoStartedTopic, message);
			client.disconnect();

		} catch (MqttException e) {
			LOG.info("Problem publishing to MQTT:", e);
		}
	}

	public static void main(String[] args) {
		new HomeAssistantMQTTService().runVideoStartedScene();
	}
}
