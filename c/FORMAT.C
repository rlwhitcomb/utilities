/*******************************************************************************
*	Utility subroutines						       *
*******************************************************************************/
#include <string.h>
#include <stdlib.h>

#include "util.h"


/*******************************************************************************
*	Reformat number string into thousands-separated form		       *
*******************************************************************************/
void reformat_number(char *buf)
{
    unsigned int len, ilen;
    char *ptr;

    /* Must skip leading sign in length check */

    if (*buf == '-' || *buf == '+')
	buf++;

    /* Find length of integer part (before decimal point or other stuff) */

    len = strlen(buf);
    for (ptr = buf; *ptr; ptr++)
        if (*ptr < '0' || *ptr > '9')
            break;
    ilen = (unsigned int)(ptr - buf);

    /* If number >= 1000, must put thousands separators in (sprintf can't do) */

    if (ilen > 3)
    {
	for (ptr = &buf[ilen-3]; ptr > buf; ptr -= 3)
	{
	    len++;
	    memmove(ptr + 1, ptr, len - (ptr - buf));
            *ptr = ',';
	}
    }
}


/*******************************************************************************
*	Format long integer into thousands-separated form		       *
*******************************************************************************/
void format_long(long num, char *buf, int unsign)
{
    /* Convert number to string (left justified) */

    if (unsign)
	_ultoa((unsigned long)num, buf, 10);
    else
	_ltoa(num, buf, 10);

    reformat_number(buf);
}


/*******************************************************************************
*       Format 8-byte integer into thousands-separated form                    *
*******************************************************************************/
#if _MSC_VER > 1000
void format_int64(__int64 num, char *buf, int unsign)
{
    /* Convert number to string (left justified) */

    if (unsign)
        _ui64toa((unsigned __int64)num, buf, 10);
    else
        _i64toa(num, buf, 10);

    reformat_number(buf);
}
#endif /* _MSC_VER > 1000 */
