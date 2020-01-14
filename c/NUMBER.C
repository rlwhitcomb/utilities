/*******************************************************************************
*	Format a number into a text representation			       *
*******************************************************************************/
#include <stdio.h>
#include <ctype.h>
#include "util.h"

static char *small_strings[] =
{
    "NONE",
    "one",
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
    "ten",
    "eleven",
    "twelve",
    "thirteen",
    "fourteen",
    "fifteen",
    "sixteen",
    "seventeen",
    "eighteen",
    "nineteen"
};
static char *decade_strings[] =
{
    "twenty",
    "thirty",
    "forty",
    "fifty",
    "sixty",
    "seventy",
    "eighty",
    "ninety"
};


unsigned int number_string(char *buf, unsigned long num, int capitalize)
{
    char *ptr, sep;

    ptr = buf;
    sep = ' ';
    while (num >= 20L)
    {
	if (ptr > buf)
	    *ptr++ = sep;

	if (num >= 1000000000L)
	{
	    ptr += number_string(ptr, num / 1000000000L, 0);
            ptr += sprintf(ptr, " billion");
	    num %= 1000000000L;
	}
	else if (num >= 1000000L)
	{
	    ptr += number_string(ptr, num / 1000000L, 0);
            ptr += sprintf(ptr, " million");
	    num %= 1000000L;
	}
	else if (num >= 1000L)
	{
	    ptr += number_string(ptr, num / 1000L, 0);
            ptr += sprintf(ptr, " thousand");
	    num %= 1000L;
	}
	else if (num >= 100L)
	{
            ptr += sprintf(ptr, "%s hundred", small_strings[(num / 100L)]);
	    num %= 100L;
	}
	else if (num >= 20L)
	{
            ptr += sprintf(ptr, "%s", decade_strings[(num / 10L) - 2]);
	    num %= 10L;
	    if (num != 0L)
	    {
                sep = '-';
	    }
	}
    }
    if (num != 0L || ptr == buf)
    {
	if (ptr > buf)
	    *ptr++ = sep;

	ptr = stpcpy(ptr, small_strings[num]);
    }

    if (capitalize)
	*buf = (char)toupper(*buf);

    return (unsigned int)(ptr - buf);
}



