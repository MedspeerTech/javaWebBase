package medspeer.tech.common;


import medspeer.tech.model.EMail;

import medspeer.tech.model.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


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

    @Autowired
    JavaMailSender mailSender;


    public void sendEmail(EMail email) {

            MimeMessagePreparator mm = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws MessagingException {
                            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                            message.setFrom(fromEmail);
                            message.setTo(email.getToAddress());
                            message.setSubject(email.getSubject());
                            message.setText(email.getMessage(), true);
//                    message.addAttachment("CoolStuff.doc", new File("CoolStuff.doc"));
                    }
            };
//            mailSender.send(mm);
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
}

