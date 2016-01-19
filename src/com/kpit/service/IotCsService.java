package com.kpit.service;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Random;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.kpit.dto.ServiceRequestDTO;
import com.kpit.dto.ServiceRequestStatus;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;

/**
 * This service accept the alert from Stream and Insert in to SR table.
 * 
 * @author ROHANL1
 * 
 */
@Path("/iotcs")
public class IotCsService {

	// SAMPLE JSON REQUESTS FROM STREAM
	/*
	 * [{"id":"0bfe2e3b-e522-44f0-a846-747c9f260c46","clientId":
	 * "3a18e2b4-0252-4e1d-99af-4ebc4f96edcc"
	 * ,"source":"0-AM","destination":"","priority"
	 * :"LOW","reliability":"BEST_EFFORT"
	 * ,"eventTime":1445413539204,"sender":"","type"
	 * :"ALERT","properties":{},"direction"
	 * :"FROM_DEVICE","receivedTime":1445413539220
	 * ,"sentTime":1445413539256,"payload"
	 * :{"format":"urn:com:oracle:hvac:alert:unabletoconnect"
	 * ,"description":"Unable to connect to the sensors"
	 * ,"severity":"CRITICAL","data":{"unable_to_connect":true}}}]
	 * 
	 * 
	 * [{"id":"48b5c64f-2552-4c31-b8b9-2c2eb19ada4d","clientId":
	 * "e330bfd9-244e-4a7e-aab5-b1691411ccda"
	 * ,"source":"0-AM","destination":"","priority"
	 * :"LOW","reliability":"BEST_EFFORT"
	 * ,"eventTime":1445412306336,"sender":"","type"
	 * :"DATA","properties":{},"direction"
	 * :"FROM_DEVICE","receivedTime":1445412306347
	 * ,"sentTime":1445412306371,"payload"
	 * :{"format":"urn:com:oracle:hvac","data"
	 * :{"outputTemp":24.55702242255211,"vibration"
	 * :0.9818008260708302,"oilViscosity"
	 * :0.388129319450818,"motorAmperage":50.09254181617871
	 * ,"targetTemp":25.0,"time":1.445412306335E12}}}]
	 */

	// END

