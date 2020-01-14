/*******************************************************************************
*	Process multiple files and subdirectories from command line params     *
*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#ifdef OS2
#define INCL_DOSFILEMGR
#include <os2.h>
#elif defined(WIN32)
#include <windows.h>
#else
#include <dos.h>
#endif
#include <direct.h>
#include <string.h>
#include <time.h>
#include <io.h>
#include "cmdline.h"
#include "match.h"

#define pathsep(c)  (((c) == '\\' || (c) == '/'))
#define EOS         '\0'


/* Global variables used here, set by caller */

int quiet	  = 0;	    /* Print error messages when file not found?      */
int ignore_error  = 0;	    /* Ignore errors returned from process()?	      */
int hidden	  = 0;	    /* Process hidden files as well as normal?	      */
int sysfile	  = 0;	    /* Process system files as well as normal?	      */
int directs	  = 0;	    /* Process directories along with files?	      */
int subdir	  = 0;	    /* Process file specs in subdirectories also?     */

int dononwild     = 0;      /* Process non-wild card names even if not found  */
int dirlist       = 0;      /* Process input (esp. @file) as output from DIR  */

char progname[_MAX_FNAME] = "";

static char default_path[_MAX_PATH] = "";


/*******************************************************************************
*       OS/2 substitutes for Microsoft DOS library routines                    *
*******************************************************************************/
#ifdef OS2
#define _A_NORMAL   FILE_NORMAL
#define _A_RDONLY   FILE_READONLY
#define _A_HIDDEN   FILE_HIDDEN
#define _A_SYSTEM   FILE_SYSTEM
#define _A_VOLID    0x0008
#define _A_SUBDIR   FILE_DIRECTORY
#define _A_ARCH     FILE_ARCHIVED

#pragma pack(2)

struct _find_t {
	HDIR hdir;
	char reserved[21];
	char attrib;
	unsigned wr_time;
	unsigned wr_date;
	long size;
	char name[13];
	};

#pragma pack()

unsigned _dos_findfirst(const char *pattern, unsigned attrib,
			struct _find_t *find)
{
    FILEFINDBUF3 findbuf;
    HDIR         hDir = HDIR_CREATE;
    ULONG        ulSearchCount = 1;
    APIRET       rc;

    find->hdir = HDIR_CREATE;
    rc = DosFindFirst(pattern, &find->hdir, attrib, &findbuf, sizeof(findbuf),
		      &ulSearchCount, FIL_STANDARD);
    /* Must close hdir if nothing found -- see logic in main loops */
}


unsigned _dos_findnext(struct _find_t *find)
{
    FILEFINDBUF3 findbuf;
    APIRET       rc;
    ULONG        ulSearchCount = 1;

    rc = DosFindNext(find->hdir, &findbuf, sizeof(findbuf), &ulSearchCount);
}

void _dos_findclose(struct _find_t *find)
{
    APIRET rc;

    rc = DosFindClose(find->hdir);  /* ?? */
    find->hdir = NULL;
}

#endif /* OS2 */

/*******************************************************************************
*       WIN32 substitutes for Microsoft library routines                       *
*******************************************************************************/
#ifdef WIN32

struct _find_t {
    /* These structures are not in DOS record, for WIN32 "behind the scenes" */
    long               handle;
    unsigned int       fattrib;
    struct _finddata_t find_data;
    /* These should mimic the DOS structures, with appropriate 32-bit size diffs */
    unsigned int attrib;
    time_t       write_time;
    long         size;              /* File on WIN32 can be > 2G though! */
    char         name[260];         /* This is different size than DOS! */
};

static void convert_win32_data(struct _find_t *find)
{
    strcpy(find->name, find->find_data.name);
    find->write_time = find->find_data.time_write;
    find->size       = find->find_data.size;
    find->attrib     = (find->find_data.attrib & 0xFF);
}


unsigned _dos_findfirst(const char *pattern, unsigned attrib,
			struct _find_t *find)
{
    /* We must do our own attribute matching under WIN32 (ugh!) */
    find->fattrib = attrib | _A_ARCH;

    find->handle = _findfirst(pattern, &find->find_data);
    if (find->handle != -1L)
    {
	if (((find->find_data.attrib & ~find->fattrib) & 0xFF) == 0 ||
	    ((find->find_data.attrib & 0xFF) == A_NORMAL))
	{
	    convert_win32_data(find);
	    return 0;
	}
	else
	{
	    while (_findnext(find->handle, &find->find_data) == 0)
	    {
		if (((find->find_data.attrib & ~find->fattrib) & 0xFF) == 0 ||
		    ((find->find_data.attrib & 0xFF) == A_NORMAL))
		{
		    convert_win32_data(find);
		    return 0;
		}
	    }
	    return 1;
	}
    }
    find->handle = 0L;
    return 1;
}

