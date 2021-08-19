package com.fotbot;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class PartitioningTests
{
    protected FotBot fotbot;

    //Any method annotated with "@Before" will be executed before each test,
    //allowing the tester to set up some shared resources.
    @Before public void setUp()
	throws DuplicateUserException, InvalidUsernameException, InvalidPasswordException
    {
	fotbot = new FotBot();
	fotbot.register("userName1", "password1!");
    }

    //Any method annotated with "@After" will be executed after each test,
    //allowing the tester to release any shared resources used in the setup.
    @After public void tearDown()
    {
    }

    //Any method annotation with "@Test" is executed as a test.
    @Test public void aTest()
    {
	//the assertEquals method used to check whether two values are
	//equal, using the equals method
	final int expected = 2;
	final int actual = 1 + 1;
	assertEquals(expected, actual);
    }

    @Test public void anotherTest()
	throws DuplicateUserException, InvalidUsernameException, InvalidPasswordException
    {
	fotbot.register("userName2", "password2!");

	//the assertTrue method is used to check whether something holds.
	assertTrue(fotbot.isUser("userName2"));
	assertFalse(fotbot.isUser("nonUser"));
    }

    @Test public void sampleFotBotTest()
	throws NoSuchUserException, IncorrectPasswordException	
    {
	fotbot.incrementCurrentDay(2);

	List<Integer> newSteps = list(new Integer [] {1000, 2000});

	//username1 is created in the setUp() method, which is run before every test
	fotbot.update("userName1", "password1!", newSteps);

	List<Integer> steps = fotbot.getStepData("userName1", "password1!", "userName1");
	List<Integer> expected = list(new Integer [] {1000, 2000});
	assertEquals(expected, steps);	
    }


    //To test that an exception is correctly throw, specify the expected exception after the @Test
    @Test(expected = java.io.IOException.class)
    public void anExceptionTest()
            throws Throwable
    {
        throw new java.io.IOException();
    }

    //This test should fail.
    //To provide additional feedback when a test fails, an error message
    //can be included
    @Test public void aFailedTest()
    {
        //include a message for better feedback
        final int expected = 2;
        final int actual = 1 + 2;
        //Uncomment the following line to make the test fail
        //assertEquals("Some failure message", expected, actual);
    }

    protected List<Integer> list(Integer[] elements)
    {
        return new ArrayList<Integer>(Arrays.asList(elements));
    }

    // Test sets for register()
    // EC1: userName already exist
    @Test (expected = DuplicateUserException.class)
    public void testDuplicateUserExceptionIsThrown() throws Exception {
        fotbot.register("userName1", "password1!");
    }

    // EC2: userName not exist but length < 4
    @Test (expected = InvalidUsernameException.class)
    public void testInvalidUsernameExceptionIsThrown() throws Exception {
        fotbot.register("us", "password1!");
    }

    // EC3: userName not exist, length is >= 4 but password length < 8
    @Test (expected = InvalidPasswordException.class)
    public void testInvalidPasswordExceptionIsThrown_1() throws Exception {
        fotbot.register("userName3", "pass");
    }

    // EC4: userName not exist, length is >= 4 password length >= 8 but no special char
    @Test (expected = InvalidPasswordException.class)
    public void testInvalidPasswordExceptionIsThrown_2() throws Exception {
        fotbot.register("userName3", "password");
    }

    //EC5: both userName and password are valid
    @Test
    public void successfullyRegisterUser() throws Exception{
        fotbot.register("userName3", "password3!");
        assertTrue(fotbot.isUser("userName3"));
    }

    // Test sets for update()
    //EC1: Given userName not exist
    @Test(expected = NoSuchUserException.class)
    public void updateNoSuchUserExceptionIsThrown() throws Exception {
        List<Integer> newSteps = list(new Integer [] {1000, 2000});
        fotbot.incrementCurrentDay(2);
        fotbot.update("userName", "password5!", newSteps);
    }

    //EC2: password incorrect
    @Test(expected = IncorrectPasswordException.class)
    public void updateIncorrectPasswordExceptionIsThrown() throws Exception {
        List<Integer> newSteps = list(new Integer [] {1000, 2000});
        fotbot.incrementCurrentDay(2);
        fotbot.update("userName1", "password5!", newSteps);
    }

    //EC3: current day not equal to latest update
    @Test
    public void currentDayNotEqualToLatestUpdate() throws Exception {
        List<Integer> newSteps = list(new Integer [] {1000, 2000});
        fotbot.incrementCurrentDay(4);
        fotbot.update("userName1", "password1!", newSteps);
        List<Integer> result = fotbot.getStepData("userName1", "password1!", "userName1");
        List<Integer> expected = list(new Integer[] {0, 0, 1000, 2000});
        assertEquals(expected, result);
    }

    // EC4: current day equal to latest update
    @Test
    public void currentDayEqualToLatestUpdate() throws Exception {
        List<Integer> newSteps = list(new Integer [] {1000, 2000});
        fotbot.incrementCurrentDay(2);
        fotbot.update("userName1", "password1!", newSteps);
        List<Integer> result = fotbot.getStepData("userName1", "password1!", "userName1");
        assertEquals(newSteps, result);
    }

    // Test sets for getStepData()
    // EC1: userName not exist
    @Test(expected = NoSuchUserException.class)
    public void getStepDataNoSuchUserExceptionIsThrown() throws Exception {
        fotbot.getStepData("userName", "password1!", "nameUser");
    }

    // EC2: input password is not correct
    @Test(expected = IncorrectPasswordException.class)
    public void getStepDataIncorrectPasswordExceptionIsThrown() throws Exception {
        fotbot.getStepData("userName1", "password5!", "nameUser");
    }

    // EC3: target user not exist
    @Test(expected = NoSuchUserException.class)
    public void targetUserNotExist() throws Exception {
        fotbot.getStepData("userName1", "password1!", "nameUser");
    }

    //EC4: reading friends data
    @Test
    public void readFriendsData() throws Exception {
        fotbot.register("nameUser", "nameUser1!");
        fotbot.addFriend("userName1", "password1!", "nameUser");
        assertTrue(fotbot.isFriend("userName1", "nameUser"));

        List<Integer> newSteps = list(new Integer [] {1000, 2000});
        fotbot.incrementCurrentDay(2);
        fotbot.update("userName1", "password1!", newSteps);
        List<Integer> result = fotbot.getStepData("nameUser", "nameUser1!", "userName1");
        assertEquals(newSteps, result);
    }

    // EC5: admin user can get the target user data
    @Test
    public void adminUserCanReadData() throws Exception {
        List<Integer> newSteps = list(new Integer [] {1000, 2000});
        fotbot.incrementCurrentDay(2);
        fotbot.update("userName1", "password1!", newSteps);
        List<Integer> result = fotbot.getStepData("admin", "admin_password1", "userName1");
        assertEquals(newSteps, result);
    }

    // EC6: user can get own data
    @Test
    public void userCanGetOwnData() throws Exception {
        List<Integer> newSteps = list(new Integer [] {1000, 2000});
        fotbot.incrementCurrentDay(2);
        fotbot.update("userName1", "password1!", newSteps);
        List<Integer> result = fotbot.getStepData("userName1", "password1!", "userName1");
        assertEquals(newSteps, result);
    }

    //EC7: get data condition does not meet
    @Test
    public void getStepDataEmptyListReturned() throws Exception {
        fotbot.register("userName2", "password2!");
        List<Integer> result = fotbot.getStepData("userName1", "password1!", "userName2");
        List<Integer> expected = new ArrayList<>();
        assertEquals(expected, result);
    }

}
