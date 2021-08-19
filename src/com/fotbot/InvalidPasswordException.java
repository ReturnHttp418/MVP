package com.fotbot;

public class InvalidPasswordException extends Exception
{
    public InvalidPasswordException(String password)
    {
        super("Password " + password + " does not comply with the requirements\n" +
	      "\t- must contains at least " +
	      FotBot.MINIMUM_PASSWORD_LENGTH + " characters\n" + 
              " and one special character that is not a letter or digit");
    }
}
