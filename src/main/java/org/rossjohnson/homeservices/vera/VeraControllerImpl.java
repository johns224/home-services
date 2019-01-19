package org.rossjohnson.homeservices.vera;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VeraControllerImpl implements VeraController {

	private static final String sceneURL =
			"http://192.168.1.180:3480/data_request?" +
					"id=lu_action&" +
					"serviceId=urn:micasaverde-com:serviceId:HomeAutomationGateway1&" +
					"action=RunScene&" +
					"SceneNum=";

	@Override
	public void runScene(String sceneNumber) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(sceneURL + sceneNumber);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(get);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally
		{
			if (response != null)
				try {response.close();} catch (IOException e) {}
		}
	}
}
