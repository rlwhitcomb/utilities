#include <fcntl.h>
#include <stdio.h>
#include <dos.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <io.h>
#include "cmdline.h"
#include "parse.h"

/* Program options flags */

static struct tm current_time = { 0 };
static int touch_readonly = 0;



void instruct(void)
{
    printf("Usage: %s [-dmm/dd/yyyy] [-thh:mm:ss] [-q][-i][-h][-s][-y][-r][-?] <Name(s)>\n", progname);
    printf("\tuse -d and/or -t to set specific date/time for file(s).\n");
    printf("\t\tNote: using date only sets time to midnight.\n");
    printf("\tuse -q (quiet) not to report file name(s) touched.\n");
    printf("\tuse -i to ignore any errors encountered.\n");
    printf("\tuse -h to touch hidden files as well.\n");
    printf("\tuse -y to touch system files as well.\n");
    printf("\tuse -r to touch read-only files also (only needed for NT or OS/2).\n");
    printf("\tuse -s to process files in subdirectories also.\n");
    printf("\t  Name(s) can be '@<Indirect file>'.\n");
}


int process(char *path, unsigned attrib, time_t date, long size)
{
    int fh, ret = -1;
    unsigned file_date, file_time;

    if (attrib & A_SUBDIR)
	return 0;

    /* If file is read-only, and touch read-only flag is set, */
    /* try changing to read-write before attempting touch.    */

    if ((attrib & A_RDONLY) && touch_readonly)
    {
	_chmod(path, _S_IWRITE | _S_IREAD);
    }

    if (_dos_open(path, _O_RDWR, &fh) == 0 ||
	_dos_open(path, _O_RDONLY, &fh) == 0)
    {
	if (_dos_setftime(fh, current_date, current_time) == 0)
	{
	    ret = 1;
	}
	_dos_close(fh);
	if (ret > 0)
	{
	    ret = -1;
	    if (_dos_open(path, _O_RDONLY, &fh) == 0)
	    {
		if (_dos_getftime(fh, &file_date, &file_time) == 0)
		{
		    if (file_date == current_date && file_time == current_time)
		    {
			ret = 1;
		    }
		}
	    }
	    _dos_close(fh);
	}
    }

    if ((attrib & A_RDONLY) && touch_readonly)
    {
	_chmod(path, _S_IREAD);
    }

    if (ret > 0)
    {
	if (!quiet)
	{
            printf("\t%s\n", path);
	}
    }
    else
    {
        printf("\n%s: Cannot set date/time for \"%s\".\n", progname, path);
    }
    return ret;
}


int processprogress(int flag, int level, char *arg)
{
    return 0;
}


int processuserarg(char *arg, unsigned flags)
{
    int ret = 0;
    char *endptr;

    switch (*arg)
    {
        case 'D':
        case 'd':
            /* Parse date value */
            if ((ret = parse_date(&arg[1], &current_time, &endptr)) > 0)
	    {
		/* Default time is midnight if date specified */
                parse_time("0", &current_time, &endptr);
	    }
	    break;

        case 'T':
        case 't':
            /* Parse time value */
            ret = parse_time(&arg[1], &current_time, &endptr);
	    break;

        case 'R':
        case 'r':
	    touch_readonly = ret = 1;
	    break;
    }
    return ret;
}


int main(int argc, char *argv[])
{
    time_t timer;

    /* Get current system date & time to set on each file as a default */

    time(&timer);
    current_time = *localtime(&timer);

    /* Process options first (non-positional) */

    if (processcmdline(argc, argv, PROCESS_OPTIONS) < 0 || argc < 2)
    {
	instruct();
        return 1;
    }

    /* Report date being used */

    if (!quiet)
    {
        char buffer[40];

        strftime(buffer, sizeof(buffer), "%c", &current_time);
        printf("Setting date and time of %s for:\n", buffer);
    }

    /* Go through arguments again and process file specs */

    processcmdline(argc, argv, PROCESS_FILES);
    return 0;
}

