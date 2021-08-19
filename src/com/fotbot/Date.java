package com.fotbot;

public class Date
{
    //a mapping from month numbers to month names
    final private static String [] MONTHS =
	new String [] {"January",
		       "February",
		       "March",
		       "April",
		       "May",
		       "June",
		       "July",
		       "August",
		       "September",
		       "October",
		       "November",
		       "December"};
  
    //the day, month, and year of this date
    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year)
    {
	this.day = day;
	this.month = month;
	this.year = year;
    }

    public int getDay()
    {
	return day;
    }

    public int getMonth()
    {
	return month;
    }

    public int getYear()
    {
	return year;
    }

    
    /**
     * Increment the date
     */
    public void increment()
    {
	if (day < monthDuration(month, year)) {
	    day++;
	}
	else if (month < 12) {
	    day = 1;
	    month++;
	}
	else {
	    day = 1;
	    month = 1;
	    year++;
	}
    }

    /**
     * Decrement the date
     */
    public void decrement()
    {
	if (day > 1) {
	    day--;
	}
	else if (month > 1) {
	    month--;
	    day = monthDuration(month, year);
	}
	else {
	    month = 12;
	    year--;
	    day = monthDuration(month, year);
	}
    }

    /**
     * Calculates the month duration.
     */
    private static int monthDuration(int month, int year)
    {
	//Adjust February for leap years
	if (month == 2  && (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))) {
	    return 29;
	}
	else if (month == 2) {
	    return 28;
	}
	else if (month == 4 || month == 6 || month == 9 || month == 11) {
	    return 30;
	}
	return 31;
    }

    public Date copy()
    {
	return new Date(day, month, year);
    }
    
    public boolean equals(Object o)
    {
	if (o instanceof Date) {
	    Date date = (Date) o;
	    return day == date.day && 
		month == date.month && 
		year == date.year;
	}
	return false;
    }

    public String toString()
    {
	String result = day + " " + MONTHS[month - 1] + " " + year;
	return result;
    }
}
