package medspeer.tech.controller;

import medspeer.tech.model.ResponseObj;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;


@RestController
@EnableAutoConfiguration
public class HomeController {

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ModelAndView landingPage()
    {
        return new ModelAndView("index.html");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/session")
    public ResponseEntity<ResponseObj> getSession(Principal principal)
    {
        if(principal==null){

            ResponseObj responseObj = new ResponseObj();
            responseObj.setBody(null);
            responseObj.setStatus(401);
            return new ResponseEntity(responseObj,HttpStatus.OK);
        }

        ResponseObj responseObj = new ResponseObj();
        responseObj.setBody("{\"username\":\"vishnu\",\"token\":\"\"}");
        responseObj.setStatus(200);
        return new ResponseEntity<ResponseObj>(responseObj,HttpStatus.OK);
    }
}
