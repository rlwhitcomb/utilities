#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <io.h>
#include "cmdline.h"


static int verbose = 0;


int processprogress(int flag, int level, char *arg)
{
    return 0;
}


int process(char *path, unsigned attrib, time_t date, long size)
{
    FILE *in, *out;
    char outname[_MAX_PATH];
    int ret = -1;
    unsigned uPos;
    int ch;
    int filenumber = 0;

    if ((in = fopen(path, "rb")) != NULL)
    {
        do
        {
            sprintf(outname, "%s.%d", path, filenumber++);
        } while (_access(outname, 0) == 0);

        if ((out = fopen(outname, "wb")) != NULL)
        {
            if (verbose)
                printf("Converting \"%s\" to \"%s\" ...", path, outname);

            uPos = 0;
            while (!feof(in) && !ferror(in))
            {
                ch = fgetc(in);
                if (ch == EOF)
                    break;

                if (ch == '\n')
                {
                    uPos = 0;
                    fputc(ch, out);
                }
                else if (ch == '\t')
                {
                    do {
                        fputc(' ', out);
                        uPos++;
                    } while ((uPos % 8) != 0);
                }
                else
                {
                    uPos++;
                    fputc(ch, out);
                }
            }
            fclose(out);

            ret = 0;
        }
        fclose(in);

        if (verbose)
            printf("\n");
    }
    return ret;
}

int processuserarg(char *arg, unsigned flags)
{
    if (*arg == 'v' || *arg == 'V')
    {
        verbose = 1;
        return 1;
    }
    return 0;
}


int main(int argc, char **argv)
{
    processcmdline(argc, argv, PROCESS_FILES | PROCESS_OPTIONS);

    return 0;
}
