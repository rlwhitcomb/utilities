#include <stdio.h>

/*******************************************************************************
*       Set errorlevel based on comparison of command line arguments           *
*******************************************************************************/
void usage()
{
    fprintf(stderr, "Usage: compare [-i] <arg1> <arg2>\n");
    fprintf(stderr, "\tuse -i to compare case insensitive\n");
    fprintf(stderr, "\tsets errorlevel 0 if equal\n");
    fprintf(stderr, "\t     errorlevel 1 if arg1 > arg2\n");
    fprintf(stderr, "\t     errorlevel 2 if arg2 > arg1\n");
}


int main (int argc, char **argv)
{
    char *a1, *a2;
    int cmp, i;
    char arg1[1024], arg2[1024];
    int case_sense = 1;

    /* Need exactly two or three arguments */
    if (argc != 3 && argc != 4)
    {
        usage();
        return 255;
    }

    /* Decide which argument has the switch in it */
    if (argc == 4)
    {
        a1 = a2 = NULL;
        for (i = 1; i < argc; i++)
        {
            if (*argv[i] == '-')
            {
                if (*argv[i] == 'i' || *argv[i] == 'I')
                {
                    case_sense = 0;
                }
                else
                {
                    usage();
                    return 255;
                }
            }
            else if (!a1)
            {
                a1 = argv[i];
            }
            else
            {
                a2 = argv[i];
            }
        }
    }
    else
    {
        a1 = argv[1];
        a2 = argv[2];
    }

    /* Read standard input if either (or both) arguments refer to '@' */

    if (strcmp(a1, "@") == 0)
    {
        cmp = scanf("%s\n", arg1);
        a1 = arg1;
    }
    if (strcmp(a2, "@") == 0)
    {
        cmp = scanf("%s\n", arg2);
        a2 = arg2;
    }

    if (case_sense)
        cmp = strcmp(a1, a2);
    else
        cmp = stricmp(a1, a2);

    if (cmp == 0)
        return 0;
    else if (cmp > 0)
        return 1;
    else
        return 2;
}
