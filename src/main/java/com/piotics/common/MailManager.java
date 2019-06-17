package com.piotics.common;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import com.piotics.model.EMail;
import com.piotics.model.Token;


@Component
public class MailManager {
	
	

	@Value("${application.url}")
	String webUrl;

    @Value("${spring.mail.username}")
    String fromEmail;
    
    @Value("${mail.verfication.subject}")
    String verificationMailSubject;
    
    @Value("${mail.verfication.message}")
    String VerificationMailMessage;

    @Value("${mail.reset.password.subject}")
    String resetMailSubject;
    
    @Value("${mail.reset.password.message}")
    String resetMailMessage;
    
    @Value("${mail.invititation.subject}")
    String invititationMailSubject;
      
    @Value("${mail.invitation.message}")
    String inviteMailMessage;
    
    @Autowired
    JavaMailSender mailSender;


    public void sendEmail(final EMail email) {

            MimeMessagePreparator mm = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws MessagingException {
                            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                            message.setFrom(fromEmail);
                            message.setTo(email.getToAddress());
                            message.setSubject(email.getSubject());
                            message.setText(email.getMessage(), true);
//                            message.addAttachment("CoolStuff.doc", new File("CoolStuff.doc"));
                    }
            };
            
            mailSender.send(mm);
    }
    
    public EMail composeSignupVerificationEmail(Token token) {
    	String verificationUrl=webUrl + "completeRegistration/" + token.getUsername()+"/"+token.getToken() ;
    	EMail email=new EMail();
    	email.setToAddress(token.getUsername());
    	email.setSubject(verificationMailSubject);
    	email.setMessage(verificationUrl);
    	return email;
    	
    }
    
    public EMail composeForgotPasswordMail(Token token) {
    	String verificationUrl=webUrl + "resetPassword/" + token.getUsername()+"/"+token.getToken() ;
    	EMail email=new EMail();
    	email.setToAddress(token.getUsername());
    	email.setSubject(resetMailSubject);
    	email.setMessage(verificationUrl);
    	return email;
    	
    }

	public EMail composeInviteVerificationEmail(Token token) {
		String invitationUrl=webUrl + "signup/" + token.getUsername()+"/"+token.getToken() ;
    	EMail email=new EMail();
    	email.setToAddress(token.getUsername());
    	email.setSubject(invititationMailSubject);
    	email.setMessage(invitationUrl);
    	return email;
	}
public boolean isEmail(String userName) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(userName);

        if(mat.matches()){

           return true;
        }else{

            return false;
        }
	}
}

