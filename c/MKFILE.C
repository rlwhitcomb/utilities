/*******************************************************************************
*                                                                              *
*   Use file list or file spec and command line to construct .BAT or .CMD file:*
*       MKFILE <output> [-options] <filespec> or @<file> "command line"        *
*   File names are substituted at appropriate places in command line:          *
*       %f or %1 = name as given                                               *
*       %n       = only filename part (no path or extension)                   *
*       %p       = only path part (NO trailing '\')                            *
*       %d       = only drive part                                             *
*       %x       = only extension part (NO leading '.')                        *
*       %q       = fully-qualified path and file name                          *
*       %m       = main name (filename + extension)
*   Valid options:                                                             *
*       -a       = append to output file (instead of overwrite)                *
*       -v       = report all files added to file to console                   *
*       -c       = create .CMD file instead of .BAT file (for OS/2)            *
*       -x       = execute .BAT file at end and then delete it (DOS only)      *
*       -s       = look for files in subdirectories also                       *
*       -d       = skip processing directories as files                        *
*	-l or -u = lower or upper case translate file names		       *
*   Error levels:                                                              *
*       0        = some files processed, no problems                           *
*       1        = no files processed                                          *
*       2        = error writing to output file or problem with arguments      *
*                                                                              *
*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <conio.h>
#include <string.h>
#include <limits.h>
#include <time.h>
#include "cmdline.h"
#include "util.h"


/* Flags to remember cmd line options */

static int append  = 0;
static int cmdfile = 0;
static int verbose = 0;
static int execute = 0;
static int xlate   = 0;
static int quote   = 0;
static char *cmdline = NULL;
static char *outline = NULL;
static FILE *outfile = NULL;


void instruct(void)
{
static char *ins[] =
{
    "Use file list or file spec and command line to construct .BAT or .CMD file:",
    "    %s <output> [-options] <filespec> or @<file> \"command line\"",
    "File names are substituted at appropriate places in command line:",
    "    %%f or %%1 = name as given",
    "    %%n       = only filename part (no path or extension)",
    "    %%p       = only path part (NO trailing '\\')",
    "    %%d       = only drive part",
    "    %%x       = only extension part (NO leading '.')",
    "    %%q       = fully-qualified path and file name",
    "    %%m       = main name (filename + extension)",
    "          Use ! for a new line, ^ for tab character",
    "Options: -a = append to output file (instead of overwrite)",
    "         -v = report all files added to file to console",
    "         -c = create .CMD file instead of .BAT file (for OS/2)",
    "         -x = execute .BAT file at end and then delete it (DOS only)",
    "         -s = look for files in subdirectories also",
    "         -d = skip processing directories as files",
    "         -q = quote file names with embedded spaces",
    "   -l or -u = lower or upper case translate file names",
    "Output file will be: xxx.bat by default, xxx.cmd with -c option,",
    "                     redirected to standard output if \"@\" is used, or",
    "                     sent to temporary file if \"*\" is used",
    "Error levels: 0 = some files processed, no problems",
    "              1 = no files processed",
    "              2 = error writing to output file or problem with arguments"
};
    int i;

    for (i = 0; i < sizeof(ins) / sizeof(ins[0]); i++)
    {
        printf(ins[i], progname);
        printf("\n");
    }
}


#define ERR_INVALID_OPTION      (1)
#define ERR_NOT_ENOUGH_ARGS     (2)
#define ERR_CANT_EXEC_CMD       (3)
#define ERR_NOT_ENOUGH_MEMORY   (4)
#define ERR_CANT_OPEN_OUTPUT    (5)
#define ERR_CANT_EXEC_STDOUT    (6)
#define ERR_CANT_CREATE_TEMP    (7)

int error(int code, char *arg)
{
    printf("%s: ", progname);
    switch (code)
    {
        case ERR_INVALID_OPTION:
            printf("Invalid option \"%s\".\n", arg);
            instruct();
            break;

        case ERR_NOT_ENOUGH_ARGS:
            printf("Not enough arguments given.\n");
            instruct();
            break;

        case ERR_CANT_EXEC_CMD:
            printf("Can't execute .CMD file!\n");
            instruct();
            break;

        case ERR_NOT_ENOUGH_MEMORY:
            printf("Not enough memory to process command line.\n");
            break;

        case ERR_CANT_OPEN_OUTPUT:
            printf("Couldn't open \"%s\" for output.\n", arg);
            break;

        case ERR_CANT_EXEC_STDOUT:
            printf("Can't execute redirected output file.\n");
            instruct();
            break;

        case ERR_CANT_CREATE_TEMP:
            printf("Can't generate temporary output file.\n");
            break;
    }
    return 2;
}


