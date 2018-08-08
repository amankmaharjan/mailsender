/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mailsender.util;

import com.mycompany.mailsender.domain.Email;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author aman
 */
public class MailSenderUtil {

    public static void send(final String from, final String password, String to, String sub, String msg) {
        //Get properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        //compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(sub);
            message.setText(msg);
            //send message
            Transport.send(message);
            System.out.println("message sent successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void configureMail(Email email, String sender, String password, List<String> receipientList) throws Exception {

//      email.getRecipientCollection().forEach(receipient->send(email.getSenderId().getEmailAddress(), password,receipient.getEmailAddress(),email.getSubject(),email.getMessage()));
        System.out.println("Email:");
        System.out.println("sender:" + sender);
        System.out.println("password:" + password.toString());
        long t0 = System.currentTimeMillis() / 1000;
        receipientList.forEach(receipient -> {
            System.out.println("sending mail to: " + receipient);
//            try {
////                Thread.sleep(email.getDuration()*60000);
////                System.out.println( System.currentTimeMillis()/60000-t0);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(MailSenderUtil.class.getName()).log(Level.SEVERE, null, ex);
//            }
            send(sender, password, receipient, email.getSubject(), email.getMessage());
        }
        );
    }

}
