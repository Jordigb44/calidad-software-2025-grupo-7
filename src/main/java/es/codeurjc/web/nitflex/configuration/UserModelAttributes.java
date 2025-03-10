package es.codeurjc.web.nitflex.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.codeurjc.web.nitflex.service.UserComponent;

@ControllerAdvice
public class UserModelAttributes {

    @Autowired
    private UserComponent userComponent;

    @ModelAttribute("currentUserName")
    public String userName() {
        return userComponent.getUser().getName();
    }

    @ModelAttribute("favoritesNum")
    public Integer favorites() {
        return userComponent.getUser().getFavoriteFilms().size();
    }

}
