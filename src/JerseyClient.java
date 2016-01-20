import java.nio.file.Files;
import java.nio.file.Paths;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * This is the test client. The main method is used to test the rest services
 * 
 * @author ROHANL1
 * 
 */
public class JerseyClient {
	public static void main(String[] args) {
		try {

			ClientConfig clientConfig = new DefaultClientConfig();

			// clientConfig.getFeatures().put(
			// JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

			Client client = Client.create(clientConfig);

			WebResource webResource = client
					.resource("http://jbossews-serviceex.rhcloud.com/iot1/sr/iotcs/recieve");

//			WebResource webResource = client
//					.resource("http://localhost:9999/iot1/sr/iotcs/recieve");
			
			
			// Map<String,String> postBody = new HashMap<String,String>();
			// postBody.put("status", "NEE");
			// postBody.put("code", "SR0006");
			//
			// ServiceRequestUpdateDTO serviceRequestUpdateDTO = new
			// ServiceRequestUpdateDTO();
			//
			// ServiceRequestDTO s = new ServiceRequestDTO();
			// s.setCode("S00001");
			// s.setStatus("NEW");
			// s.setActualResolution("FIXED");
			// serviceRequestUpdateDTO.setServiceRequestDTO(s);
			//
			// ClientResponse response = webResource.accept("application/json")
			// .type("application/json")
			// .post(ClientResponse.class, serviceRequestUpdateDTO);

			// IOTCS

			String str = new String(Files.readAllBytes(Paths
					.get("C:/Users/rohanl1/Documents/json.txt")));
			ClientResponse response = webResource.post(ClientResponse.class,
					str);

			if (response.getStatus() != 200) {
				System.out.println(response);
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);

			System.out.println("Server response .... \n");
			System.out.println(output);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