int process(char *path, unsigned attrib, time_t date, long size)
{
    char drive[_MAX_DRIVE], dir[_MAX_PATH], fname[_MAX_FNAME], ext[_MAX_EXT];
    char fullpath[_MAX_PATH], mainname[_MAX_PATH];
    char *ptr, *out, *part;
    unsigned len;

    /* Get fully qualified path of file (if not given) */
    _fullpath(fullpath, path, sizeof(fullpath));

    _splitpath(fullpath, drive, dir, fname, ext);

    /* Make "main name" guy */
    _makepath(mainname, NULL, NULL, fname, ext);

    /* Note:
    ** drive has ':'
    ** dir has trailing '\'
    ** filename is plain
    ** ext has leading '.'
    */
    /* But to make these things usable:
    ** delete trailing '\' from path,
    ** delete leading '.' from extension
    */
    len = strlen(dir) - 1;
    if (dir[len] == '\\' || dir[len] == '/')
        dir[len] = '\0';
    if (ext[0] == '.')
	strcpy(ext, ext+1);

    /* Parse command line, filling in file name elements */

    out = outline;
    for (ptr = cmdline; *ptr; ptr++)
    {
        if (*ptr == '%')
        {
	    part = NULL;
            ptr++;
            switch (*ptr)
            {
                case '%':
                default:
                    *out++ = *ptr;
                    break;

                case 'f':
                case 'F':
                case '1':
		    part = path;
		    break;

                case 'n':
                case 'N':
		    part = fname;
		    break;

                case 'p':
                case 'P':
		    part = dir;
		    break;

                case 'd':
                case 'D':
		    part = drive;
		    break;

                case 'x':
                case 'X':
		    part = ext;
		    break;

                case 'q':
                case 'Q':
		    part = fullpath;
		    break;

                case 'm':
                case 'M':
                    part = mainname;
                    break;
            }
	    if (part != NULL)
	    {
                if (quote && strchr(part, ' ') != NULL)
                {
                    part += sprintf(out, "\"%s\"", part);
                }
                else
                {
                    part = stpcpy(out, part);
                }
		if (xlate)
		{
		    if (xlate < 0)
			_strlwr(out);
		    else
			_strupr(out);
		}
		out = part;
	    }
	}
        else if (*ptr == '!')
        {
            *out++ = '\n';
        }
        else if (*ptr == '^')
        {
            *out++ = '\t';
        }
        else
        {
            *out++ = *ptr;
        }
    }
    *out = '\0';

    if (verbose)
    {
        cputs(outline);
        cputs("\r\n");
    }
    fputs(outline, outfile);
    fputs("\n", outfile);

    return 1;
}


int processuserarg(char *arg, unsigned flags)
{
    int ret = 0;

    switch (arg[0])
    {
        case 'A':
        case 'a':
            append = ret = 1;
            break;

        case 'C':
        case 'c':
            cmdfile = ret = 1;
            break;

        case 'V':
        case 'v':
            verbose = ret = 1;
            break;

        case 'X':
        case 'x':
            execute = ret = 1;
            break;

        case 'S':
        case 's':
            subdir = ret = 1;
            break;

        case 'D':
        case 'd':
	    directs = 0;
	    ret = 1;
	    break;

        case 'l':
        case 'L':
	    xlate = -1;
	    ret = 1;
	    break;

        case 'u':
        case 'U':
	    xlate = 1;
	    ret = 1;
	    break;

        case 'q':
        case 'Q':
            quote = ret = 1;
            break;

        default:
            ret = -1;
            break;
    }
    return ret;
}


int processprogress(int flag, int level, char *arg)
{
    return 0;
}


