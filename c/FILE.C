#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void usage(void)
{
    fprintf(stderr, "Usage: file <filename> {new|part|PART|Part} {drive|dir|fname|ext} [new]\n");
}


int main(int argc, char **argv)
{
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    char path[_MAX_PATH];
    int i;

    if (argc < 4)
    {
        usage();
        return 1;
    }

    do
    {
        if (strcmp(argv[1], "@") == 0)
        {
            i = scanf("%s\n", path);
        }
        else
        {
            strcpy(path, argv[1]);
            i = 0;
        }

        /* Replace part of the file name */
        if (strcmpi(argv[2], "new") == 0)
        {
            _splitpath(path, drive, dir, fname, ext);

            if (strcmpi(argv[3], "drive") == 0)
            {
                _makepath(path, argv[4], dir, fname, ext);
                printf(path);
            }
            else if (strcmpi(argv[3], "dir") == 0)
            {
                _makepath(path, drive, argv[4], fname, ext);
                printf(path);
            }
            else if (strcmpi(argv[3], "fname") == 0)
            {
                _makepath(path, drive, dir, argv[4], ext);
                printf(path);
            }
            else if (strcmpi(argv[3], "ext") == 0)
            {
                _makepath(path, drive, dir, fname, argv[4]);
                printf(path);
            }
            else
            {
                usage();
                return 1;
            }
        }
        else if (strcmpi(argv[2], "part") == 0)
        {
            /* Tricky: "part" => lower case the name
                       "PART" => upper case the name
                       "Part" => leave alone
             */
            if (strcmp(argv[2], "part") == 0)
                _strlwr(path);
            else if (strcmp(argv[2], "PART") == 0)
                _strupr(path);

            _splitpath(path, drive, dir, fname, ext);

            if (strcmpi(argv[3], "drive") == 0)
            {
                printf(drive);
            }
            else if (strcmpi(argv[3], "dir") == 0)
            {
                printf(dir);
            }
            else if (strcmpi(argv[3], "fname") == 0)
            {
                printf(fname);
            }
            else if (strcmpi(argv[3], "ext") == 0)
            {
                /* Leave off the leading '.' if present */
                if (ext[0] == '.')
                    printf(&ext[1]);
                else
                    printf(ext);
            }
            else
            {
                usage();
                return 1;
            }
        }
        else
        {
            usage();
            return 1;
        }
        if (i > 0)
            printf("\n");
    } while (i > 0);
    return 0;
}
