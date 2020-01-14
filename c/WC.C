/*******************************************************************************
*                                                                              *
*       Word counting program                                                  *
*                                                                              *
*           Taken from K&R pg. 18                                              *
*           Modified to use unsigned long for counts                           *
*           Further modified to use cmdline routines to process multiple files *
*	    Also modified to open file in binary mode to give accurate char    *
*		count under MS-DOS environments where newlines are CR-LF       *
*                                                                              *
*******************************************************************************/

#include <stdio.h>
#include "cmdline.h"
#include "util.h"

#define   YES  1
#define   NO   0

static unsigned long nl, nw, nc;

void count(FILE *fp)
{
     int c, inword;

     inword = NO;
     while ((c = getc(fp)) != EOF) {
          ++nc;
          if (c == '\n')
               ++nl;
          if (c == ' ' || c == '\r' || c == '\n' || c == '\t')
               inword = NO;
          else if (inword == NO) {
               inword = YES;
               ++nw;
          }
     }
}


int processprogress(int flag, int level, char *arg)
{
    return 0;
}


int process(char *path, unsigned attrib, time_t date, long size)
{
     FILE *fp;

     if ((fp = fopen(path, "rb")) != NULL) {
          count(fp);
          fclose(fp);
          return 1;
     }
     return -1;
}


int processuserarg(char *arg, unsigned flags)
{
    return 0;
}


void main(int argc, char **argv)    /* count lines, word, chars in input */
{
    int ret;
    char buf[80];

    nl = nw = nc = 0L;

    quiet = 1;
    if ((ret = processcmdline(argc, argv, PROCESS_OPTIONS)) < 0)
	return;

    if (argc - ret < 2)
	count(stdin);
    else
	processcmdline(argc, argv, PROCESS_FILES);

    format_long(nl, buf, TRUE);
    printf(buf);
    format_long(nw, buf, TRUE);
    printf("\t%s", buf);
    format_long(nc, buf, TRUE);
    printf("\t%s\n", buf);
}