int main (int argc, char **argv)
{
    int ret = 0, sub, arg, spec, cmd, std, temp;
    char outname[_MAX_FNAME], *ptr;
    unsigned len, digit;

    /* Set switches for cmdline processor */
    dononwild = directs = dirlist = 1;

    /* Get program name from cmd line */
    extractprogramname(argv[0]);

    /* Process options next (starting in 2nd position) */

    for (arg = 2; arg < argc; arg++)
    {
        if ((sub = processarg(argv[arg], PROCESS_OPTIONS)) < 0)
        {
            return error(ERR_INVALID_OPTION, argv[arg]);
        }

        /* Break out of loop at end of options */
        if (sub == 0)
            break;
    }

    if (arg >= argc)
    {
        return error(ERR_NOT_ENOUGH_ARGS, NULL);
    }

    /* Can't execute .CMD file */

    if (execute && cmdfile)
    {
        return error(ERR_CANT_EXEC_CMD, NULL);
    }

    /* Argument at "arg" is (or should be) filespec */
    spec = arg++;

    /* Remaining arguments are command line */

    cmd = arg;
    if (argc - cmd < 1)
    {
        return error(ERR_NOT_ENOUGH_ARGS, NULL);
    }
    len = 0;
    for (arg = cmd; arg < argc; arg++)
    {
        len += strlen(argv[arg]) + 1;
    }
    len++;
    if ((cmdline = malloc(len)) == NULL)
    {
        return error(ERR_NOT_ENOUGH_MEMORY, NULL);
    }
    ptr = cmdline;
    for (arg = cmd; arg < argc; arg++)
    {
        ptr = stpcpy(ptr, argv[arg]);
        *ptr++ = ' ';
    }
    *ptr = '\0';

    if ((outline = malloc(strlen(cmdline) + _MAX_PATH * 2 + 1)) == NULL)
    {
        return error(ERR_NOT_ENOUGH_MEMORY, NULL);
    }

    /* Construct output file name based on flags */

    strcpy(outname, argv[1]);
    if (strcmp(outname, "@") == 0)
    {
        std = 1;
        temp = 0;
        outfile = stdout;
        if (execute)
        {
            return error(ERR_CANT_EXEC_STDOUT, NULL);
        }
    }
    else if (strcmp(outname, "*") == 0)
    {
        std = 0;
        temp = 0;
        for (digit = 1; digit < UINT_MAX; digit++)
        {
            sprintf(outname, "t%u.%s", digit, (cmdfile ? "cmd" : "bat"));
            if ((outfile = fopen(outname, "r")) == NULL)
            {
                temp = 1;
                break;
            }
            else
            {
                fclose(outfile);
            }
        }
        if (!temp)
        {
            return error(ERR_CANT_CREATE_TEMP, NULL);
        }
        /* Note: temp file does not exist, so append is useless */
        if ((outfile = fopen(outname, "w")) == NULL)
        {
            return error(ERR_CANT_OPEN_OUTPUT, outname);
        }
    }
    else
    {
        std = temp = 0;
        if (strchr(outname, '.') == NULL)
        {
            if (cmdfile)
            {
                strcat(outname, ".cmd");
            }
            else
            {
                strcat(outname, ".bat");
            }
        }

        if ((outfile = fopen(outname, (append ? "a" : "w"))) == NULL)
        {
            return error(ERR_CANT_OPEN_OUTPUT, outname);
        }
    }

    sub = processfilelist(argv[spec], 0, 0);

    if (sub == 0)
    {
        ret = 1;
    }
    else if (sub < 0)
    {
        ret = 2;
    }

    free(cmdline);
    free(outline);

    if (!std)
    {
        fclose(outfile);
    }

    /* Now execute .BAT file if required */

    if (ret == 0 && execute && !std)
    {
        ret = system(outname);
        if (ret != 0)
        {
            if (ret == -1)
                printf("%s: Error code %d from system.\n", progname, errno);
            else
                printf("%s: Error code %d from command.\n", progname, ret);
        }
    }

    /* If no lines processed for output or done with it, remove output file */
    if (sub == 0 || (ret == 0 && execute && !std))
    {
        remove(outname);
    }
    else if (temp)
    {
        printf("%s: Output sent to \"%s\".\n", progname, outname);
    }

    return ret;
}


