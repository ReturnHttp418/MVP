package com.fotbot;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * A FotBot looks like a wristwatch and includes functions for
 * counting the steps of the wearer. This data is uploaded into a
 * cloud-based system. The FotBot and its cloud application has three
 * main intended features:
 *  - To record the number of steps a person takes each day.
 *  - To share information with other FotBot wearers for social
      reasons; e.g.competitions to see who can take the most steps.
 * - To share information with the FotBot company.
 *
 * Each FotBot's data is stored in the cloud. Each user can set access
 * permissions to their own data, described below.
 *
 * The server code is below. For simplicity for the assignment, the
 * database is implemented as a Java data structure.
 */
public class FotBot 
{
    /** The minimum length of a username */
    public final static int MINIMUM_USERNAME_LENGTH = 4;

    /** The minimum length of a password */
    public final static int MINIMUM_PASSWORD_LENGTH = 8;

    //The admin username and password
    protected final static String ADMIN_USERNAME = "admin";
    protected final static String ADMIN_PASSWORD = "admin_password1"; //Unbreakable!

    //The passwords for all users (non encrypted!!!)
    //I'm not claiming this code maintains privacy! :)
    private Map<String, String> passwords;

    //The number of steps a user has taken in the past
    private Map<String, List<Integer>> stepData;

    //The last update for a user
    private Map<String, Date> latestUpdate;

    //The friends of users
    private Map<String, Set<String>> friends;

    //The current day
    private Date currentDay;

