#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <direct.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <io.h>
#include <conio.h>
#include "cmdline.h"
#include "util.h"


static int prompt          = 1;
static int quietly	   = 0;
static int delete_readonly = 0;

static unsigned long files_deleted   = 0L;
static unsigned long files_processed = 0L;
static unsigned long dirs_removed    = 0L;
static unsigned long dirs_processed  = 0L;


typedef struct _SAVEDIR
{
    struct _SAVEDIR *next;
    unsigned char attr;
    char   name[1];
} SAVEDIR, *PSAVEDIR;

static int removedir = 0;
static PSAVEDIR root = NULL;


static int savedir(char *path, unsigned attrib)
{
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    char newpath[_MAX_PATH];
    unsigned len;
    PSAVEDIR psave;

    _splitpath(path, drive, dir, fname, ext);
    if (fname[0] == '.' && ext[0] == '.' && dir[0] != '\0')
    {
	_makepath(newpath, drive, dir, NULL, NULL);
	len = strlen(newpath);
        if (newpath[len - 1] == '\\')
	{
            newpath[--len] = '\0';
	}
	if ((psave = (PSAVEDIR)malloc(sizeof(SAVEDIR) + len)) != NULL)
	{
	    psave->next = root;
	    root = psave;
	    strcpy(psave->name, newpath);
	    psave->attr = (unsigned char)attrib;
	    return 1;
	}
    }
    return 0;
}


void status(char *name, unsigned attrib, char *deleted)
{
    if (quietly)
	printf(name);

    if (errno == EACCES)
    {
	if (attrib & A_RDONLY)
	{
            printf(" is READ-ONLY.\n");
	}
	else
	{
            printf(" is in use.\n");
	}
    }
    else if (errno == ENOENT)
        printf(" not found.\n");
    else
        printf(" NOT %s.\n", deleted);
}


int processprogress(int flag, int level, char *arg)
{
    int ret = 0;
    int key;

    switch (flag)
    {
        case PROGRESS_RECURSE_INTO:
        case PROGRESS_RECURSE_OUT:
            break;

        case PROGRESS_WILD_START:
            if (prompt)
            {
                printf("OK to delete \"%s\"?", arg);
                key = getch();
                if (key == 0 || key == 0xE0)
                {
                    key = getch();
                }
                if (key == 0x1B || key == 0x03)
                {
                    printf("\n");
                    exit(0);
                }
                if (key == 'y' || key == 'Y')
                {
                    printf("Yes\n");
                }
                else
                {
                    printf("No\n");
                    ret = -1;
                }
            }
            break;

        case PROGRESS_WILD_FINISH:
            break;
    }
    return ret;
}


int process(char *path, unsigned attrib, time_t date, long size)
{
    int ret;
    int tries = 0;

    if (!(attrib & A_SUBDIR))
    {
	files_processed++;
	if (!quietly)
	    printf(path);
try_again:
	ret = remove(path);

	if (ret == 0)
	{
	    files_deleted++;
	    if (!quietly)
	    {
		if (attrib & A_RDONLY)
                    printf(" was READ-ONLY but now");
                printf(" deleted.\n");
	    }
	}
	else
	{
	    if ((errno == EACCES) && (attrib & A_RDONLY) &&
		(delete_readonly && tries++ == 0) &&
		(_chmod(path, _S_IWRITE | _S_IREAD) == 0))
		goto try_again;
	    else
                status(path, attrib, "deleted");
	    return -1;
	}
	return 1;
    }
    else if (removedir)
    {
	if (savedir(path, attrib))
	    return 1;
    }
    return 0;
}


static void processsavedir(void)
{
    PSAVEDIR save, next;
    int ret;

    if (removedir && root != NULL)
    {
	for (save = root; save; save = next)
	{
	    dirs_processed++;
	    next = save->next;
	    if (!quietly)
		printf(save->name);

	    ret = _rmdir(save->name);

	    if (ret == 0)
	    {
		dirs_removed++;
		if (!quietly)
                    printf(" removed.\n");
	    }
	    else
	    {
                status(save->name, save->attr, "removed");
	    }
	    free(save);
	}
    }
}


void instruct(void)
{
    printf("eXtended DELete utility\n");
    printf("-----------------------\n");
    printf("Usage: %s [-i] [-h] [-y] [-a] [-e<path>] [-s] [-r] [-q] [-p] [-?] <Name(s)>\n", progname);
    printf("\tuse -i to ignore any errors encountered and keep going.\n");
    printf("\tuse -h to delete hidden files as well.\n");
    printf("\tuse -y to delete system files as well.\n");
    printf("\tuse -a to delete read-only files also.\n");
    printf("\tuse -e<path> to specify default path for remaining files.\n");
    printf("\tuse -s to process files in subdirectories also.\n");
    printf("\tuse -r to remove directory entries also.\n");
    printf("\tuse -q to quietly do the work.\n");
    printf("\tuse -p to not prompt on wild card deletes.\n");
    printf("\t  Name(s) can be '@<Indirect file>'.\n");
}


int processuserarg(char *arg, unsigned flags)
{
    switch (*arg)
    {
        case 'E':
        case 'e':
        case 'I':
        case 'i':
        case 'H':
        case 'h':
        case 'Y':
        case 'y':
        case 'S':
        case 's':
        case '?':
	    /* Allow default processing */
	    break;

        case 'P':
        case 'p':
            prompt = 0;
            return 1;

        case 'Q':
        case 'q':
	    quietly = 1;
	    return 1;

        case 'R':
        case 'r':
	    removedir = directs = 1;
	    return 1;

        case 'a':
        case 'A':
            /* Don't ask why 'A' for this option:  maybe for 'attribute'?! */
	    delete_readonly = 1;
	    return 1;

	default:
	    return -1;
    }
    return 0;
}


static void report(unsigned long processed, unsigned long deleted,
		   char *proc_spec, char *mult_spec, char *del_spec)
{
    char buf[80];

    number_string(buf, processed, TRUE);
    printf("%s ", buf);
    if (processed == 1L)
        printf("%s ", proc_spec);
    else
        printf("%s ", mult_spec);

    if (deleted == processed)
        printf("%s.\n", del_spec);
    else
    {
        printf("processed, but ");
	if (deleted != 0L)
            printf("only ");
	number_string(buf, deleted, FALSE);
        printf("%s %s.\n", buf, del_spec);
    }
}


static void reporttotals(void)
{
    if (!quietly)
    {
	if (files_processed == 0L)
	{
            printf("NO files processed!\n");
	}
	else
	{
            report(files_processed, files_deleted, "file", "files", "deleted");
	}
	if (dirs_processed != 0L)
	{
            report(dirs_processed, dirs_removed, "directory", "directories",
                   "removed");
	}
    }
}


int main(int argc, char **argv)
{
    quiet = 1;
    directs = 0;
    if (processcmdline(argc, argv, PROCESS_OPTIONS) < 0 || argc < 2)
    {
	instruct();
	return 0;
    }
    processcmdline(argc, argv, PROCESS_FILES | PROCESS_OPTIONS);
    processsavedir();
    reporttotals();
    return 0;
}
