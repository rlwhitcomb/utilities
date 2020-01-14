/*******************************************************************************
*	File Renaming Utility						       *
*******************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <dos.h>
#include <time.h>
#include <string.h>
#include <malloc.h>
#include <limits.h>
#include <errno.h>
#ifdef WIN32
#include <windows.h>
#endif
#include "cmdline.h"
#include "parse.h"
#include "util.h"
#include "match.h"


struct charmap {
	struct charmap *next;
	struct charmap *prev;
	short fromlen;
	short tolen;
	char from[10];
	char to[10];
} *char_head = 0,
 *char_tail = 0;

#define LIST_INSERT(elem, head, tail) \
	elem->next = head; \
	elem->prev = 0; \
	if (head == 0) \
	    tail = elem; \
	else \
	    head->prev = elem; \
	head = elem


static int runquiet = 0;
static int ignoreerrors = 0;
static int reportonly = 0;
static long numfilesprocessed = 0L;

/*******************************************************************************
*       Callback to check progress -- unused                                   *
*******************************************************************************/
int processprogress(int flag, int level, char *arg)
{
    return 0;
}


/*******************************************************************************
*	Lookup a character mapping					       *
*******************************************************************************/
static struct charmap *ismapped(char *ptr)
{
    struct charmap *cm;
    /* Later values supercede earlier ones, so search from last to first */
    for (cm = char_head; cm != NULL; cm = cm->next)
    {
	if (*ptr == cm->from[0])
	{
	    if (memcmp(ptr+1, &cm->from[1], cm->fromlen-1) == 0)
		break;
	}
    }
    return cm;
}

/*******************************************************************************
*	Fixup a	name doing the character translations			       *
*******************************************************************************/
static int fixup(char *name)
{
    int changed = FALSE;
    char *in, *out;
    struct charmap *map;

    in = out = name;
    /* "processconfigfile" checks that "to" string is never longer than "from" */
    /* string, so it is alright to process the string in place.                */
    while (*in != '\0') {
	if ((map = ismapped(in)) != NULL) {
	    memcpy(out, map->to, map->tolen);
	    in += map->fromlen;
	    out += map->tolen;
	    changed = TRUE;
	}
	else
	    *out++ = *in++;
    }
    *out = '\0';
    return changed;
}

/*******************************************************************************
*	Callback for each file processed -- this does all the work	       *
*******************************************************************************/
int process(char *path, unsigned attrib, time_t date, long size)
{
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    char newpath[_MAX_PATH];
    char *msg;
    int ret;

    _splitpath(path, drive, dir, fname, ext);
    /* Fix up "fname" and "ext" based on the character mappings provided */
    if (fixup(fname) || fixup(ext))
    {
	_makepath(newpath, drive, dir, fname, ext);

	if (!runquiet) {
	    printf("Renaming '%s'\n      to '%s'...\n", path, newpath);
	    fflush(stdout);
	}

	if (!reportonly)
	{
	    if ((ret = rename(path, newpath)) != 0)
	    {
		switch (errno)
		{
		    case EACCES:
			msg = "Could not rename the file: permission denied.";
			break;
		    case ENOENT:
			msg = "Could not find file to rename it: already deleted or renamed?";
			break;
		    case EINVAL:
			msg = "Invalid new file name: character mappings may be invalid.";
			break;
		    case EEXIST:
			msg = "New file name already in use.";
			break;
		    default:
			msg = "Unknown error!";
			break;
		}
		fprintf(stderr, "Error %d trying to rename file: '%s'\n\t%s\n", errno, path, msg);
		fflush(stderr);
		if (ignoreerrors)
		    return 0;
		return -1;
	    }
	}
	numfilesprocessed++;
	return 1;
    }

    return 0;
}


/*******************************************************************************
*	Callback to process command line arguments			       *
*******************************************************************************/
int processuserarg(char *arg, unsigned flags)
{
    int status = 0;

    switch (*arg)
    {
	case 'i':
	case 'I':
	    ignoreerrors = status = 1;
	    break;
	case 'q':
	case 'Q':
	    runquiet = status = 1;
	    break;
	case 'r':
	case 'R':
	    reportonly = status = 1;
	    break;
	case 's':
	case 'S':
	    subdir = status = 1;
	    break;
    }
    return status;
}


