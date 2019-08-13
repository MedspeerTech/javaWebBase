package test.piotics.builder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.piotics.common.TokenType;
import com.piotics.model.Token;

public class TokenBuilder implements Builder<Token> {

	private String id = "5d38540043cb13581a800526";
	private String userId = "5d38540043cb13581a800525";
	private String username = "dijofrancis01@gmail.com";
	private String token = "jhp5nhmes355hs28pt850vgok0";
	private Date creationDate = new Date(System.currentTimeMillis());
	private TokenType tokenType = TokenType.INVITATION;

	public Token build() {
		Token tokenObj = new Token(userId, username, token, tokenType, creationDate);
		tokenObj.setId(id);
		return tokenObj;
	}

	public static TokenBuilder aToken() {

		return new TokenBuilder();
	}

	public TokenBuilder withId(String id) {
		this.id = id;
		return this;
	}

	public TokenBuilder withUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public TokenBuilder withUsername(String username) {
		this.username = username;
		return this;
	}

	public TokenBuilder withToken(String token) {
		this.token = token;
		return this;
	}

	public TokenBuilder withCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public TokenBuilder withTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
		return this;
	}

	public TokenBuilder but() {
		return new TokenBuilder()
				.withId(id)
				.withUserId(userId)
				.withUsername(username)
				.withToken(token)
				.withCreationDate(creationDate)
				.withTokenType(tokenType);
	}
	
	public static String toISO8601UTC(Date date) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		return df.format(date);
	}

	public static Date fromISO8601UTC(String dateStr) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);

		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

}
