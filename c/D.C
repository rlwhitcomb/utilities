/*******************************************************************************
*                                                                              *
*       Substitute for "Directory" DOS command                                 *
*                                                                              *
*  History:                                                                    *
*       Everything before this is lost in antiquity.                           *
*       01-Aug-2016 (whiro01)                                                  *
*           Process the "-wide" list so everything lines up with the widest    *
*           name (otherwise long names make the list very messy to read).      *
*           Regularize whitespace.                                             *
*       24-Mar-2017 (whiro01)                                                  *
*           Fix "-q(uoted)" option to also work outside of "brief" mode.       *
*       18-Jul-2017 (whiro01)                                                  *
*           Add extra line spaces in /wide mode; remove trailing "\" for dir   *
*           name headings there also.                                          *
*       13-Feb-2018 (whiro01)                                                  *
*           Change "-N" option to not display extensions either.               *
*       22-May-2018 (whiro01)                                                  *
*           Add option "-/" to translate directory separators to "/" form.     *
*                                                                              *
*******************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <dos.h>
#include <time.h>
#include <string.h>
#include <malloc.h>
#include <limits.h>
#ifdef WIN32
#include <windows.h>
#include <direct.h>
#else
#include <graph.h>
#endif
#include <conio.h>
#include "cmdline.h"
#include "parse.h"
#include "util.h"
#include "match.h"


#ifdef WIN32
#define HUGE
#define _nmalloc    malloc
#else
#define NEAR _near
#define HUGE __huge
#define _getdiskfree    _dos_getdiskfree
#endif


typedef struct _drivespace
{
    unsigned char    used;
    unsigned         avail_clusters;
    unsigned __int64 cluster_size;
} drivespace;

static unsigned __int64 total_size         = 0L;
static unsigned __int64 total_cluster_size = 0L;
static unsigned long num_files             = 0L;
static unsigned long num_directories       = 0L;
static int           line_pos              = 0;
static char          last_path[_MAX_PATH]  = "";
static drivespace    drives[26]            = { {0} };
static unsigned      current_drive	  = 0;

#define SORT_NONE   (0)
#define SORT_NAME   (1)
#define SORT_FNAM   (2)
#define SORT_EXTN   (3)
#define SORT_DATE   (4)
#define SORT_SIZE   (5)
#define SORT_ATTR   (6)

#define SORT_ASCEND	(0)
#define SORT_DESCEND	(1)

#define FILT_NONE   (0x0000)
#define FILT_NAME   (0x0001)
#define FILT_FNAM   (0x0002)
#define FILT_EXTN   (0x0004)
#define FILT_DATE   (0x0008)
#define FILT_SIZE   (0x0010)
#define FILT_ATTR   (0x0020)
#define FILT_DOTS   (0x0040)

#define FILT_ASCEND     SORT_ASCEND
#define FILT_DESCEND    SORT_DESCEND

static int brief               = 0;
static int wide                = 0;
static int savewide            = 0;
static int bare_name           = 0;
static int without_ext         = 0;
static int full_name           = 0;
static int paged               = 0;
static int quoted              = 0;
static int totals_only         = 0;
static int unadorned           = 0;
static int limitrecursion      = 0;
static int errorlimitrecursion = 0;
static int widest_name_len     = 0;
static int use_forward_slashes = 0;

static int sortcase = 0;
static int sortcrit = SORT_NONE;
static int sortdir  = SORT_ASCEND;

static int filtcase = 1;
static int filtcrit = FILT_NONE;
static int filtdir  = FILT_NONE;    /* FILT_ASCEND for all types */

static char      filtername[_MAX_PATH] = "*";
static struct tm filtertime = { 0 };
static long      filtersize = 0L;
static unsigned  filterattr = 0;

static int       currentrow = 0;
static int       screenrows;
static int       screencols;

static char hdr1[] = "Attr     Size        Date       Time     Name";
static char hdr2[] = "---- ----------- ----------- ---------- ------------";
static char ftr1[] = "     -----------                        ------------";
static char ftr2[] = "  %14s bytes                 %7s file%s.";
static char ftr3[] = "  %14s bytes of space used   %7s director%s.";
static char twos[] = "%s%s";


typedef struct _DIRLIST
{
    struct _DIRLIST NEAR *next;
    char	     path[1];
} DIRLIST, NEAR *PDIRLIST;

PDIRLIST dirlisthead = NULL;
PDIRLIST lastdir     = NULL;


typedef struct _SAVELIST
{
    struct _SAVELIST *left;
    struct _SAVELIST *right;
    PDIRLIST	      dir;
    unsigned char     attrib;
    time_t            date;
    long              size;
    char	      name[1];

} SAVELIST, *PSAVELIST;

PSAVELIST listhead = NULL;


/*******************************************************************************
*       Memory allocation routines                                             *
*******************************************************************************/
#if defined(OS2) || defined(WIN32)

/* Reasonable platforms don't need much help */

#define mem_init()      (1)
#define mem_deinit()
#define mem_alloc(s)    malloc(s)

#else

/* Our own memory allocator (just carve up big block in chunks) */

char HUGE *memory        = NULL;
unsigned long memorysize = 0L;
unsigned long memoryused = 0L;


