package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.MailManager;
import com.piotics.common.TokenType;
import com.piotics.model.EMail;
import com.piotics.model.Tenant;
import com.piotics.model.Token;

@Service
public class MailService {
	
	@Autowired
	MailManager mailManager;

	public void sendMail(Token token) {

//		Token token = tokenService.getTokenForEmailVerification(appUser);
		EMail email = new EMail();
		if (token.getTokenType() == TokenType.EMAILVERIFICATION) {

			email = mailManager.composeSignupVerificationEmail(token);

		} else if (token.getTokenType() == TokenType.INVITATION) {

			email = mailManager.composeInviteVerificationEmail(token);
		} else if (token.getTokenType() == TokenType.PASSWORDRESET) {

			email = mailManager.composeForgotPasswordMail(token);
		} else if (token.getTokenType() == TokenType.MAIL_RESET) {

			email = mailManager.composeMailResetVerificationEmail(token);
		}
		mailManager.sendEmail(email);
	}

	public void notifyOwnerOnTenantCreation(Tenant tenant) {
		EMail eMail = mailManager.composeTenantCreationNotificationEmail(tenant);
		mailManager.sendEmail(eMail);
	}
	
}
