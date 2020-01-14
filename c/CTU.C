/*******************************************************************************
*       Win32 implementation of DER's CTU (16-bit) utility                     *
*******************************************************************************/

#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <stdio.h>
#include <io.h>
#include <stdlib.h>
#include <string.h>
#include <sys\types.h>
#include <sys\stat.h>
#include "cmdline.h"
#include "util.h"


static char src_path[_MAX_PATH] = "";
static char dest_path[_MAX_PATH];

static unsigned options = 0;

#define DEST_YOUNGER    0x00000001
#define SOURCE_YOUNGER  0x00000002
#define DEST_MISSING    0x00000004
#define FILES_EQUAL     0x00000008
#define REMOVE_FILES    0x00000010
#define REPORT_ONLY     0x00000020
#define QUOTE_NAMES     0x00000040

static int dest_fat = 0;


int IsFATVolume(char *path)
{
    char drive[_MAX_DRIVE], FSName[20];
    DWORD dwMaxLen, dwFSFlags;

    _splitpath(path, drive, NULL, NULL, NULL);
    strcat(drive, "\\");
    GetVolumeInformation(drive, NULL, 0, NULL, &dwMaxLen, &dwFSFlags,
                         FSName, sizeof(FSName));
    return (memcmp(FSName, "FAT", 3) == 0) ? TRUE : FALSE;
}


void usage()
{
    printf("Usage: CTU <sourcespec> <destdir> {options}\n");
    printf("\n");
    printf("  This program compares dates and times of a set of\n");
    printf("  files (given by <sourcespec>) with a similar set on\n");
    printf("  some other drive or directory (<destdir>).\n");
    printf("\n");
    printf("  Files which differ on the destination directory are\n");
    printf("  copied from the source directory.\n");
    printf("\n");
    printf("  By default, CTU will copy a file to the destination\n");
    printf("  directory if its date differs from the source, or if\n");
    printf("  it is missing from the destination.\n");
    printf("\n");
    printf("  The {options} specify which files are copied to destination:\n");
    printf("\n");
    printf("    /s -- copy if source file is younger\n");
    printf("    /d -- copy if destination file is younger\n");
    printf("    /m -- copy if destination file is missing\n");
    printf("    /e -- copy if files are equal\n");
    printf("    /r -- remove file from destination if non-existent on source\n");
    printf("          (Will remove ONLY those files matching <sourcespec>.)\n");
    printf("          [Currently not implemented in Win32 version].\n");
    printf("    /l<dir> -- specify default source location\n");
    printf("    /c -- don't perform the copy operation, just report differences.\n");
    printf("    /q -- if /c specified, quote file names with blanks.\n");
}


int processprogress(int flag, int level, char *arg)
{
    return 0;
}


time_t round_times(time_t date)
{
    return (date + 2) & ~(0x1);
}


int process(char *path, unsigned attrib, time_t date, long size)
{
    char fullpath[_MAX_PATH];
    char newpath[_MAX_PATH], fname[_MAX_FNAME], ext[_MAX_EXT];
    struct _stat dst_stat;
    unsigned copy;
    int src_fat;
    DWORD dwError;
    LPVOID lpMsgBuf;

    _fullpath(fullpath, path, sizeof(fullpath));
    _splitpath(fullpath, NULL, NULL, fname, ext);
    _makepath(newpath, NULL, dest_path, fname, ext);

    copy = 0;
    if (_access(newpath, 0) == 0)
    {
        _stat(newpath, &dst_stat);
        /*
        ** Times are funny when moving to/from FAT and non-FAT volumes
        ** therefore, if the source and destination are different,
        ** round the times so the comparison is meaningful.
        */
        if (date != dst_stat.st_mtime)
        {
            src_fat = IsFATVolume(fullpath);
            if (src_fat != dest_fat)
            {
                if (dest_fat)
                    date = round_times(date);
                if (src_fat)
                    dst_stat.st_mtime = round_times(dst_stat.st_mtime);
            }
        }
        if (date < dst_stat.st_mtime)
            copy |= DEST_YOUNGER;
        if (date > dst_stat.st_mtime)
            copy |= SOURCE_YOUNGER;
        if (date == dst_stat.st_mtime)
            copy |= FILES_EQUAL;
    }
    else
    {
        copy |= DEST_MISSING;
    }

    if ((copy & options))
    {
        if (options & REPORT_ONLY)
        {
            if ((options & QUOTE_NAMES) && strchr(fullpath, ' ') != NULL)
                printf("\"%s\"\n", fullpath);
            else
                printf("%s\n", fullpath);
        }
        else
        {
            printf("Copying %s to %s\n", fullpath, newpath);
            if (!CopyFile(fullpath, newpath, FALSE))
            {
                dwError = GetLastError();
                FormatMessage(
                     FORMAT_MESSAGE_ALLOCATE_BUFFER |
                     FORMAT_MESSAGE_FROM_SYSTEM |
                     FORMAT_MESSAGE_IGNORE_INSERTS,
                     NULL,
                     dwError,
                     MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
                     (LPTSTR) &lpMsgBuf,
                     0,
                     NULL );
                printf("Error %ld: %s\n", dwError, lpMsgBuf);
                LocalFree( lpMsgBuf );
                return -1;
            }
        }
    }
    return 0;
}


int processuserarg(char *arg, unsigned flags)
{
    static int in_l = 0;

    if (in_l)
        return 0;

    while (*arg)
    {
        if (*arg == 'L' || *arg == 'l')
        {
            /* Ugly code: we have to fake out our caller and ourselves */
            /* to process this option by a different name.             */

            in_l = 1;
            *arg = 'E';
            processarg(arg-1, PROCESS_FILES);
            in_l = 0;
            return 1;
        }
        if (*arg == 'D' || *arg == 'd')
        {
            options |= DEST_YOUNGER;
        }
        if (*arg == 'S' || *arg == 's')
        {
            options |= SOURCE_YOUNGER;
        }
        if (*arg == 'M' || *arg == 'm')
        {
            options |= DEST_MISSING;
        }
        if (*arg == 'E' || *arg == 'e')
        {
            options |= FILES_EQUAL;
        }
        if (*arg == 'R' || *arg == 'r')
        {
            options |= REMOVE_FILES;
        }
        if (*arg == 'C' || *arg == 'c')
        {
            options |= REPORT_ONLY;
        }
        if (*arg == 'Q' || *arg == 'q')
        {
            options |= QUOTE_NAMES;
        }
        arg++;
    }
    return 1;
}


int main(int argc, char **argv)
{
    int i;

    extractprogramname(argv[0]);

    if (argc > 2)
    {
        _fullpath(dest_path, argv[2], sizeof(dest_path));
        dest_fat = IsFATVolume(dest_path);
        if (argc > 3)
        {
            for (i = 3; i < argc; i++)
            {
                processarg(argv[i], PROCESS_OPTIONS);
            }
        }
        else
        {
            options = DEST_YOUNGER | SOURCE_YOUNGER | DEST_MISSING;
        }
        /* Make sure we have some options besides REPORT_ONLY */
        if (!(options & ~REPORT_ONLY))
        {
            options |= DEST_YOUNGER | SOURCE_YOUNGER | DEST_MISSING;
        }
        processfilelist(argv[1], 0, PROCESS_FILES);
        return 0;
    }
    else
    {
        usage();
        return 1;
    }
}