/*******************************************************************************
*	Initialize memory allocator					       *
*******************************************************************************/
int mem_init(void)
{
    unsigned seg;

    /* Call OS to determine amount of space available to allocate */

    _dos_allocmem(UINT_MAX, &seg);
    memorysize = ((unsigned long)seg - 2048) * 16L;
    memoryused = 0L;

    if ((memory = _halloc(memorysize, 1)) != NULL)
    {
	return 1;
    }
    return 0;
}


/*******************************************************************************
*	Deinitialize memory allocator					       *
*******************************************************************************/
void mem_deinit(void)
{
    if (memory != NULL)
    {
	_hfree(memory);
    }
    memory     = NULL;
    memorysize = 0L;
    memoryused = 0L;
}


/*******************************************************************************
*	Memory allocator						       *
*******************************************************************************/
void *normalize_ptr(void *p)
{
    __asm
    {
	mov     ax,[bp+6] /* offset p */
	mov     dx,[bp+8] /* seg p    */
	mov     cl,4
	mov     bx,ax
	shr     bx,cl
	add     dx,bx
	and     ax,0Fh
    }
}


void *mem_alloc(unsigned size)
{
    void *ptr = NULL;
    unsigned long lsize = (unsigned long)size;

    if (memory != NULL && (memoryused + lsize <= memorysize))
    {
	ptr = normalize_ptr(&memory[memoryused]);
	memoryused += lsize;
    }
    return ptr;
}

#endif  /* !OS2 && !WIN32 */


/*******************************************************************************
*       Compare two struct tm date/times                                       *
*******************************************************************************/
static int compare_dates(struct tm *time1, struct tm *time2)
{
    int ret;

    if ((ret = time1->tm_year - time2->tm_year) == 0)
    {
	if ((ret = time1->tm_mon - time2->tm_mon) == 0)
	{
	    if ((ret = time1->tm_mday - time2->tm_mday) == 0)
	    {
		if ((ret = time1->tm_hour - time2->tm_hour) == 0)
		{
		    if ((ret = time1->tm_min - time2->tm_min) == 0)
		    {
			ret = time1->tm_sec - time2->tm_sec;
		    }
		}
	    }
	}
    }
    return ret;
}


/*******************************************************************************
*	Compare filenames or extensions (for sorting purposes)		       *
*******************************************************************************/
static int compare_filenames(char *patha, char *pathb, int which)
{
    char drive[_MAX_DRIVE], dir[_MAX_DIR];
    char fnamea[_MAX_FNAME], fnameb[_MAX_FNAME], exta[_MAX_EXT], extb[_MAX_EXT];
    int  ret;

    _splitpath(patha, drive, dir, fnamea, exta);
    _splitpath(pathb, drive, dir, fnameb, extb);

    switch (which)
    {
	case SORT_EXTN:
	    if (sortcase)
	    {
		if ((ret = _strcmpi(exta, extb)) == 0)
		{
		    ret = _strcmpi(fnamea, fnameb);
		}
	    }
	    else
	    {
		if ((ret = strcmp(exta, extb)) == 0)
		{
		    ret = strcmp(fnamea, fnameb);
		}
	    }
	    break;

	case SORT_FNAM:
	default:
	    if (sortcase)
	    {
		if ((ret = _strcmpi(fnamea, fnameb)) == 0)
		{
		    ret = _strcmpi(exta, extb);
		}
	    }
	    else
	    {
		if ((ret = strcmp(fnamea, fnameb)) == 0)
		{
		    ret = strcmp(exta, extb);
		}
	    }
	    break;
    }
    return ret;
}


/*******************************************************************************
*	Compare path and filename (for sorting purposes)		       *
*******************************************************************************/
static int compare_fullnames(PSAVELIST filea, PSAVELIST fileb)
{
    char patha[_MAX_PATH], pathb[_MAX_PATH];
    char *namea, *nameb;
    int ret;

    /* Quick check to see if they are in the same directory */
    if (filea->dir == fileb->dir)
    {
	namea = filea->name;
	nameb = fileb->name;
    }

    /* Different directories, must construct full paths to compare */
    else
    {
	if (filea->dir == NULL)
	    namea = filea->name;
	else
	{
	    sprintf(patha, twos, filea->dir->path, filea->name);
	    namea = patha;
	}
	if (fileb->dir == NULL)
	    nameb = fileb->name;
	else
	{
	    sprintf(pathb, twos, fileb->dir->path, fileb->name);
	    nameb = pathb;
	}
    }

    if (sortcase)
    {
	ret = _strcmpi(namea, nameb);
    }
    else
    {
	ret = strcmp(namea, nameb);
    }
    return ret;
}


/*******************************************************************************
*       Match file names against pattern (for filtering purposes)              *
*******************************************************************************/
static int match_names(char *path, char *pattern, int crit, int casesens)
{
    char fname[_MAX_FNAME], ext[_MAX_EXT], filename[_MAX_PATH];

    switch (crit & (FILT_NAME | FILT_FNAM | FILT_EXTN | FILT_DOTS))
    {
	case FILT_NAME:
	    return string_match(path, pattern, casesens);

	case FILT_FNAM:
	    _splitpath(path, NULL, NULL, fname, ext);
	    sprintf(filename, twos, fname, ext);
	    return string_match(filename, pattern, casesens);

	case FILT_EXTN:
	    _splitpath(path, NULL, NULL, fname, ext);
	    return string_match(ext, pattern, casesens);

	case FILT_DOTS:
	    _splitpath(path, NULL, NULL, fname, ext);
	    if ((fname[0] == EOS || fname[0] == '.') &&
		strcmp(ext, ".") == 0)
		    return TRUE;
    }
    return 0;
}


