package medspeer.tech.config;

import medspeer.tech.model.SocialUser;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/social")
public class SocialController {

	@RequestMapping(method = RequestMethod.POST,value = "/login")
	public void facebookLogin(@RequestBody SocialUser socialUser){
		if(socialUser.getProvider().equals("facebook")) {
			Facebook facebook = new FacebookTemplate(socialUser.getToken());
			PagedList<User> friends = facebook.friendOperations()
					.getFriendProfiles();

			for (User profile : friends) {

				System.out.println(profile.getId());
			}
		}else if(socialUser.getProvider().equals("google")) {
			Google google=new GoogleTemplate(socialUser.getToken());
			google.plusOperations().getGoogleProfile();
		}
	}
}
