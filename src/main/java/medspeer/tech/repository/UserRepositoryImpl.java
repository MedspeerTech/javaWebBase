package medspeer.tech.repository;

import medspeer.tech.model.Attachment;
import medspeer.tech.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository{

	@Autowired
    ImageService imageService;

	@Override
	public void updateProfileImage(Attachment attachment) {

		imageService.createProfileImage(attachment);

	}

}