/****************************************************************************************************
*       Quote a file name with embedded spaces or other special characters.                         *
*                                                                                                   *
* From here:                                                                                        *
*   http://stackoverflow.com/questions/30620876/how-to-properly-escape-filenames-in-windows-cmd-exe *
* The characters ^ and & can be escaped with either a caret or double quotes.                       *
* The characters ;, ,, =, and space can only be escaped with double quotes.                         *
*                                                                                                   *
****************************************************************************************************/
static void quote_name(char *buf, char *path)
{
    char c;

    if (strchr(path, ' ') == NULL &&
        strchr(path, '^') == NULL &&
        strchr(path, '&') == NULL &&
        strchr(path, ';') == NULL &&
        strchr(path, ',') == NULL &&
        strchr(path, '=') == NULL)
    {
	if (buf != path)
	    strcpy(buf, path);
    }
    else
    {
	*buf++ = '"';
	while ((c = *path++) != EOS)
	{
	    *buf++ = c;
	    if (c == '"')
		*buf++ = c;
	}
	*buf++ = '"';
	*buf = EOS;
    }
}


/*******************************************************************************
*	Convert backslashes to forward slashes if option is given.             *
*******************************************************************************/
static void convert_slashes(char *buf)
{
    if (use_forward_slashes)
    {
	char *ptr = buf;
	while ((ptr = strchr(ptr, '\\')) != NULL)
	{
	    *ptr++ = '/';
	}
    }
}


/*******************************************************************************
*	Compute filename and extension part of path (given file attribute)     *
*******************************************************************************/
static void get_name(char *path, char *buf, unsigned attrib)
{
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    char new_name[_MAX_PATH];

    _splitpath(path, drive, dir, fname, ext);
    if (attrib & A_SUBDIR)
    {
	sprintf(buf, "[%s%s]", fname, ext);
    }
    else
    {
	if (quoted)
	{
	    sprintf(new_name, twos, fname, ext);
	    quote_name(buf, new_name);
	}
	else
	{
	    sprintf(buf, twos, fname, ext);
	}
    }
    convert_slashes(buf);
}


/*******************************************************************************
*	Compute drive and directory part of path			       *
*******************************************************************************/
static void get_path(char *path, char *buf)
{
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    size_t len;

    _splitpath(path, drive, dir, fname, ext);
    len = strlen(dir);
    if (dir[len - 1] == '\\')
	dir[len - 1] = EOS;
    sprintf(buf, twos, drive, dir);
    convert_slashes(buf);
}


/*******************************************************************************
*	Compute drive letter from path, set global drive usage flag	       *
*******************************************************************************/
static unsigned __int64 log_drive(char *path, long size)
{
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    unsigned no;
    struct _diskfree_t diskspace;
    unsigned __int64 cluster_space, cluster_size;

    _splitpath(path, drive, dir, fname, ext);
    if (drive[0] != EOS)
    {
	if (drive[0] >= 'A' && drive[0] <= 'Z')
	    no = (drive[0] - 'A');
	else if (drive[0] >= 'a' && drive[0] <= 'z')
	    no = (drive[0] - 'a');
	else
	    no = (current_drive - 1);
    }
    else
    {
	no = (current_drive - 1);
    }
    if (drives[no].used == 0)
    {
	drives[no].used = 1;
	_getdiskfree(no + 1, &diskspace);
	drives[no].avail_clusters = diskspace.avail_clusters;
	drives[no].cluster_size = (unsigned __int64)diskspace.bytes_per_sector *
						    diskspace.sectors_per_cluster;
    }
    cluster_size  = drives[no].cluster_size;
    cluster_space = ((unsigned __int64)size + (cluster_size - 1)) / cluster_size *
		    cluster_size;
    return cluster_space;
}


/*******************************************************************************
*	Find/Allocate new directory list entry for path			       *
*******************************************************************************/
static PDIRLIST dirlist_find(char *path, char **pname)
{
    PDIRLIST list, prev, dirlist;
    char *name, ch;
    int cmp;

    /* Point to end of path, beginning of name */
    name = stppath(path);
    *pname = name;

    /* If there is no path part, just return */
    if (name == path)
	return NULL;

    /* Fixup path so EOS is after path part */
    ch = *name;
    *name = EOS;

    /* First check if match with last directory accessed (mostly true) */

    if (lastdir != NULL && strcmp(path, lastdir->path) == 0)
    {
	*name = ch;
	return lastdir;
    }

    /* Search sorted list for exact match, or proper sorted position of new */

    for (prev = NULL, list = dirlisthead; list; prev = list, list = list->next)
    {
	cmp = strcmp(path, list->path);
	if (cmp == 0)
	{
	    *name = ch;
	    lastdir = list;
	    return list;
	}
	else if (cmp < 0)
	    break;
    }

    /* Insert new element into sorted order in list */

    if ((dirlist = _nmalloc(sizeof(DIRLIST) + (name - path))) != NULL)
    {
	strcpy(dirlist->path, path);

	if (prev == NULL)
	{
	    dirlisthead = dirlist;
	}
	else
	{
	    prev->next = dirlist;
	}
	dirlist->next = list;
    }

    *name = ch;
    lastdir = dirlist;
    return dirlist;
}


