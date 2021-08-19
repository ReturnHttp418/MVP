package com.fotbot;

public class IncorrectPasswordException extends Exception 
{
    public IncorrectPasswordException(String username, String password)
    {
        super("Incorrect password: " + password + " for user " + username);
    }
}
