package dev.alphacentaurii.RETROWARE.enums;

public enum UserRegistrationStatus {
    
    OK("Registered successfully!"),
    USER_ALREADY_EXISTS("Username not available."),
    PASSWORD_CONFIRMATION_FAILED("Password and password confirmation fields do not match."),
    PASSWORD_UNACCEPTABLE("Password does not meet minimum requirements."),
    USERNAME_UNACCEPTABLE("The username is invalid!");

    public final String message;

    UserRegistrationStatus(String message){
        this.message = message;
    }

}