/*******************************************************************************
*	Brief help for program options					       *
*******************************************************************************/
void instruct(void)
{
    printf ("File Renaming Program\n");
    printf ("---------------------\n");
    printf ("Usage:\n");
    printf (" %s [-i][-q][-r][-s] [Files]\n", progname);
    printf ("Given input files will be renamed according to the character mappings\n");
    printf ("  listed in the '%s.ini' file\n", progname);
    printf ("\t-i will Ignore errors and keep going\n");
    printf ("\t-q will do the work Quietly\n");
    printf ("\t-r will only Report the files but not actually rename them\n");
    printf ("\t-s will recurse all Subdirectories under the current directory\n");
}


static int processconfigfile()
{
    char fullpath[_MAX_PATH+1];
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    char line[1024+1];
    char *ptr;
    FILE *inifile;
    long lineno;
    int len;
    short ix;

    /* Find where we were run from and try to read our configuration file from there */
    GetModuleFileName((HMODULE)NULL, fullpath, sizeof(fullpath));
    _splitpath(fullpath, drive, dir, fname, ext);
    _makepath(fullpath, drive, dir, "renfile", "ini");
    if ((inifile = fopen(fullpath, "r")) == NULL) {
	fprintf(stderr, "Could not open the configuration file: '%s'\n", fullpath);
	return 1;
    }
    lineno = 0;
    /* Read through the configuration file and build a list of mapping entries from there */
    while (!feof(inifile)) {
	lineno++;
	if (fgets(line, sizeof(line), inifile) != NULL)
	{
	    char startch;
	    struct charmap *cm;
	    len = strlen(line) - 1;
            if (line[len] == '\n')
                line[len] = EOS;

	    /* Ignore leading whitespace */
	    ptr = stpblk(line);
	    /* Skip blank or comment lines */
	    if (*ptr == '\0' || *ptr == '!' || *ptr == '#')
		continue;
	    /* Parse the normal line: ['"]c['"]\s=\s['"]c['"] */
	    cm = (struct charmap *)calloc(1, sizeof(struct charmap));
	    startch = *ptr++;
	    ix = 0;
	    if (startch != '\'' && startch != '"') {
		cm->from[ix++] = startch;
	    }
	    else {
		do {
		    cm->from[ix++] = *ptr++;
		} while (*ptr != startch && *ptr != '\0' && ix < 9);
		if (*ptr != startch)
		    goto syntaxerror;
		ptr++;
	    }
	    cm->from[ix] = '\0';
	    cm->fromlen = ix;
	    ptr = stpblk(ptr);
	    if (*ptr++ != '=')
		goto syntaxerror;
	    ptr = stpblk(ptr);
	    startch = *ptr++;
	    ix = 0;
	    if (startch != '\'' && startch != '"') {
		cm->to[ix++] = startch;
	    }
	    else {
		do {
		    cm->to[ix++] = *ptr++;
		} while (*ptr != startch && *ptr != '\0' && ix < 9);
		if (*ptr != startch)
		    goto syntaxerror;
	    }
	    cm->to[ix] = '\0';
	    cm->tolen = ix;
	    if (cm->fromlen < cm->tolen)
	    {
		fprintf(stderr, "'to' string is longer than the 'from' string -- not supported!\n");
		goto syntaxerror;
	    }
	    LIST_INSERT(cm, char_head, char_tail);
	}
    }
    fclose(inifile);
    return 0;

syntaxerror:
    fprintf(stderr, "Syntax error at line %ld: %s\n", lineno, line);
    fclose(inifile);
    return 2;
}


/*******************************************************************************
*	Main Program							       *
*******************************************************************************/
int main(int argc, char **argv)
{
    int  ret;

    quiet = 1;

    /* Try to read our configuration file from where we were run from */
    if ((ret = processconfigfile()) != 0)
	return ret;

    /* Process all the command-line options first */
    if ((ret = processcmdline(argc, argv, PROCESS_OPTIONS)) < 0)
    {
	instruct();
	return 1;
    }

    /* If no filenames were listed, default to processing all files */
    if (argc - ret == 1)
    {
        argv[argc++] = "*";
    }

    /* Now, process all the files listed */
    if (processcmdline(argc, argv, PROCESS_FILES) > 0)
    {
	printf("Total of %ld file(s) %s.\n", numfilesprocessed, (reportonly ? "found":"renamed"));
    }

    return 0;
}

/* End of file renfile.c */
