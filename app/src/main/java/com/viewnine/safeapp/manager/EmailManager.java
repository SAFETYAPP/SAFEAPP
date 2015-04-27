package com.viewnine.safeapp.manager;


import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.viewnine.safeapp.ulti.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * Created by user on 4/27/15.
 * Disable security: https://www.google.com/settings/security/lesssecureapps
 */
public class EmailManager {
    private static final String accName1 = "safeappmail1@gmail.com";
    private static final String accP1 = "safeappmail";
    private static final String accName2 = "safeappmail2@gmail.com";
    private static final String accP2 = "safeappmail";
    private static EmailManager ourInstance = new EmailManager();
    private InternetAddress internetAddress;
    public static EmailManager getInstance() {
        return ourInstance;
    }

    private EmailManager() {

    }


    public void sendMail(String subject, String messageBody, String fileAttachment) {
        Session session = createSessionObject();
        internetAddress = getSenderEmail();
        try {
            Message message = createMessage(subject, messageBody, fileAttachment, session);
//            new SendMailTask().execute(message);

            SendEmailRunnable emailRunnable = new SendEmailRunnable(message);

            new Thread(emailRunnable).start();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String subject, String messageBody, String fileAttachment, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(internetAddress);

        //Set recipient
        String primaryEmail = SharePreferenceManager.getInstance().getPrimaryEmail();
        String secondaryEmail = SharePreferenceManager.getInstance().getSecondaryEmail();
        if(!primaryEmail.isEmpty()){
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(primaryEmail  , primaryEmail));
        }
        if(!secondaryEmail.isEmpty()){
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(secondaryEmail  , secondaryEmail));
        }

        //Set subject
        message.setSubject(subject);
        message.setText(messageBody);


        //Set attachment
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(messageBody);
        Multipart multipart = new MimeMultipart();
        FileDataSource source = new FileDataSource(fileAttachment);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(fileAttachment);
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);


        LogUtils.logI(EmailManager.class.getName(), "File attachement: " + fileAttachment);

        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {

            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(internetAddress.getAddress(), (internetAddress.getAddress().equalsIgnoreCase(accName1) == true ? accP1: accP2));
            }
        });


    }

    private class SendEmailRunnable implements Runnable{
        Message message;
        public SendEmailRunnable(Message message){
            this.message = message;
        }
        @Override
        public void run() {

            try {
                LogUtils.logI(EmailManager.class.getName(), "Sending email...");
                Transport.send(message);
                LogUtils.logI(EmailManager.class.getName(), "End");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }


    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LogUtils.logI(EmailManager.class.getName(), "Sending email...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LogUtils.logI(EmailManager.class.getName(), "End");
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private InternetAddress getSenderEmail(){
        InternetAddress internetAddress = new InternetAddress();
        Random random = new Random();

        int number = random.nextInt(100);
        if((number % 2) == 0){
            internetAddress.setAddress(accName2);
            try {
                internetAddress.setPersonal(accName2);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            internetAddress.setAddress(accName1);
            try {
                internetAddress.setPersonal(accName1);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        LogUtils.logI(EmailManager.class.getName(), "Number: " +  number + " .Sender email: " + internetAddress.getAddress());

        return internetAddress;
    }



}