	/*
	 * public void receive(String str) throws Exception {
	 * System.out.println("Received JSON string-> " + str); JSONArray jsonArray
	 * = null; final JSONObject jsonObject = jsonArray.getJSONObject(0);
	 * 
	 * System.out.println("Received JSON string-> " + jsonObject);
	 * 
	 * JSONObject payLoad = jsonObject.getJSONObject("payload");
	 * 
	 * // get the message format final String messageFormat =
	 * payLoad.getString("format");
	 * 
	 * // if (EXPECTED_MSG_FORMAT.equals(messageFormat)) { // message is in //
	 * expected format get the data final JSONObject data =
	 * payLoad.getJSONObject("data");
	 * System.out.println("Message is in expected format"); if (data != null) {
	 * final String taskId = data.getString("taskid"); final String deviceId =
	 * data.getString("deviceid");
	 * 
	 * } // } }
	 */
	@POST
	@Path("/recieve")
	public void receive(String str) throws Exception {

		System.out.println("Received JSON string-> " + str);
		// PARSE JSON TO OBJ
		JSONTokener tokener = new JSONTokener(str);
		JSONArray array = new JSONArray(tokener);
		JSONObject root = array.getJSONObject(0);
		ServiceRequestDTO sr = new ServiceRequestDTO();

		if (root.getString("type").equals("ALERT")) {

			Date date = new Date(root.getLong("eventTime"));
			sr.setRaiseDate(date);

			JSONObject payload = root.getJSONObject("payload");

			sr.setDesc(payload.getString("description"));
			sr.setFaultDesc(payload.getString("description"));
			sr.setPriority(payload.getString("severity"));
			sr.setStatus("New");

			try {
				JSONObject data = payload.getJSONObject("data");
				sr.setGenSetName(data.getString("$(source)_description"));
				sr.setGenSetModel(data.getString("$(source)_modelNumber"));
				sr.setGenSetSerial(data.getString("$(source)_serialNumber"));
				sr.setGenSetLoc(data.getString("$(source)_location"));
				sr.setFaultCode("F" + data.getString("Faultcode"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// SRCODE

			Random r = new Random();
			int Low = 1;
			int High = 100000;
			int R = r.nextInt(High - Low) + Low;
			sr.setCode("S" + new Integer(R).toString());

			// JSON PARSE END

			// Convert Priority
			if (sr.getPriority().equals("CRITICAL"))
				sr.setPriority("Critical");
			else if (sr.getPriority().equals("MAJOR"))
				sr.setPriority("Major");
			else if (sr.getPriority().equals("MINOR"))
				sr.setPriority("Minor");

			// SAVE DATA
			Connection conn = null;
			PreparedStatement st = null;

			Statement select = null;
			PreparedStatement errSt = null;
			ResultSet errRs = null;
			ResultSet rs = null;
			ResultSet emailrs = null;
			Integer rowsAffected = null;
			try {
				InitialContext ctx = new InitialContext();
				DataSource ds = (DataSource) ctx
						.lookup("java:/comp/env/jdbc/kpit");
				conn = ds.getConnection();
				select = conn.createStatement();

				// Fetch Fault Desc
				errSt = conn
						.prepareStatement("SELECT  FAULT_MESSAGE FROM ERROR_MAPPING WHERE FAULT_CODE=?");
				errSt.setString(1, sr.getFaultCode());
				errRs = errSt.executeQuery();
				String faultMsg = null;
				while (errRs.next()) {
					System.out.println(errRs.getString("FAULT_MESSAGE"));
					faultMsg = errRs.getString("FAULT_MESSAGE");

				}
				// Set SR DESC
				sr.setDesc(faultMsg);
				// END

				String query = "INSERT INTO SERVICEREQUEST (SRCODE, SRDESC, SRPRIORITY, SRSTATUS, FAULTCODE, FAULTDESC, RAISEDDATE, GENSETNAME, GENSETMODEL, GENSETSERIAL,GENSETSOFTVER,GENSETLOC) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

				st = conn.prepareStatement(query);
				st.setString(1, sr.getCode());
				st.setString(2, sr.getDesc());
				st.setString(3, sr.getPriority());
				st.setString(4, sr.getStatus());
				st.setString(5, sr.getFaultCode());
				st.setString(6, sr.getFaultDesc());
				st.setDate(7, new java.sql.Date(sr.getRaiseDate().getTime()));
				st.setString(8, sr.getGenSetName());
				st.setString(9, sr.getGenSetModel());
				st.setString(10, sr.getGenSetSerial());
				st.setString(11, "V1.0");
				st.setString(12, sr.getGenSetLoc());
				rowsAffected = st.executeUpdate();
				// rowsAffected = st.executeUpdate();

				// Send email Notification
				emailrs = select
						.executeQuery("SELECT TO_EMAIL, FROM_EMAIL,ENABLE_EMAIL FROM EMAIL WHERE 1");
				String toEmail = null;
				String fromEmail = null;
				String enableEmail = null;
				while (emailrs.next()) {
					toEmail = emailrs.getString("TO_EMAIL");
					fromEmail = emailrs.getString("FROM_EMAIL");
					enableEmail = emailrs.getString("ENABLE_EMAIL");
				}

				sendEmail(sr, toEmail, fromEmail, enableEmail);

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (errRs != null)
						errRs.close();
					if (emailrs != null)
						emailrs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					if (st != null)
						st.close();
					if (errSt != null)
						errSt.close();
					if (select != null)
						select.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Response sendEmail(ServiceRequestDTO sr, String toEmail,
			String fromEmail, String enableEmail) {
		Response res = null;
		try {
			System.out.println(toEmail);
			System.out.println(fromEmail);
			System.out.println(enableEmail);
			SendGrid sendgrid = new SendGrid(
					"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");

			Email email = new Email();
			email.addTo(toEmail.split(","));
			// email.addTo("lopesrohan1988@gmail.com");
			email.setFrom(fromEmail);
			email.setSubject(sr.getPriority() + " ALERT: " + sr.getFaultDesc());
			email.setHtml("<html><body>" + "SR " + sr.getCode()
					+ " raised for Fault: " + sr.getFaultDesc() + "<br/>"
					+ "SR Desc: " + sr.getDesc() + "<br/>"
					+ "Generator Name: " + sr.getGenSetName() + "<br/>"
					
					+ "Generator Model: " + sr.getGenSetModel()
					+ "</body></html>");

			if (enableEmail.equals("Y")) {
				res = sendgrid.send(email);
				System.out.println(res.getStatus());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	// public void receiveDemo(String str) throws Exception {
	//
	// System.out.println("Received JSON string-> " + str);
	// // PARSE JSON TO OBJ
	// JSONTokener tokener = new JSONTokener(str);
	// JSONArray array = new JSONArray(tokener);
	// JSONObject root = array.getJSONObject(0);
	// ServiceRequestDTO sr = new ServiceRequestDTO();
	//
	// if (root.getString("type").equals("ALERT")) {
	//
	// Date date = new Date(root.getLong("eventTime"));
	// sr.setRaiseDate(date);
	//
	// JSONObject payload = root.getJSONObject("payload");
	//
	// sr.setDesc(payload.getString("description"));
	// sr.setFaultDesc(payload.getString("description"));
	// sr.setPriority(payload.getString("severity"));
	// sr.setStatus("New");
	//
	// try {
	// JSONObject data = payload.getJSONObject("data");
	// sr.setGenSetName(data.getString("$(source)_description"));
	// sr.setGenSetModel(data.getString("$(source)_modelNumber"));
	// sr.setGenSetSerial(data.getString("$(source)_serialNumber"));
	// sr.setFaultCode(data.getString("Faultcode"));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // SRCODE
	//
	// Random r = new Random();
	// int Low = 1;
	// int High = 100000;
	// int R = r.nextInt(High - Low) + Low;
	// sr.setCode("S" + new Integer(R).toString());
	//
	// // JSON PARSE END
	//
	// // Convert Priority
	// if (sr.getPriority().equals("CRITICAL"))
	// sr.setPriority("Critical");
	// else if (sr.getPriority().equals("MAJOR"))
	// sr.setPriority("Major");
	// else if (sr.getPriority().equals("MINOR"))
	// sr.setPriority("Minor");
	//
	// // SAVE DATA
	// Connection conn = null;
	// PreparedStatement st = null;
	// ResultSet rs = null;
	// Integer rowsAffected = null;
	// try {
	// InitialContext ctx = new InitialContext();
	// DataSource ds = (DataSource) ctx
	// .lookup("java:/comp/env/jdbc/kpit");
	// conn = ds.getConnection();
	// String query =
	// "INSERT INTO SERVICEREQUEST (SRCODE, SRDESC, SRPRIORITY, SRSTATUS, FAULTCODE, FAULTDESC, RAISEDDATE, GENSETNAME, GENSETMODEL, GENSETSERIAL) VALUES (?,?,?,?,?,?,?,?,?,?)";
	//
	// st = conn.prepareStatement(query);
	// st.setString(1, sr.getCode());
	// st.setString(2, sr.getDesc());
	// st.setString(3, sr.getPriority());
	// st.setString(4, sr.getStatus());
	// st.setString(5, sr.getFaultCode());
	// st.setString(6, sr.getFaultDesc());
	// st.setDate(7, new java.sql.Date(sr.getRaiseDate().getTime()));
	// st.setString(8, sr.getGenSetName());
	// st.setString(9, sr.getGenSetModel());
	// st.setString(10, sr.getGenSetSerial());
	// rowsAffected = st.executeUpdate();
	// // rowsAffected = st.executeUpdate();
	//
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// } finally {
	// try {
	// if (rs != null)
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// try {
	// if (st != null)
	// st.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// try {
	// if (conn != null)
	// conn.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }

	public static void main(String[] args) throws  Exception {
		JSONTokener tokener = new JSONTokener(new FileReader(
				"C:/Users/rohanl1/Documents/json.txt"));
		JSONArray array = new JSONArray(tokener);
		JSONObject root = array.getJSONObject(0);
		System.out.println(root.getString("id"));
		System.out.println(root.getString("priority"));
		Date date = new Date(root.getLong("eventTime"));
		System.out.println(date);

		JSONObject payload = root.getJSONObject("payload");
		System.out.println(payload.getString("format"));
		System.out.println(payload.getString("description"));
		System.out.println(payload.getString("severity"));

		JSONObject data = payload.getJSONObject("data");

		System.out.println(data.getString("Faultcode"));
		System.out.println(data.getString("$(source)_description"));
		ServiceRequestDTO sr = new ServiceRequestDTO();
		Random r = new Random();
		int Low = 1;
		int High = 100000;
		int R = r.nextInt(High - Low) + Low;
		System.out.println(R);
		// System.out.println(jsonStr);
	}

}
