package medspeer.test.repository;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;

import medspeer.tech.repository.UserRepositoryImpl;
import medspeer.tech.service.ImageService;
import medspeer.tech.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryImplTest {

	@Value("${meds.storage.location}")
	String storageLocationPath;

	@Value("${meds.storage.profile.location}")
	String profileLocationUrl;
	
	@InjectMocks
	UserRepositoryImpl userRepositoryImpl;
	
	@Mock
	ImageService imageService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
	}
	
}
