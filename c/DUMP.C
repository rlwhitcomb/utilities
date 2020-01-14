/*******************************************************************************
*       Binary/hex dumpfile utility                                            *
*******************************************************************************/

#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <stdio.h>
#include <io.h>
#include <stdlib.h>
#include <string.h>
#include <share.h>
#include <sys\types.h>
#include <sys\stat.h>
#include "cmdline.h"
#include "util.h"

#define MAX_BUFFER  16


static void dump(FILE *fp, long offset, int size)
{
    int c = EOF, count, i, bufPos;
    char buffer[MAX_BUFFER + 3];

    count = bufPos = 0;

    if (size == 0)
        return;

    for (;!feof(fp) && !ferror(fp) && size > 0; size--) {
        if ((c = fgetc(fp)) == EOF) {
            break;
        }

        if (count == 0) {
            printf(" %06lX:  ", offset);
            offset += MAX_BUFFER;
        }
        count++;

        if (c >= ' ') {
            buffer[bufPos++] = (char)c;
        } else {
            buffer[bufPos++] = '.';
        }
        printf("%02X ", c);
        if (count == (MAX_BUFFER / 2)) {
            printf("  ");
            buffer[bufPos++] = ' ';
            buffer[bufPos++] = ' ';
        }

        if (count == MAX_BUFFER) {
            buffer[bufPos] = '\0';
            printf(" %s\n", buffer);
            count = 0;
            bufPos = 0;
        }
    }
    if (bufPos > 0) {
        buffer[bufPos] = '\0';
        if (count < (MAX_BUFFER / 2)) {
            printf("  ");
        }
        for(i=count; i<MAX_BUFFER; i++) {
            printf("   ");
        }
        printf(" %s\n", buffer);
    }
}


void usage()
{
    printf("Usage: DUMP <filespec[s]> {options}\n");
    printf("\n");
}


int processprogress(int flag, int level, char *arg)
{
    return 0;
}


int process(char *path, unsigned attrib, time_t date, long size)
{
    FILE *fp;
    long offset = 0L;

    printf("File: %s\n", path);
    if ((fp = _fsopen(path, "rb", _SH_DENYNO)) == NULL)
    {
        printf("DUMP: Cannot open \"%s\".\n", path);
        return -1;
    }
    while (!feof(fp) && !ferror(fp))
    {
        dump(fp, offset, MAX_BUFFER);
        offset += MAX_BUFFER;
    }
    fclose(fp);
    printf("\n");

    return 0;
}


int processuserarg(char *arg, unsigned flags)
{
    return 0;
}


int main(int argc, char **argv)
{
    if (argc < 2)
    {
        usage();
        return 1;
    }
    processcmdline(argc, argv, PROCESS_OPTIONS | PROCESS_FILES);
    return 0;
}