/*******************************************************************************
*	Allocate new file list element (for sorting)			       *
*******************************************************************************/
static PSAVELIST list_alloc(char *path, unsigned attrib, time_t date, long size)
{
    PSAVELIST newlist;
    PDIRLIST  dirlist;
    char     *name;

    dirlist = dirlist_find(path, &name);
    if ((newlist = mem_alloc(sizeof(SAVELIST) + strlen(name))) != NULL)
    {
	newlist->dir	= dirlist;
	newlist->attrib = (unsigned char)attrib;
	newlist->date	= date;
	newlist->size   = size;
	strcpy(newlist->name, name);
	/* Do the "widest name" calculation for "-w" display. */
	if (savewide)
	{
	    char buf[_MAX_PATH];
	    get_name(name, buf, attrib);
	    widest_name_len = max(widest_name_len, (int)strlen(buf));
	}
    }
    return newlist;
}


/*******************************************************************************
*	Insert list element into list in sorted order			       *
*******************************************************************************/
static void list_insert(PSAVELIST newlist)
{
    PSAVELIST list;
    int comp;

    if ((list = listhead) == NULL)
    {
	listhead = newlist;
    }
    else
    {
	for (;;)
	{
	    switch (sortcrit)
	    {
		case SORT_NAME:
		    comp = compare_fullnames(newlist, list);
		    break;

		case SORT_FNAM:
		case SORT_EXTN:
		    comp = compare_filenames(newlist->name, list->name, sortcrit);
		    break;

		case SORT_DATE:
		    if (newlist->date > list->date)
			comp = 1;
		    else if (newlist->date < list->date)
			comp = -1;
		    else
			comp = compare_fullnames(newlist, list);
		    break;

		case SORT_SIZE:
		    if (newlist->size > list->size)
			comp = 1;
		    else if (newlist->size < list->size)
			comp = -1;
		    else
			comp = compare_fullnames(newlist, list);
		    break;

		case SORT_ATTR:
		    if (newlist->attrib > list->attrib)
			comp = 1;
		    else if (newlist->attrib < list->attrib)
			comp = -1;
		    else
			comp = compare_fullnames(newlist, list);
		    break;

		default:
		    comp = 0;
		    break;
	    }
	    if (sortdir == SORT_DESCEND)
	    {
		comp = -comp;
	    }
	    if (comp < 0)
	    {
		if (list->left == NULL)
		{
		    list->left = newlist;
		    break;
		}
		else
		{
		    list = list->left;
		}
	    }
	    else
	    {
		if (list->right == NULL)
		{
		    list->right = newlist;
		    break;
		}
		else
		{
		    list = list->right;
		}
	    }
	}
    }

    newlist->left  = NULL;
    newlist->right = NULL;
}


/*******************************************************************************
*	Called at start of new line of output to check page size	       *
*******************************************************************************/
static void startline()
{
    int key;

    if (paged)
    {
	if (currentrow >= screenrows - 1)
	{
	    printf("More:");
	    key = getch();
	    if (key == 0 || key == 0xE0)
	    {
		key = getch();
	    }
	    printf("\r");
	    currentrow = 0;
	    if (key == 0x1B || key == 0x03)
	    {
		exit(0);
	    }
	}
    }
}


/*******************************************************************************
*	Called at end of current line of output -- increment row number        *
*******************************************************************************/
static void endline()
{
    printf("\n");
    currentrow++;
}


/*******************************************************************************
*       Callback to check progress -- unused                                   *
*******************************************************************************/
int processprogress(int flag, int level, char *arg)
{
    if (flag == PROGRESS_RECURSE_INTO)
    {
	if (limitrecursion != 0 && level > limitrecursion)
	{
	    if (errorlimitrecursion)
	    {
		fprintf(stderr, "Directory path: %s\n  is nested to %d levels (exceeds limit of %d).\n",
			arg, level, limitrecursion);
	    }
	    return -1;
	}
    }
    return 0;
}


