package medspeer.tech.repository;


import medspeer.tech.model.Attachment;

public interface UserRepository {
	
	void updateProfileImage(Attachment attachment);

}
