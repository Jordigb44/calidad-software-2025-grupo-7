package es.codeurjc.web.nitflex.configuration;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;

@ControllerAdvice(basePackages = "es.codeurjc.web.nitflex.controller.web")
public class WebErrorHandler {

    /**
	 * When a 'FilmNotFound' exception occurs, the following method is executed
	 * @param ex
	 * @return a view with a message indicating the error
	 */
	@ExceptionHandler({FilmNotFoundException.class, IllegalArgumentException.class, BindException.class})
    public ModelAndView handleException(Exception ex){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("message");
		modelAndView.addObject("error", true);

		if(ex instanceof MethodArgumentNotValidException manvExp){
			modelAndView.addObject("message", manvExp.getFieldError().getDefaultMessage());
		}else{
			modelAndView.addObject("message", ex.getMessage());
		}

        return modelAndView;
    }

}