/*******************************************************************************
*	Callback for each file processed -- this does all the work	       *
*******************************************************************************/
int process(char *path, unsigned attrib, time_t date, long size)
{
    struct tm *file_time;
    char buf[_MAX_PATH], buf2[_MAX_PATH];
    PSAVELIST newlist;
    unsigned __int64 cluster_space;

    /* Check filtering criteria one after the other */

    if (filtcrit != FILT_NONE)
    {
	if (filtcrit & FILT_NAME)
	{
	    if (!(filtdir & FILT_NAME)) /* FILT_ASCEND */
	    {
		/* Secondary "include" pattern */
		if (!match_names(path, filtername, filtcrit, filtcase))
		    return 0;
	    }
	    else
	    {
		/* "exclude" pattern */
		if (match_names(path, filtername, filtcrit, filtcase))
		    return 0;
	    }
	}

	if (filtcrit & FILT_FNAM)
	{
	    if (!(filtdir & FILT_FNAM)) /* FILT_ASCEND */
	    {
		/* Secondary "include" pattern */
		if (!match_names(path, filtername, filtcrit, filtcase))
		    return 0;
	    }
	    else
	    {
		/* "exclude" pattern */
		if (match_names(path, filtername, filtcrit, filtcase))
		    return 0;
	    }
	}

	if (filtcrit & FILT_EXTN)
	{
	    if (!(filtdir & FILT_EXTN)) /* FILT_ASCEND */
	    {
		/* Secondary "include" pattern */
		if (!match_names(path, filtername, filtcrit, filtcase))
		    return 0;
	    }
	    else
	    {
		/* "exclude" pattern */
		if (match_names(path, filtername, filtcrit, filtcase))
		    return 0;
	    }
	}

	if (filtcrit & FILT_DOTS)
	{
	    if (match_names(path, NULL, filtcrit, filtcase))
		return 0;
	}

	if (filtcrit & FILT_DATE)
	{
	    file_time = localtime(&date);
	    if (!(filtdir & FILT_DATE)) /* FILT_ASCEND */
	    {
		if (compare_dates(file_time, &filtertime) < 0)
		    return 0;
	    }
	    else
	    {
		if (compare_dates(file_time, &filtertime) >= 0)
		    return 0;
	    }
	}

	if (filtcrit & FILT_SIZE)
	{
	    if (!(filtdir & FILT_SIZE)) /* FILT_ASCEND */
	    {
		if (size < filtersize)
		    return 0;
	    }
	    else
	    {
		if (size >= filtersize)
		    return 0;
	    }
	}

	if (filtcrit & FILT_ATTR)
	{
	    if (!(filtdir & FILT_ATTR)) /* FILT_ASCEND */
	    {
		/* "include" attribute filter */
		if (!(attrib & filterattr))
		    return 0;
	    }
	    else
	    {
		/* "exclude" attribute filter */
		if (attrib & filterattr)
		    return 0;
	    }
	}
    }

    /* If sorting the list, just save everything for now */

    if (sortcrit != SORT_NONE || savewide)
    {
	if ((newlist = list_alloc(path, attrib, date, size)) != NULL)
	{
	    list_insert(newlist);
	    if (attrib & A_SUBDIR)
		num_directories++;
	    else
		num_files++;
	    return 1;
	}
	printf("%s: Not enough memory to sort (%lu files processed).\n", progname, num_files + num_directories);
	return -1;
    }
    else
    {
	if (!brief && !wide && !totals_only)
	{
	    if (num_files == 0 && num_directories == 0 && !unadorned)
	    {
		startline(); printf(hdr1); endline();
		startline(); printf(hdr2); endline();
	    }

	    startline();

	    buf[0] = ((attrib & A_RDONLY) ? 'R' : ' ');
	    buf[1] = ((attrib & A_HIDDEN) ? 'H' : ' ');
	    buf[2] = ((attrib & A_SYSTEM) ? 'S' : ' ');
	    buf[3] = ((attrib & A_ARCH)   ? 'A' : ' ');
	    buf[4] = EOS;
	    printf(buf);
	    if (attrib & A_SUBDIR)
	    {
		printf("    <DIR>    ");
	    }
	    else
	    {
		format_long(size, buf, TRUE);
		printf(" %11s ", buf);
	    }

	    file_time = localtime(&date);

	    /* It is possible to get a bogus date (I don't know how)    */
	    /* in which case file_time is NULL and strftime crashes, so */
	    /* format a blank time in this case.                        */

	    if (!file_time)
	    {
		memset(buf, ' ', 22);
		buf[22] = EOS;
	    }
	    else
	    {
		strftime(buf, sizeof(buf), "%b %d,%Y %I:%M:%S%p ", file_time);

		/* Fiddle with the output from strftime to clean it up a bit */

		if (buf[4] == '0')
		{
		    buf[4] = ' ';
		}
		if (buf[12] == '0')
		{
		    buf[12] = ' ';
		}
		if (buf[20] == 'A')
		{
		    buf[20] = 'a';
		    buf[21] = 'm';
		}
		if (buf[20] == 'P')
		{
		    buf[20] = 'p';
		    buf[21] = 'm';
		}
	    }
	    printf(buf);
	}
	if (wide)
	{
	    get_path(path, buf);
	    if (*buf != EOS && strcmp(buf, last_path) != 0)
	    {
		if (line_pos > 0)
		{
		    endline();
		    line_pos = 0;
		}
		startline(); endline();
		startline(); printf("%s:", buf); endline();
		startline();
		strcpy(last_path, buf);
	    }
	    else if (num_files == 0 && num_directories == 0)
	    {
		startline();
	    }
	    get_name(path, buf, attrib);
	    if (line_pos + widest_name_len > screencols)
	    {
		endline();
		line_pos = 0;
		startline();
	    }
	    printf("%*s", -widest_name_len, buf);
	    line_pos += widest_name_len;
	}
	else if (!totals_only)
	{
	    if (brief)
	    {
		startline();
	    }
	    if (bare_name)
	    {
		if (full_name)
		{
		    _fullpath(buf2, path, sizeof(buf2));
		    convert_slashes(buf2);
		    printf("%-13s", buf2);
		}
		else
		{
		    /* Don't put [] around directory names here (only in wide form) */
		    get_name(path, buf, A_NORMAL);
		    if (without_ext)
		    {
			char *ext_ptr = strrchr(buf, '.');
			if (ext_ptr != NULL)
			{
			    *ext_ptr = EOS;
			}
		    }
		    printf("%-13s", buf);
		}
	    }
	    else if (full_name)
	    {
		_fullpath(buf2, path, sizeof(buf2));
		convert_slashes(buf2);
		if (quoted)
		{
		    quote_name(buf, buf2);
		    printf("%-13s", buf);
		}
		else
		{
		    printf("%-13s", buf2);
		}
	    }
	    else
	    {
		if (quoted)
		{
		    quote_name(buf, path);
		    convert_slashes(buf);
		    printf("%-13s", buf);
		}
		else
		{
		    if (use_forward_slashes)
		    {
			strcpy(buf, path);
			convert_slashes(buf);
			printf("%-13s", buf);
		    }
		    else
		    {
			printf("%-13s", path);
		    }
		}
	    }
	    endline();
	}
	cluster_space = log_drive(path, size);
	if (!(attrib & A_SUBDIR))
	{
	    total_size	       += size;
	    total_cluster_size += cluster_space;
	    num_files++;
	}
	else
	{
	    num_directories++;
	}
    }
    return 1;
}


