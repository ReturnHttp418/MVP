package com.fotbot;

public class DuplicateUserException extends Exception 
{
    public DuplicateUserException(String username)
    {
        super("Username already exists: " + username);
    }
}
