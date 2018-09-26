package medspeer.tech.learn;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by vishnu on 02/11/17.
 */

@RestController
@RequestMapping(value = "/example")
public class ExampleController {

    static int i=0;

    @RequestMapping(method = RequestMethod.GET, value = "/cache")
    @Cacheable("testCache")
    public int testCachingOfVariable(Principal principal)
    {
        i=i+1;
        return i;
    }
}
