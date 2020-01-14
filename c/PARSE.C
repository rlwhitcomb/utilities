/*******************************************************************************
*       Date/Time Parsing Functions                                            *
*******************************************************************************/
#include <stdlib.h>
#include <time.h>
#include "parse.h"

static unsigned char days[12] = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
#define LEAPTEST	2100		/* only funny leap year in range */
#define leapyear(y)	((y & 3) == 0 && y != LEAPTEST)
#define ispunct(c)      ((c) == '/' || (c) == '-' || (c) == '.' || (c) == ';'\
                      || (c) == ',' || (c) == '_')
#define isdigit(c)      ((c) >= '0' && (c) <= '9')

/*******************************************************************************
*	Parse a string into month/day/year parts			       *
*									       *
*	Returns:    1 if successful					       *
*		    0 if input string is empty				       *
*		   -1 if some other error				       *
*******************************************************************************/
int parse_date(char *ptr, struct tm *date, char **endptr)
{
    int ret = -1;
    unsigned long l;
    unsigned int month, day;
    unsigned int year;

    *endptr = ptr;
    if (*ptr && isdigit(*ptr))
    {
	/* Set defaults from current date */
        month = date->tm_mon;       /* 0..11 */
        day   = date->tm_mday;      /* 1..31 */
        year  = date->tm_year;      /* year since 1900 */

	/* Month */
	l = strtoul(ptr, endptr, 10);
	if (l <= 12 && l >= 1)
	{
            month = (unsigned int)(l) - 1;
	    ptr = *endptr;
	    if (*ptr)
	    {
		if (ispunct(*ptr) && isdigit(*(ptr+1)))
		{
		    ptr++;
		    /* Day */
		    l = strtoul(ptr, endptr, 10);
		    if (l <= 31 && l >= 1)
		    {
                        day = (unsigned int)l;
			ptr = *endptr;
			if (*ptr)
			{
			    if (ispunct(*ptr) && isdigit(*(ptr+1)))
			    {
				ptr++;
				/* Year */
				l = strtoul(ptr, endptr, 10);
                                /* Accept four digit years from 1900 to 2399 */
                                if (l <= 2399 && l >= 1900)
				{
                                    year = (unsigned int)(l) - 1900;
				    ret = 1;
				}
                                /* Handle two-digit years: */
                                /* 80-99 are 1980 to 1999 */
                                else if (l <= 99 && l >= 80)
				{
                                    year = (unsigned int)l;
				    ret = 1;
				}
                                /* 00-79 are 2000 to 2079 */
                                else if (l >= 0 && l < 80)
                                {
                                    year = (unsigned int)(l) + 2000 - 1900;
                                    ret = 1;
                                }
			    }
			}
			else
			{
			    /* Default year is current year if none given */
			    ret = 1;
			}
		    }
		}
	    }
	    else
	    {
		/* Default day is 1 if only month given */
		day = 1;
		ret = 1;
	    }
	}

	/* Check for valid day number within month */

	if (ret > 0)
	{
            if (day > days[month] ||
                (!leapyear(year + 1900) && month == 1 && day == 29))
	    {
		ret = -1;
	    }
	}

	/* If all parsing succeeded, set new date */

	if (ret > 0)
	{
            date->tm_mon  = month;
            date->tm_mday = day;
            date->tm_year = year;
	}
    }
    /* Signal an empty string */
    else
    {
	ret = 0;
    }
    return ret;
}


int parse_time(char *ptr, struct tm *tm, char **endptr)
{
    int ret = -1;
    int c;
    unsigned long l;
    unsigned int hour, minute, second;

    *endptr = ptr;
    if (*ptr && isdigit(*ptr))
    {
	/* Set defaults from current time */
        hour   = tm->tm_hour;   /* 0..23 */
        minute = tm->tm_min;    /* 0..59 */
        second = tm->tm_sec;    /* 0..59 */

	/* Hour */
	l = strtoul(ptr, endptr, 10);
	if (l <= 23)
	{
            hour = (unsigned int)l;
	    minute = 0;
	    second = 0;
	    ptr = *endptr;
            if (*ptr == ':' && isdigit(*(ptr+1)))
	    {
		ptr++;
		/* Minute */
		l = strtoul(ptr, endptr, 10);
		if (l <= 59)
		{
                    minute = (unsigned int)l;
		    ptr = *endptr;
		}
                if (*ptr == ':' && isdigit(*(ptr+1)))
		{
		    ptr++;
		    /* Second */
		    l = strtoul(ptr, endptr, 10);
		    if (l <= 59)
		    {
                        second = (unsigned int)l;
			ptr = *endptr;
		    }
		}
	    }
	    if (*ptr)
	    {
		/* Check for AM/PM indicator */
		c = *ptr++;
                if (c == 'A' || c == 'a' || c == 'P' || c == 'p')
		{
                    if (!*ptr || ((*ptr == 'M' || *ptr == 'm') && !*(ptr+1)))
		    {
			if (*ptr)
			    ptr++;

                        /* Can't use hour == 0 or > 12 with AM/PM */
			if (hour >= 1 && hour <= 12)
			{
                            if (c == 'A' || c == 'a')
			    {
				if (hour == 12)
				{
				    hour = 0;
				}
			    }
			    else
			    {
				if (hour < 12)
				{
				    hour += 12;
				}
			    }
			    ret = 1;
			}
		    }
		}
		*endptr = ptr;
	    }
	    else
	    {
		ret = 1;
	    }
	}

	/* If all parsing succeeded, set new time */

	if (ret > 0)
	{
            tm->tm_hour = hour;
            tm->tm_min  = minute;
            tm->tm_sec  = second;
	}
    }
    /* Signal empty string */
    else
    {
	ret = 0;
    }
    return ret;
}

/* End of file PARSE.c */
