package medspeer.tech.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//import org.springframework.data.annotation.Id;

import medspeer.tech.common.TokenType;


@Entity
@Table(name = "Token")
public class Token {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
	//private String email;
	private String username;
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private String token;
    private Date creationDate;
    private TokenType tokenType;

    public Token(String username, String token, TokenType tokenType, Date currentTimestamp) {
        this.username = username;
        this.token = token;
        this.tokenType = tokenType;
        this.creationDate = currentTimestamp;
    }

    public Token(String username, String token, TokenType tokenType) {
        this.username = username;
        this.token = token;
        this.tokenType = tokenType;
    }

    public Token() {
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

   /* public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }*/

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }


}