/*******************************************************************************
*	Called to reprocess (display) sorted file list after sorting	       *
*******************************************************************************/
static void reprocess(PSAVELIST list)
{
    char path[_MAX_PATH], *name;

    if (list->dir == NULL)
	name = list->name;
    else
    {
	sprintf(path, twos, list->dir->path, list->name);
	name = path;
    }
    process(name, list->attrib, list->date, list->size);
}


/*******************************************************************************
*	Process sorted tree recursively					       *
*******************************************************************************/
static void processtree(PSAVELIST root)
{
    /* Process all files in sorted order */

    if (root != NULL)
    {
	processtree(root->left);

	reprocess(root);

	processtree(root->right);
    }
}


/*******************************************************************************
*	Parse attribute filter list					       *
*******************************************************************************/
static unsigned parseattr(char *arg)
{
    char *ptr;
    unsigned attr = A_NORMAL;

    for (ptr = arg; *ptr; ptr++)
    {
	switch (*ptr)
	{
	    case 'R':
	    case 'r':
		attr |= A_RDONLY;
		break;
	    case 'H':
	    case 'h':
		attr |= A_HIDDEN;
		break;
	    case 'S':
	    case 's':
		attr |= A_SYSTEM;
		break;
	    case 'A':
	    case 'a':
		attr |= A_ARCH;
		break;
	    case 'D':
	    case 'd':
		attr |= A_SUBDIR;
		break;
	    default:
		return 0;
	}
    }
    return attr;
}


/*******************************************************************************
*       Parse filter criteria                                                  *
*******************************************************************************/
static int parsefilter(char *arg, int dir)
{
    char *endptr;

    /* Check for invalid filter conditions:                  */
    /* i.e. more than one name, date, size, or attr criteria */

    switch (arg[1])
    {
	case 'N':
	case 'n':
	case 'F':
	case 'f':
	case 'X':
	case 'x':
	    if (filtcrit & (FILT_NAME | FILT_FNAM | FILT_EXTN))
		return -1;
	    filtcase = 1;
	    break;

	case 'D':
	case 'd':
	    if (filtcrit & FILT_DATE)
		return -1;
	    break;

	case 'S':
	case 's':
	    if (filtcrit & FILT_SIZE)
		return -1;
	    break;

	case 'A':
	case 'a':
	    if (filtcrit & FILT_ATTR)
		return -1;
	    break;
    }

    switch (arg[1])
    {
	case 'N':
	    filtcase = 0;
	case 'n':
	    strcpy(filtername, &arg[2]);
	    filtcrit |= FILT_NAME;
	    if (dir == FILT_DESCEND)
		filtdir |= FILT_NAME;
	    break;

	case 'F':
	    filtcase = 0;
	case 'f':
	    strcpy(filtername, &arg[2]);
	    filtcrit |= FILT_FNAM;
	    if (dir == FILT_DESCEND)
		filtdir |= FILT_FNAM;
	    break;

	case 'X':
	    filtcase = 0;
	case 'x':
	    strcpy(filtername, &arg[2]);
	    filtcrit |= FILT_EXTN;
	    if (dir == FILT_DESCEND)
		filtdir |= FILT_EXTN;
	    break;

	case 'D':
	case 'd':
	    if (parse_date(&arg[2], &filtertime, &endptr) >= 0)
	    {
		if (*endptr)
		{
		    if (parse_time(++endptr, &filtertime, &endptr) > 0)
		    {
			filtcrit |= FILT_DATE;
			if (dir == FILT_DESCEND)
			    filtdir |= FILT_DATE;
		    }
		    else
			return -1;
		}
		else
		{
		    parse_time("0", &filtertime, &endptr);
		    filtcrit |= FILT_DATE;
		    if (dir == FILT_DESCEND)
			filtdir |= FILT_DATE;
		}
	    }
	    else
		return -1;
	    break;

	case 'S':
	case 's':
	    filtersize = strtoul(&arg[2], &endptr, 10);
	    if (filtersize != 0)
	    {
		filtcrit |= FILT_SIZE;
		if (dir == FILT_DESCEND)
		    filtdir |= FILT_SIZE;
	    }
	    else
		return -1;
	    break;

	case 'A':
	case 'a':
	    if ((filterattr = parseattr(&arg[2])) != 0)
	    {
		filtcrit |= FILT_ATTR;
		if (dir == FILT_DESCEND)
		    filtdir |= FILT_ATTR;
	    }
	    else
		return -1;
	    break;

	default:
	    return -1;
    }
    return 1;
}


