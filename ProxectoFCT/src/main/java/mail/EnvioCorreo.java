package mail;


import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnvioCorreo {



    public static void Correo(String direccionCorreo, String asunto, String cuerpo) {
        // Configuración de la sesión de correo electrónico
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        String username = "fctdam2023@gmail.com";
        String password = "gvotbjwtfwyuddcd";

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("fctdam2023@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(direccionCorreo));
            message.setSubject(asunto);
            message.setText(cuerpo);


            Transport.send(message);
            System.out.println("Correo enviado ");
        } catch (MessagingException e) {
            System.out.println("Fallo envio de correo: " + e.getMessage());
        }
    }
}