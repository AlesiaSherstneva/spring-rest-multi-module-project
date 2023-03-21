package by.academy.it.exceptions;

public class UserNotCreatedException extends RuntimeException {
    public UserNotCreatedException(String message) {
        super(message);
    }
}