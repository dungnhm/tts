/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author hungdt
 */
public class SendEmail implements LoggerInterface {

    String senderEmailID;// = "sellersupport@30usd.com";
    String senderPassword;// = "Admin@2020";
    String emailSMTPserver;// = "smtp.zoho.com";
    String emailServerPort;// = "465";

    public SendEmail() {
    }

    public SendEmail(String senderEmailID, String senderPassword, String emailSMTPserver, String emailServerPort) {
        this.senderEmailID = senderEmailID;
        this.senderPassword = senderPassword;
        this.emailSMTPserver = emailSMTPserver;
        this.emailServerPort = emailServerPort;
    }

    public int sendEmail(String receiverEmailID, String emailSubject, String emailBody, String name) {
        int ret = 0;
        Properties props = new Properties();
        props.put("mail.smtp.user", senderEmailID);
        props.put("mail.smtp.host", emailSMTPserver);
        props.put("mail.smtp.port", emailServerPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", emailServerPort);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        SecurityManager security = System.getSecurityManager();
        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            MimeMessage msg = new MimeMessage(session);
            msg.setText(emailBody, "utf-8", "html");
            msg.setSubject(emailSubject);
            msg.setFrom(new InternetAddress(senderEmailID, name));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmailID));

            Transport.send(msg);
            logger.info("Message send Successfully:)");
        } catch (MessagingException | UnsupportedEncodingException mex) {
            logger.error("", mex);
            ret = -1;
        }
        return ret;
    }

    public class SMTPAuthenticator
            extends Authenticator {

        public SMTPAuthenticator() {
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmailID, senderPassword);
        }
    }

//    public static void main(String[] args) {
//        SendEmail sendEmail = new SendEmail();
//        String email = "<p></p><p><strong style=\"color: rgb(33, 32, 50);\">TIP: If you buy 2 or more (hint: make a gift for someone or team up) you’ll save quite a lot on shipping.&nbsp;</strong></p><p><br></p><p><img src=\"https://s5.postimg.org/s0zr2iq13/best_selling_icon.png\"></p><p><br></p><p>Guaranteed safe and secure checkout via:&nbsp;</p><p><strong>Paypal | VISA | MASTERCARD&nbsp;</strong></p><p><br></p><p>Click the&nbsp;<strong>GREEN BUTTON</strong>, select your size and style.&nbsp;</p><p><br></p><p><strong>Trouble Ordering?</strong></p><p>Email&nbsp;support@teespring.com</p><p>▼▼ Click GREEN&nbsp;BUTTON&nbsp;Below To Order&nbsp;▼▼</p><p></p>";
//        sendEmail.sendEmail("paul@leadsgen.asia", "Hungdt test mail 3", email);
//    }
}
