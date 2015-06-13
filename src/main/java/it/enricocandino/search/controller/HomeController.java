package it.enricocandino.search.controller;

import it.enricocandino.search.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Enrico Candino
 */
@Controller
public class HomeController {

    @Autowired
    private UserSession userSession;

    @RequestMapping("/")
    public String index(ModelMap map) {

        map.put("user", userSession);

        return "index";
    }

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

}
