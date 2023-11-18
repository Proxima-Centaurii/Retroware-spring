package dev.alphacentaurii.RETROWARE.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController{
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model){

        log.trace("Accessed the error page.");

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status == null){
            log.trace("Error status is null.");
            return "redirect:/home";
        }

        HttpStatus statusCode = HttpStatus.valueOf(Integer.valueOf(status.toString()));

        model.addAttribute("error_title", statusCode.value() + " " + statusCode.name().replace('_', ' '));

        switch (statusCode) {
            case NOT_FOUND:
                log.trace("Error status is 404 Not Found.");
                model.addAttribute("error_message", "The resource requested could not be found.");
                break;
        
            case INTERNAL_SERVER_ERROR:
                log.trace("Error status is 500 Internal server error.");
                model.addAttribute("error_message", "An error occured while processing your request.");
                break;

            default:
                log.trace("Unhandled error.");
                log.trace("Error: " + statusCode.value() + " - " + statusCode.name());
                model.addAttribute("error_message", "Something went wrong.");
                break;
        }

        return "error";
    }

}//End of class