unsigned _dos_findnext(struct _find_t *find)
{
    while (_findnext(find->handle, &find->find_data) == 0)
    {
	if (((find->find_data.attrib & ~find->fattrib) & 0xFF) == 0 ||
	    ((find->find_data.attrib & 0xFF) == A_NORMAL))
	{
	    convert_win32_data(find);
	    return 0;
	}
    }
    return 1;
}

void _dos_findclose(struct _find_t *find)
{
    if (find->handle != 0L)
    {
	_findclose(find->handle);
    }
    find->handle = 0L;
}

#endif /* WIN32 */

#if !defined(OS2) && !defined(WIN32)

#define _dos_findclose(f)

#endif /* !OS2 && !WIN32 */


/*******************************************************************************
*	Skip blanks and white space in string				       *
*******************************************************************************/
char *stpblk(char *s)
{
    register char c;

    while (((c = *s) != EOS) && (c == ' ' || c == '\t' || c == '\n'))
    {
	s++;
    }
    return s;
}


/*******************************************************************************
*       Skip until white space in string                                       *
*******************************************************************************/
char *stpwrd(char *s)
{
    register char c;

    while (((c = *s) != EOS) && (c != ' ' && c != '\t' && c != '\n'))
    {
	s++;
    }
    return s;
}


/*******************************************************************************
*       Point to file name part of path string                                 *
*******************************************************************************/
char *stppath(char *s)
{
    char *ptr;

    /* Skip to end of string first */

    ptr = s + strlen(s);

    /* Search backwards until path separator or drive designator found */

    while (ptr > s && !(pathsep(*ptr) || (*ptr == ':' && ptr == s + 1)))
    {
	--ptr;
    }

    /* If delimiter found (past beginning of string), move after that */

    if (pathsep(*ptr) || (*ptr == ':' && ptr == s + 1))
    {
	++ptr;
    }

    return ptr;
}


/*******************************************************************************
*	Extract program name from argv[0]				       *
*******************************************************************************/
void extractprogramname(char *arg)
{
    char fname[_MAX_FNAME];

    /* Extract executable name from arg (usually argv[0]) */

    _splitpath(arg, NULL, NULL, fname, NULL);
    strcpy(progname, fname);
    _strupr(progname);
}


/*******************************************************************************
*	Process standard switches used in these routines		       *
*           Returns:  0 if not a switch (doesn't start with '-' or '/'         *
*		     -1 if unrecognized switch (not standard)		       *
*		      1 if valid standard switch			       *
*******************************************************************************/
int  processarg(char *arg, unsigned flags)
{
    int ret = 0;

    if (arg[0] == '-' || arg[0] == '/')
    {
	/* Give caller first chance to process argument (overrides standard) */
	if ((ret = processuserarg(&arg[1], flags)) == 0)
	{
	    switch (arg[1])
	    {
		case 'Q':
		case 'q':
		    quiet = ret = 1;
		    break;
		case 'I':
		case 'i':
		    ignore_error = ret = 1;
		    break;
		case 'H':
		case 'h':
		    hidden = ret = 1;
		    break;
		case 'S':
		case 's':
		    subdir = ret = 1;
		    break;
		case 'Y':
		case 'y':
		    sysfile = ret = 1;
		    break;
		case 'D':
		case 'd':
		    directs = ret = 1;
		    break;
		case 'E':
		case 'e':
		    /* Process this option only if positional arguments used */
		    if (flags & PROCESS_FILES)
		    {
			strcpy(default_path, &arg[2]);
		    }
		    break;
		case '?':
		    return -1;	/* abort argument processing, but no error msg */
		default:
		    ret = -1;
		    break;
	    }
	}
	if (ret < 0)
	{
	    fprintf(stderr, "%s: Unknown or invalid option \"%s\".\n", progname, arg);
	}
    }
    return ret;
}