/*******************************************************************************
*       Parse sort criteria                                                    *
*******************************************************************************/
static int parsesort(char *arg, int dir)
{
    /* More than one sort criteria doesn't make sense */
    if (sortcrit != SORT_NONE)
	return -1;

    sortcase = 0;

    switch (arg[1])
    {
	case 'N':
	    sortcase = 1;
	case 'n':
	    sortcrit = SORT_NAME;
	    sortdir  = dir;
	    break;

	case 'F':
	    sortcase = 1;
	case 'f':
	    sortcrit = SORT_FNAM;
	    sortdir  = dir;
	    break;

	case 'X':
	    sortcase = 1;
	case 'x':
	    sortcrit = SORT_EXTN;
	    sortdir  = dir;
	    break;

	case 'D':
	    sortcase = 1;
	case 'd':
	    sortcrit = SORT_DATE;
	    sortdir  = dir;
	    break;

	case 'S':
	    sortcase = 1;
	case 's':
	    sortcrit = SORT_SIZE;
	    sortdir  = dir;
	    break;

	case 'A':
	    sortcase = 1;
	case 'a':
	    sortcrit = SORT_ATTR;
	    sortdir  = dir;
	    break;

	default:
	    return -1;
    }
    return 1;
}


/*******************************************************************************
*	Callback to process command line arguments			       *
*******************************************************************************/
int processuserarg(char *arg, unsigned flags)
{
    switch (*arg)
    {
	case 'D':
	    filtcrit |= FILT_DOTS;
	    return 1;

	case 'd':
	    directs = 0;
	    return 1;

	case 'B':
	case 'b':
	    brief = 1;
	    return brief;

	case 'W':
	case 'w':
	    /* For second pass, we will have set "savewide" with the real value
	     * and "wide" back to 0 so we will make two passes in order to figure
	     * out the widest name (for a reasonable printout). */
	    if (wide == 0 && savewide == 0)
		wide = 1;
	    return 1;

	case 'Q':
	case 'q':
	    quoted = 1;
	    return quoted;

	case 'F':
	    full_name = 1;
	    return full_name;

	case 'L':
	    errorlimitrecursion = 1;
	    /* fall through */
	case 'l':
	    arg = stpblk(arg+1);
	    if ((limitrecursion = atoi(arg)) <= 0)
		return -1;
	    return 1;

	case 'n':
	    bare_name = 1;
	    return bare_name;

	case 'N':
	    bare_name = 1;
	    without_ext = 1;
	    return bare_name;

	case 'f':
	    if (flags & PROCESS_FILES)
	    {
		return 1;
	    }
	    if (arg[1] == '-')
	    {
		return parsefilter(&arg[1], FILT_DESCEND);
	    }
	    else
	    {
		return parsefilter(arg, FILT_ASCEND);
	    }
	    break;

	case 'O':
	case 'o':
	    if (flags & PROCESS_FILES)
	    {
		return 1;
	    }
	    if (arg[1] == '-')
	    {
		return parsesort(&arg[1], SORT_DESCEND);
	    }
	    else
	    {
		return parsesort(arg, SORT_ASCEND);
	    }
	    break;

	case 'P':
	case 'p':
	    paged = 1;
	    return paged;

	case 'T':
	case 't':
	    totals_only = 1;
	    return totals_only;

	case 'U':
	case 'u':
	    unadorned = 1;
	    return unadorned;

	case '/':
	    use_forward_slashes = 1;
	    return use_forward_slashes;
    }
    return 0;
}


/*******************************************************************************
*	Brief help for program options					       *
*******************************************************************************/
void instruct(void)
{
    printf ("Directory List Program\n");
    printf ("----------------------\n");
    printf ("(%s %s)\n", __DATE__, __TIME__);
    printf ("\nUsage:\n");
    printf (" %s [-s][-h][-y][-d][-e][-b][-l|-L<nn>][-q][-n][-N][-F][-w][-p][-t][-u][-/]\n", progname);
    printf ("\t[-o|-f[-][n,f,x,d,s,a]] Files\n", progname);
    printf ("    -s will search all subdirectories.\n");
    printf ("    -h will find hidden files.\n");
    printf ("    -y will find system files.\n");
    printf ("    -d will skip listing directory names.\n");
    printf ("    -D will skip listing the '.' and '..' directory entries.\n");
    printf ("    -e<path> will specify default path for remaining files.\n");
    printf ("    -b will list only file names without date, size or attributes.\n");
    printf ("    -q will quote file names with embedded blanks.\n");
    printf ("    -n will list names without paths.\n");
    printf ("    -N will list names without paths or extensions.\n");
    printf ("    -F will list names with full paths.\n");
    printf ("    -w will list only names in wide format.\n");
    printf ("    -p will page through the output one screen at a time.\n");
    printf ("    -t will display only total information.\n");
    printf ("    -l or -L will limit recursion to nn levels (-L will report the error).\n");
    printf ("    -u will display unadorned file name info (without headers or totals).\n");
    printf ("    -/ will use \"/\" instead of \"\\\" in paths.\n");
    printf ("    -o[-][n,N,f,F,x,X,d,s,a] sorts by or -f[-][n,N,f,F,x,X,d,s,a] filters by:\n");
    printf ("        name, file name, extension, date, size, or attribute.\n");
    printf ("    Note: Files can be @indirectfile.\n");
    printf ("    Note: drive and directory names can be wild cards (*?) also.\n");
    printf ("\nExamples:\n");
    printf ("    %s *.c *.h\t\t\tList all .c and .h files in current directory.\n", progname);
    printf ("    %s \\*.* /s\t\t\tList all files on current drive.\n", progname);
    printf ("    %s \\win\\*.ini /os\t\tList all Windows .ini files sorted by size.\n",progname);
    printf ("    %s \\bin\\*.exe /fd9-16\tList all .EXE files changed since 9-16.\n",progname);
    printf ("    %s \\spjx?\\win\\*\\.rc\t\tList all .RC files in the SPJX?\\WIN areas.\n",progname);
}