    /**
     * Constructs a new FotBot server with no users
     */
    public FotBot()
    {
	passwords = new HashMap<String, String>();
        stepData = new HashMap<String, List<Integer>>();
        latestUpdate = new HashMap<String, Date>();
        friends = new HashMap<String, Set<String>>();
	currentDay = new Date(1, 1, 2021);

	passwords.put(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    /**
     * Registers a new user.
     *
     * The username must be at least four characters long and has no other contraints.
     *
     * The password must conform to the following requirements:
     *  - Must be at least eight characters long
     *  - Must contain at least one special ASCII character other than a letter or digit (that is, 
     *       other than a-z, A-Z, or 0-9)
     *
     * @param username   the username for the user to be added
     * @param password   the password for the user
     * @throws DuplicateUserException   if the username is already registered
     * @throws InvalidUsernameException  if the username does not fit
     *          the requirements
     * @throws InvalidPasswordException  if the password does not fit
     *          the requirements 
     *
     * Assumption: username and password are non-null
     */
    public void register(String username, String password)
	throws DuplicateUserException, InvalidUsernameException, InvalidPasswordException
    {
	//Check if this user exists
	if (passwords.containsKey(username)) {
	    throw new DuplicateUserException(username);
	}
	//Check that the username and password are long enough
	else if (username.length() < MINIMUM_USERNAME_LENGTH) {
	    throw new InvalidUsernameException(username);
	}
        else if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new InvalidPasswordException(password);
        }
	else {
	    //check the password contains at least one special character
            boolean special = false;
	    for(char c : password.toCharArray()) {
		if (!('a' <= c && c <= 'z') && !('A' <= c && c <= 'Z') && !('0' <= c && c <= '9')) {
                    special = true;
		    break;
		}
	    }
            if (!special) {
                throw new InvalidPasswordException(password);
            }
	}

	passwords.put(username, password);
        stepData.put(username, new ArrayList<Integer>());
        latestUpdate.put(username, currentDay.copy());
        friends.put(username, new HashSet<String>());
    }

    /**
     * Checks if a user exists
     * @param username  the username
     * @return  true if and only if this user is registered
     *
     * Assumption: username is non-null
     */
    public boolean isUser(String username)
    {
	return passwords.containsKey(username);
    }

    /**
     * Updates the steps for a sequence of previous days by adding the
     * new steps to the user's steps. The update is made from the
     * current day back to the date of the last update. If the length
     * of 'steps' is less than the number of days since the last
     * update, it updates those remaining days with 0. That is, it
     * updates as many days as possible and then backfills the rest
     * with 0. 
     * 
     * For example, if the last update is three days ago, and
     * steps is [3000, 5000], then update [0, 3000, 5000].
     *
     * @throws  NoSuchUserException if the user does not have an account
     * @throws  IncorrectPasswordException if the password is incorrect for this user
     * 
     * Assumption: the length of 'steps' is always less than or equal
     * to numbers of days since the last update
     * 
     * Assumption: username and password are non-null
     *
     * Assumption: 'steps' records the order of the days from oldest
     * (at index 0) to most recent (at index steps.length - 1)
     */
    public void update(String username, String password, List<Integer> steps)
        throws NoSuchUserException, IncorrectPasswordException
    {
        if (checkUsernamePassword(username, password)) {
	    Date userLastUpdate = latestUpdate.get(username);
	    Date day = currentDay.copy();
	    int i = steps.size() - 1;

	    //Add zeros to the start of the list if required
	    List<Integer> newUpdates = new ArrayList<Integer>();
	    while (!day.equals(userLastUpdate)) {
		if (i >= 0) {
		    newUpdates.add(0, steps.get(i));
		}
		else {
		    newUpdates.add(0, 0);
		}
		i--;
		day.decrement();
	    }

	    //Add the new data and update the latest update for this user
	    stepData.get(username).addAll(newUpdates);
	    latestUpdate.put(username, currentDay.copy());
	}
    }

    /**
     * Add a friend to a user's set of friends.
     *
     * @param username the username
     * @param password the password
     * @param friendUsername the friend's username
     *     
     * @throws  NoSuchUserException if the user does not have an account
     * @throws  IncorrectPasswordException if the password is incorrect for this user
     */
    public void addFriend(String username, String password, String friendUsername)
	throws NoSuchUserException, IncorrectPasswordException
    {
	if (checkUsernamePassword(username, password)) {
	    friends.get(username).add(friendUsername);
	}
    }
    
    /**
     * Check if someone is a friend of another person. Anyone can read this information
     *
     * Assumption: username and friendUsername are both non-null
     */
    public boolean isFriend(String username, String friendUsername)
    {
	return friends.get(username).contains(friendUsername);
    }

    /**
     * Read the step data from a user. A user should be able to read step data if and only if:
     * - the data they are reading is their own; 
     * - the data they are reading is their friends; or
     * - 'username' is FotBot.ADMIN_USERNAME
     *
     * If the reading user is not a friend or admin, return an empty list of data
     *
     * @throws  NoSuchUserException if either user ('username' or 'user') does not have an account
     * @throws  IncorrectPasswordException if the password is incorrect for username
     */
    public List<Integer> getStepData(String username, String password, String targetUser)
	throws NoSuchUserException, IncorrectPasswordException
    {
	if (checkUsernamePassword(username, password)) {
	    if (!passwords.containsKey(targetUser)) {
		throw new NoSuchUserException(targetUser);
	    }

	    if (isFriend(targetUser, username) || username.equals(ADMIN_USERNAME) || username.equals(targetUser)) {
		return stepData.get(targetUser);
	    }
	}
	List<Integer> result = new ArrayList<Integer>();
	return result;
    }

    
    /**
     * Increment the current day by a number of days
     */
    public void incrementCurrentDay(int days)
    {
	for (int i = 0; i < days; i++) {
	    currentDay.increment();
	}
    }
    
    /**
     * Check a username and password combination, returning true if the 
     * username and password are correct and throwing an exception otherwise.
     *
     * @throws  NoSuchUserException if the user does not have an account
     * @throws  IncorrectPasswordException if the password is incorrect for this user
     */
    private boolean checkUsernamePassword(String username, String password)
	throws NoSuchUserException, IncorrectPasswordException
    {
        //Check that the user exists
	if (!passwords.containsKey(username)) {
	    throw new NoSuchUserException(username);
	}
	//Check the password
	else if (!passwords.get(username).equals(password)) {
	    throw new IncorrectPasswordException(username, password);
	}

        return true;
    }
}