/*******************************************************************************
*	Routine to process indirect or wildcard file specs with subdir scan    *
*   Uses globals: progname[], subdir, ignore_error, quiet, process(), MAXLEVEL,*
*		  hidden						       *
*   Returns:  0 if no file found or argument not processed		       *
*	     -1 if error in file or argument				       *
*	      1 if successful						       *
*	     other from process() or processuserarg()			       *
*******************************************************************************/
int  processfilelist(char *arg, int level, unsigned flags)
{
    struct _find_t file;
    char drive[_MAX_DRIVE], dir[_MAX_DIR], path[_MAX_PATH];
    char spec[_MAX_FNAME], ext[_MAX_EXT];
    char line[_MAX_PATH], *ptr, *bow, *eow, *wildpath;
    char dflt[_MAX_PATH];
    FILE *fp;
    int  ret, sub;
    int  drv;
    int  wild, dot;
    unsigned int len, attrib;
    unsigned long lineno;
    static char toomany[] = "\n%s: Too many levels of subdirectories or indirect files to process\n\t\"%s\".\n";
    static char wilds[] = "*?";

    /* Look for standard switches -- if processed here, will be positional    */
    /* i.e. switches apply to all file specs processed afterwards on cmd line */

    if (arg[0] == '-' || arg[0] == '/')
    {
	/* We don't want positional flags during file processing to     */
	/* erroneously affect the none vs. some counting aspects of ret */
	/* so only set it if there was an error processing the args.    */

	ret = 0;
	if (flags & PROCESS_OPTIONS)
	{
	    if ((sub = processarg(arg, flags)) < 0)
	    {
		ret = sub;
	    }
	}
    }

    /* Look for indirect file spec */
    else if (arg[0] == '@')
    {
	if (level > MAXLEVEL)
	{
	    fprintf(stderr, toomany, progname, arg);
	    return -1;
	}
	if (arg[1] == EOS)
	{
	    fp = stdin;
	}
	else
	{
	    fp = fopen(&arg[1], "r");
	}
	if (fp != NULL)
	{
	    ret = 0;
	    lineno = 0L;
	    while (!feof(fp))
	    {
		lineno++;
		if (fgets(line, sizeof(line), fp) != NULL)
		{
		    len = strlen(line) - 1;
		    if (line[len] == '\n')
		    {
			line[len] = EOS;
		    }
		    /* Ignore totally blank lines and leading blank chars */
		    if (dirlist)
		    {
			ptr = line;
			/* Ignore non-file entry lines (header and footer) */
			if (*ptr == ' ')
			{
			    *ptr = EOS;
			}
		    }
		    else
		    {
			ptr = stpblk(line);
		    }
		    while (*ptr != EOS)
		    {
			/* If first character is a quote, then do "normal" */
			/* parsing of a quoted string (i.e., doubled quote */
			/* is a single embedded quote) and look for the    */
			/* trailing quote to terminate the "word".         */
			if (*ptr == '"')
			{
			    /* Going to use eow as input, bow as output */
			    /* for the copy loop, then leave eow ready  */
			    /* for next go round with ptr where it was  */
			    bow = ptr;
			    eow = ptr+1;
			    while (*eow != EOS)
			    {
				if (*eow == '"')
				{
				    eow++;
				    if (*eow == '"')
				    {
					/* Doubled quote = embedded quote char */
					*bow++ = *eow++;
				    }
				    else
				    {
					/* Single quote = end of quoted string */
					break;
				    }
				}
				else
				{
				    /* Just copy the input character as is */
				    *bow++ = *eow++;
				}
			    }
			    *bow = EOS;
			}
			else
			{
			    eow = stpwrd(ptr);
			}

			/* Normally, parse through and use each "word"    */
			/* on line, unless this is a "DIR" listing when   */
			/* we should "glue" first two words together with */
			/* "." and only use that much.                    */

			if (*eow != EOS)
			{
			    if (dirlist)
			    {
				/* If the output looks like "name.ext", */
				/* output is "brief" DOS listing format */

				if (strchr(ptr, '.') == NULL)
				{
				    *eow++ = '.';
				    bow = &line[9];
				    if (*bow != ' ')
				    {
					strcpy(eow, bow);
					eow = stpwrd(eow);
				    }
				}
				*eow = EOS;
			    }
			    else
			    {
				*eow++ = EOS;
			    }
			}
			if ((sub = processfilelist(ptr, level + 1, flags)) < 0)
			{
			    fclose(fp);
			    return sub;
			}
			ret += sub;
			if (dirlist)
			{
			    ptr = eow;
			}
			else
			{
			    ptr = stpblk(eow);
			}
		    }
		}
		else
		{
		    if (ferror(fp))
		    {
			fprintf(stderr, "\n%s: Error reading indirect file \"%s\" at line %lu.\n", progname, &arg[1], lineno);
			ret = -1;
			break;
		    }
		}
	    }
	    fclose(fp);
	}
	else
	{
	    fprintf(stderr, "\n%s: Cannot open indirect file \"%s\".\n", progname, &arg[1]);
	    ret = -1;
	}
    }
    else
    {
	/* Notify caller of impending recursion, allow user abort */

	if ((ret = processprogress(PROGRESS_RECURSE_INTO, level, arg)) < 0)
	{
	    return ret;
	}

	ret = 0;
	_splitpath(arg, drive, dir, spec, ext);

	/* Deal with default path (if given) here:  if no explicit path   */
	/* specified in "arg", used default path (if any) before the spec */

	if (*drive == EOS && *dir == EOS && *default_path != EOS)
	{
	    _makepath(dflt, NULL, default_path, spec, ext);
	    _splitpath(dflt, drive, dir, spec, ext);
	    arg = dflt;
	}

	/* If wild-card is given for drive or directory, we must recurse */
	/* here to process that ambiguity one component at a time	 */

	if ((ptr = strpbrk(drive, wilds)) != NULL)
	{
	    /* Skip the floppy drives (for now?) */
	    for (drv = 3; drv <= 26; drv++)
	    {
		if (_getdcwd(drv, line, sizeof(line)) != NULL)
		{
		    *ptr = (char)(drv - 1 + 'A');
		    _makepath(path, drive, dir, spec, ext);

		    /* Now recurse to process any other ambiguous element */

		    if ((sub = processfilelist(path, level + 1, flags)) < 0)
		    {
			ret = sub;
			break;
		    }
		    ret += sub;
		}
	    }
	}

	else if ((ptr = strpbrk(dir, wilds)) != NULL)
	{
	    /* Must truncate path at wild-card point, find and process	   */
	    /* all subdirectories at that level, and tack on the remaining */
	    /* path for each one and process recursively.		   */

	    strcpy(line, dir);

	    /* Mark path points before and after this ambiguous element */
	    bow = ptr;
	    while (ptr > dir && !pathsep(*ptr))
		ptr--;
	    if (pathsep(*ptr))
		ptr++;
	    eow = ptr;
	    while (*bow && !pathsep(*bow))
		bow++;
	    *bow = EOS;

	    /* Now move these marks to the saved version in 'line' */
	    bow = line + (bow - dir);
	    eow = line + (eow - dir);
	    *eow = EOS;

	    attrib = _A_SUBDIR | _A_RDONLY;
	    if (hidden)
		attrib |= _A_HIDDEN;
	    if (sysfile)
		attrib |= _A_SYSTEM;
	    sprintf(path, "%s%s", drive, dir);
	    if (_dos_findfirst(path, attrib, &file) == 0)
	    {
		do
		{
		    if (file.attrib & _A_SUBDIR && file.name[0] != '.')
		    {
			/* Make up a path name consisting of the path before */
			/* the ambiguous element, then the file.name from    */
			/* the directory search, then the remaining path and */
			/* file name from the original spec.		     */

			sprintf(path, "%s%s%s%s%s%s", drive, line, file.name,
						      bow, spec, ext);

			/* Now recurse to process any other ambiguous element */

			if ((sub = processfilelist(path, level + 1, flags)) < 0)
			{
			    ret = sub;
			    break;
			}
			ret += sub;
		    }
		} while (_dos_findnext(&file) == 0);
		_dos_findclose(&file);
	    }
	}

	else
	{
	    wildpath = arg;

	    /* If no name or extension is specified (i.e. \), or a wild */
	    /* card (*?) is specified in either, use "*.*" for OS file  */
	    /* spec and use our own version of pattern matching to find */
	    /* the file.						*/

	    if (spec[0] == EOS)
	    {
		spec[0] = '*';
		spec[1] = EOS;
	    }

	    /* First test: if unambiguous spec is a directory name */
	    /* go down one level                                   */

	    if ((strpbrk(arg, wilds) == NULL))
	    {
		if (_dos_findfirst(arg, _A_SUBDIR | _A_RDONLY, &file) == 0)
		{
		    if (file.attrib & _A_SUBDIR)
		    {
			_makepath(line, NULL, arg, "*", "*");
			_splitpath(line, drive, dir, spec, ext);
			arg = line;
		    }
		    _dos_findclose(&file);
		}
	    }

	    attrib = _A_RDONLY;
	    if (hidden)
		attrib |= _A_HIDDEN;
	    if (sysfile)
		attrib |= _A_SYSTEM;
	    if (directs)
		attrib |= _A_SUBDIR;

	    /* This test catches cases like "d" which should get all  */
	    /* extensions, but "d*" should just process as is.        */
	    /* But, must check if file is there as is before assuming */
	    /* a wild card is meant.                                  */

	    if (ext[0] == EOS && strpbrk(spec, wilds) == NULL)
	    {
		if (_dos_findfirst(arg, attrib, &file) == 0)
		{
		    _dos_findclose(&file);
		}
		else
		{
		    ext[0] = '.';
		    ext[1] = '*';
		    ext[2] = EOS;
		}
	    }
	    if (strpbrk(spec, wilds) != NULL || strpbrk(ext, wilds) != NULL)
	    {
		_makepath(path, drive, dir, "*", "*");
		arg = path;
		sprintf(line, "%s%s", spec, ext);
		wild = TRUE;
		dot = (strchr(line, '.') == NULL) ? FALSE : TRUE;
	    }
	    else
	    {
		wild = dot = FALSE;
	    }

	    /* Process files that match spec */

	    if (_dos_findfirst(arg, attrib, &file) == 0)
	    {
		if (!wild ||
		  (sub = processprogress(PROGRESS_WILD_START, level, wildpath)) >= 0)
		{
		    do
		    {
			_makepath(path, drive, dir, file.name, NULL);

			/* Special case of file without any extension yet our */
			/* pattern is looking for a dot -- put dot into name  */

			if (dot && strchr(file.name, '.') == NULL)
			{
			    strcat(file.name, ".");
			}

			if (!wild || string_match(file.name, line, FALSE))
			{
			    if ((sub = process(path, file.attrib,
					       file.write_time,
					       file.size)) < 0)
			    {
				if (ignore_error)
				{
				    sub = 0;
				}
				else
				{
				    ret = sub;
				    break;
				}
			    }
			    ret += sub;
			}
		    } while (_dos_findnext(&file) == 0);
		}
		_dos_findclose(&file);
		if (wild)
		{
		    processprogress(PROGRESS_WILD_FINISH, level, NULL);
		}
	    }
	    else if (dononwild && !wild)
	    {
		if ((sub = process(arg, 0, 0, 0L)) < 0)
		{
		    if (ignore_error)
		    {
			sub = 0;
		    }
		    else
		    {
			processprogress(PROGRESS_RECURSE_OUT, level, NULL);
			return sub;
		    }
		}
		ret += sub;
	    }
	    else
	    {
		if (!quiet)
		{
		    fprintf(stderr, "\n%s: Cannot find \"%s\".\n", progname, arg);
		}
	    }

	    /* Process subdirectories that match file spec also */

	    if (subdir)
	    {
		if (level > MAXLEVEL)
		{
		    fprintf(stderr, toomany, progname, arg);
		    processprogress(PROGRESS_RECURSE_OUT, level, NULL);
		    return -1;
		}
		_makepath(path, drive, dir, "*", "*");
		attrib = _A_SUBDIR | _A_RDONLY;
		if (hidden)
		    attrib |= _A_HIDDEN;
		if (sysfile)
		    attrib |= _A_SYSTEM;
		if (_dos_findfirst(path, attrib, &file) == 0)
		{
		    do
		    {
			if (file.attrib & _A_SUBDIR && file.name[0] != '.')
			{
			    _makepath(line, NULL, dir, file.name, NULL);
			    _makepath(path, drive, line, spec, ext);
			    if ((sub = processfilelist(path, level + 1, flags)) < 0)
			    {
				ret = sub;
				break;
			    }
			    ret += sub;
			}
		    } while (_dos_findnext(&file) == 0);
		    _dos_findclose(&file);
		}
	    }
	}
	processprogress(PROGRESS_RECURSE_OUT, level, NULL);
    }
    return ret;
}


/*******************************************************************************
*	Process entire command line in standard manner			       *
*******************************************************************************/
int  processcmdline(int argc, char *argv[], unsigned flags)
{
    int i, ret = 0, sub;

    extractprogramname(argv[0]);

    /* For non-positional arguments, process all switches before file specs */
    /* Note: caller must call us twice in this case, because other stuff    */
    /* might need to happen in between (such as processing of args)	    */

    if ((flags & PROCESS_OPTIONS) && !(flags & PROCESS_FILES))
    {
	for (i = 1; i < argc; i++)
	{
	    if ((sub = processarg(argv[i], flags)) < 0)
	    {
		return sub;
	    }
	    ret += sub;
	}
    }

    /* Process file specs and positional switches */

    else if (flags & PROCESS_FILES)
    {
	for (i = 1; i < argc; i++)
	{
	    if ((sub = processfilelist(argv[i], 0, flags)) < 0)
	    {
		return sub;
	    }
	    ret += sub;
	}
    }

    return ret;
}


/* End of file CMDLINE.C */
