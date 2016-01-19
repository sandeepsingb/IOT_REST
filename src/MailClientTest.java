import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;

public class MailClientTest {
	public static void main(String[] args) throws SendGridException {
		SendGrid sendgrid = new SendGrid(
				"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");

		Email email = new Email();
		String str = "lopesrohan1988@gmail.com";
		String[] strA = new String[] {  "rohan.lopes@kpit.com" };
		
		
		String[] strB=str.split(",");
		System.out.println(str.split(str));

		System.out.println(strA);
		// email.addTo(new String[] { "Sandeep.Bhandari@kpit.com",
		// "Sagar.Patankar@kpit.com", "rohan.lopes@kpit.com" });
		email.addTo("rohan.lopes@kpit.com");
		email.setFrom("lopesrohan1988@gmail.com");
		email.setSubject("ALERT: TEST");
		email.setText("SR Raised for Fault : Unable to connect sensor");
		 Response res = sendgrid.send(email);
		 System.out.println(res.getStatus());

	}
}