/*******************************************************************************
*	Main Program							       *
*******************************************************************************/
int main(int argc, char **argv)
{
    char sizbuf[40], numdbuf[40], numfbuf[40], sdbuf[4], sfbuf[4];
    int  ret;
    unsigned drive;
    unsigned __int64 freespace;
#ifdef WIN32
    CONSOLE_SCREEN_BUFFER_INFO csbInfo;
    HANDLE hConsole;
#else
    struct _videoconfig vc;
#endif /* !WIN32 */
    time_t timer;

    quiet = directs = dirlist = 1;

    /* Get current system date & time as a default */

    time(&timer);
    filtertime = *localtime(&timer);

    /* Save current disk drive for log_drive() above */

    current_drive = _getdrive();

    /* Get screen size */

#ifdef WIN32
    hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
    GetConsoleScreenBufferInfo(hConsole, &csbInfo);
    screenrows = csbInfo.dwSize.Y;
    screencols = csbInfo.dwSize.X;
#else
    _getvideoconfig(&vc);
    screenrows = vc.numtextrows;
    screencols = vc.numtextcols;
#endif

    if ((ret = processcmdline(argc, argv, PROCESS_OPTIONS)) < 0)
    {
	instruct();
	return 1;
    }
    if (argc - ret == 1)
    {
	argv[argc++] = "*";
    }

    /* Do a fiddle with "wide" and "savewide" in order to buffer everything. */
    savewide = wide;
    wide = 0;

    /* Initialize local memory allocator */
    if ((sortcrit != SORT_NONE || savewide) && !mem_init())
    {
	printf("%s: Not enough memory to sort!\n", progname);
	return 1;
    }

    if (processcmdline(argc, argv, PROCESS_FILES | PROCESS_OPTIONS) > 0)
    {
	if (sortcrit != SORT_NONE || savewide)
	{
	    sortcrit        = SORT_NONE;
	    wide            = savewide;
	    savewide        = 0;
	    num_files       = 0L;
	    num_directories = 0L;

	    widest_name_len = max(widest_name_len + 1, 15);
	    /* If the column width divides the screen width exactly
	     * we get a blank line between each line, so bump the column
	     * width so we get one less column, but avoid the blank lines. */
	    if (screencols % widest_name_len == 0)
		widest_name_len++;

	    processtree(listhead);
	}
	if (wide)
	{
	    endline();
	}
	if (!brief && !unadorned)
	{
	    format_int64(total_size,      sizbuf,  TRUE);
	    format_long (num_files,       numfbuf, TRUE);
	    format_long (num_directories, numdbuf, TRUE);
	    if (num_files == 1)
	    {
		sfbuf[0] = EOS;
	    }
	    else
	    {
		sfbuf[0] = 's';
		sfbuf[1] = EOS;
	    }
	    if (num_directories == 1)
	    {
		sdbuf[0] = 'y';
		sdbuf[1] = EOS;
	    }
	    else
	    {
		sdbuf[0] = 'i';
		sdbuf[1] = 'e';
		sdbuf[2] = 's';
		sdbuf[3] = EOS;
	    }
	    if (wide)
	    {
		startline(); endline();
		startline();
		printf("  %14s bytes in %s file%s, %s director%s.",
			  sizbuf, numfbuf, sfbuf, numdbuf, sdbuf);
		endline();
	    }
	    else
	    {
		if (!totals_only)
		{
		    startline(); printf(ftr1); endline();
		}
		startline(); printf(ftr2, sizbuf, numfbuf, sfbuf); endline();
	    }
	    format_int64(total_cluster_size, sizbuf, TRUE);
	    startline();
	    if (wide)
	    {
		printf("  %14s bytes of space used.", sizbuf);
	    }
	    else
	    {
		printf(ftr3, sizbuf, numdbuf, sdbuf);
	    }
	    endline();

	    /* Report the free space on all the drives we touched */

	    for (drive = 0; drive < sizeof(drives) / sizeof(drives[0]); drive++)
	    {
		if (drives[drive].used)
		{
		    freespace = (unsigned __int64)drives[drive].avail_clusters *
				drives[drive].cluster_size;
		    format_int64(freespace, sizbuf, TRUE);
		    startline();
		    printf("%c:%14s bytes free.", drive + 'A', sizbuf);
		    endline();
		}
	    }
	}
    }
    mem_deinit();
    return 0;
}

/* End of file D.C */
