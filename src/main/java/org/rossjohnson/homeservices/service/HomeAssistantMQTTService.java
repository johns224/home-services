package org.rossjohnson.homeservices.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HomeAssistantMQTTService implements HomeAssistantService {
	@Value("${mqtt.topic.video.started:/theater/playing}")
	private String videoStartedTopic = "/theater/playing";

	@Value("${mqtt.host:192.168.1.28}")
	private String mqttHost = "192.168.1.28";

	@Value("${mqtt.user:mqtt-user")
	private String mqttUser = "mqtt-user";

	@Value("${mqtt.pass:mos97fink")
	private String mqttPass = "mos97fink";

	@Value("${mqtt.port:1883}")
	private int mqttPort = 1883;

	private int mqttQos = 1;

	@Override
	public void runVideoStartedScene() {
		try {
			String broker = "tcp://" + mqttHost + ":" + mqttPort;
			MqttClient client = new MqttClient(	broker, "homeServices", new MemoryPersistence());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(mqttUser);
			options.setPassword(mqttPass.toCharArray());
			client.connect(options);
			MqttMessage message = new MqttMessage("Playing".getBytes());
			message.setQos(mqttQos);
			System.out.println("Publishing '" + message.toString() + "' to " + broker + " on " + videoStartedTopic);
			client.publish(videoStartedTopic, message);
			client.disconnect();

		} catch (MqttException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new HomeAssistantMQTTService().runVideoStartedScene();
	}
}
