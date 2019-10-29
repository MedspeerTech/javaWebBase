package test.piotics.builder;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.piotics.model.Invitation;
import com.piotics.model.Token;
import com.piotics.model.UserShort;

public class InvitationBuilder implements Builder<Invitation> {

	private String id;
	private String email;
	private String phone;
	private UserShort invitedBY;
	private Token token;
	private String tenantId;

	@Override
	public Invitation build() {
		Invitation invitation = new Invitation();
		invitation.setEmail(email);
		invitation.setInvitedBY(invitedBY);
		invitation.setPhone(phone);

		return invitation;
	}

	public InvitationBuilder anInvitation() {
		return new InvitationBuilder();
	}
	
	public InvitationBuilder withId(String id) {
		this.id = id;
		return this;
	}
	
	public InvitationBuilder withEmail(String email) {
		this.email = email;
		return this;
	}
	
	public InvitationBuilder withPhone(String phone) {
		this.phone = phone;
		return this;
	}
	
	public InvitationBuilder withInvitedBy(UserShort invitedBY) {
		this.invitedBY = invitedBY;
		return this;
	}
	
	public InvitationBuilder withToken(Token token) {
		this.token = token;
		return this;
	}
	
	public InvitationBuilder withTenantId(String tenantId) {
		this.tenantId = tenantId;
		return this;
	}
}
