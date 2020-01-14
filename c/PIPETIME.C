/*	inserts current date and time into the pipeline		*/

#include <stdio.h>
#include <time.h>
#include <sys\types.h>
#include <sys\timeb.h>


void put2(char *buf, int val)
{
    int digit;
    *(buf+0) = '0' + (char)(digit=(val/10));
    *(buf+1) = '0' + (char)(val-digit*10);
}


int main(void)
{
    struct _timeb timebuffer;
    struct tm now;
    char *buffer;

    _ftime(&timebuffer);
    now = *localtime(&timebuffer.time);

    buffer = "00/00/0000 00:00:00.00am ";
    /*  index:0....v....1....v....2....v */
    put2(buffer+0,now.tm_mon+1);
    put2(buffer+3,now.tm_mday);
    put2(buffer+6,(now.tm_year + 1900) / 100);
    put2(buffer+8,(now.tm_year % 100));
    if (now.tm_hour >= 12)
    {
        *(buffer+22) = 'p';
	if (now.tm_hour > 12) now.tm_hour -= 12;
    }
    else
    {
        *(buffer+22) = 'a';
	if (now.tm_hour == 0) now.tm_hour = 12;
    }
    put2(buffer+11,now.tm_hour);
    if (*(buffer+11)=='0') *(buffer+11) = ' ';
    put2(buffer+14,now.tm_min);
    put2(buffer+17,now.tm_sec);
    put2(buffer+20,timebuffer.millitm/10);
    fputs(buffer, stdout);
    return(0);
}

