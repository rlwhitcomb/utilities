#include <io.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h>


static char *szPgmName    = "Which";
static char *szExtensions = ".COM;.EXE;.BAT";
static char *szEnvVar     = "Path";

static char **path_list   = NULL;
static char **ext_list    = NULL;


void instruct()
{
    printf("Usage: %s [-E<EnvVar>] <FileSpec(s)>\n", szPgmName);
}


char *alloc_copy(char *str, int len)
{
    char *ptr;

    if (str && *str)
    {
        if (len == -1)
        {
            len = strlen(str) + 1;
        }

        if ((ptr = malloc(len)) != NULL)
        {
            strncpy(ptr, str, len);
            ptr[len-1] = '\0';
            return ptr;
        }
    }
    return "";  /* to avoid NULL pointer traps */
}


int count_path_list(char *list)
{
    char *ptr, *semi;
    int  i;

    for (ptr = list, i = 0; *ptr; i++)
    {
        if ((semi = strchr(ptr, ';')) != NULL)
        {
            ptr = semi + 1;
        }
        else
        {
            break;
        }
    }
    return i + 1;
}


char *split_path_list(char *list, int index)
{
    char *ptr, *semi;
    int  i;

    for (ptr = list, i = 0; i < index; i++)
    {
        if ((semi = strchr(ptr, ';')) != NULL)
        {
            ptr = semi + 1;
        }
        else
        {
            return NULL;
        }
    }
    if ((semi = strchr(ptr, ';')) != NULL)
    {
        return alloc_copy(ptr, (semi - ptr) + 1);
    }
    return alloc_copy(ptr, -1);
}


char **split_list(char *list)
{
    int i = count_path_list(list);
    int j;
    char **array = malloc((i + 1) * sizeof(char *));

    if (array != NULL)
    {
        for (j = 0; j < i; j++)
        {
            array[j] = split_path_list(list, j);
        }
        array[j] = NULL;    /* end of list */
    }
    return array;
}


int search(char *fullpath, char *path, char *name)
{
    long   fh;
    char   **ext;
    struct _finddata_t find;

    /* If name has extension, just try it as is */

    if (strchr(name, '.') != NULL)
    {
        _makepath(fullpath, NULL, path, name, NULL);
        if ((fh = _findfirst(fullpath, &find)) != -1L)
        {
            _findclose(fh);
            return 1;
        }
    }
    else
    {
        /* Else try all the extensions in order */

        for (ext = ext_list; *ext; ext++)
        {
            _makepath(fullpath, NULL, path, name, *ext);
            if ((fh = _findfirst(fullpath, &find)) != -1L)
            {
                _findclose(fh);
                return 1;
            }
        }
    }
    return 0;
}


int search_list(char *fullpath, char *name)
{
    char **list;

    /* First, just try current directory (no path) */

    if (search(fullpath, NULL, name))
    {
        return 1;
    }

    /* Then try down the path */

    for (list = path_list; *list; list++)
    {
        if (search(fullpath, *list, name))
        {
            return 1;
        }
    }
    return 0;
}


int main(int argc, char **argv)
{
    int i;
    int num_files = 0;
    char *ext, *path, fullpath[_MAX_PATH];

    /* Check command line switches first, and check number of file specs */

    for (i = 1; i < argc; i++)
    {
        if (argv[i][0] == '-' || argv[i][0] == '/')
        {
            switch (argv[i][1])
            {
                case 'e':
                case 'E':
                    szEnvVar = alloc_copy(&argv[i][2], -1);
                    break;

                case '?':
                    instruct();
                    return 0;

                default:
                    printf("%s: invalid option '%s'\n", szPgmName, argv[i]);
                    instruct();
                    return 1;
            }
        }
        else
        {
            /* If it is not an option switch, it must be a file name */

            num_files++;
        }
    }

    /* If no file specs given, just instruct */

    if (num_files == 0)
    {
        instruct();
        return 0;
    }

    /* Check for the PATHEXT environment variable (only on NT)  */
    /* If found, use this instead of default list of extensions */

    if ((ext = getenv("PATHEXT")) != NULL)
    {
        szExtensions = alloc_copy(ext, -1);
    }

    /* Get the environment variable specified (either "Path" or */
    /* whatever the user supplied)                              */

    if ((path = getenv(szEnvVar)) == NULL)
    {
        /* If we can't find the environment variable, no point in going on */

        printf("%s: Can't find the environment variable '%s'\n", szPgmName, szEnvVar);
        instruct();
        return 1;
    }

    /* Split up the path and the extension list into parts */

    path_list = split_list(path);
    ext_list  = split_list(szExtensions);

    /* Now check the name(s) supplied on the command line */

    for (i = 1; i < argc; i++)
    {
        if (argv[i][0] != '-' && argv[i][0] != '/')
        {
            if (search_list(fullpath, argv[i]))
            {
                printf("%s\n", fullpath);
            }
        }
    }

    return 0;
}
