package com.fotbot;

public class InvalidUsernameException extends Exception
{
    public InvalidUsernameException(String username)
    {
        super("Username " + username + " does not comply with the requirements\n" +
	      "\t- must contains at least " +
	      FotBot.MINIMUM_USERNAME_LENGTH + " characters");
    }
}
