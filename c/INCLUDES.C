/*******************************************************************************
*       Process .C, .H, etc. kinds of files looking for #include directives    *
*******************************************************************************/

#include <stdio.h>
#include <io.h>
#include <stdlib.h>
#include <string.h>
#include "cmdline.h"
#include "util.h"

typedef int BOOL;

static int      verbose     = 0;
static BOOL     ignore_std  = FALSE;
static char    *IncludePath = NULL;
static unsigned IncludeLen  = 0;
static char     src_path[_MAX_PATH] = "";
static char     def_ext[]   = ".OBJ";
static char     def_path[_MAX_PATH] = "";
static char     compile_method[_MAX_PATH] = "compile";

/* List of included files already seen */
typedef struct included_file_tree {
    struct included_file_tree *next;
    struct included_file_tree *parent;
    struct included_file_tree *child;
    struct included_file_tree *sibling;
    char   file_name[1];
} TREE;

TREE *file_list = NULL;


/* Macro substitutions for input or output directories */
typedef struct macro_list {
    struct macro_list *next;
    char *name;
    char *value;
} MACRO_LIST;

MACRO_LIST *pMacroList = NULL;

#define NMAKE   0
#define AMK     1

int macro_format = NMAKE;

static int ProcessFile(TREE *main, char *path, int level, TREE *list);
static void PrintEntry(char *file, TREE *list, int level, BOOL bIndent);
static void PrintTree(char *file, TREE *tree, int level, BOOL bIndent);


/*******************************************************************************
*       Minimal help for program                                               *
*******************************************************************************/
void instruct(void)
{
    fprintf(stderr, "Usage: %s [-I<path_list>][-V[+|*|!]][-E][-s<ext>][-S<path>][-M<macro=value>][-Fn][-P<name>] <files>\n", progname);
    fprintf(stderr, "\t-I adds further places to look for included files\n");
    fprintf(stderr, "\t   (uses INCLUDE= environment variable by default)\n");
    fprintf(stderr, "\t-V reports more and more detail about what's happening\n");
    fprintf(stderr, "\t-E ignores #include <> (i.e. in standard places)\n");
    fprintf(stderr, "\t-s specifies an extension for the output file other than .OBJ\n");
    fprintf(stderr, "\t-S specifies an alternate path for the output file\n");
    fprintf(stderr, "\t-M specifies a macro substitution for an input or output path\n");
    fprintf(stderr, "\t-Fn specifies the format for macros (0=NMAKE (default),1=AMK)\n");
    fprintf(stderr, "\t-L is ignored\n");
    fprintf(stderr, "\t-C is ignored\n");
    fprintf(stderr, "\t-P specifies AMK compile macro other than '%s'\n", compile_method);
}

/*******************************************************************************
*       Search for included file in list                                       *
*******************************************************************************/
static TREE *SearchFile(char *name)
{
    char drive[_MAX_DRIVE], dir[_MAX_PATH], ext[_MAX_EXT];
    char file_name[_MAX_FNAME+_MAX_EXT+1];
    TREE *list;

    for (list = file_list; list != NULL; list = list->next)
    {
        _splitpath(list->file_name, drive, dir, file_name, ext);
        strcat(file_name, ext);
        if (strcmpi(file_name, name) == 0)
            break;
    }
    if (verbose > 1)
    {
        printf("%s: SearchFile(\"%s\") => \"%s\"\n",
                        progname, name, (list?list->file_name:"<NULL>"));
    }
    return list;
}


/*******************************************************************************
*       Add included file to list                                              *
*******************************************************************************/
static TREE *AddFile(char *name, TREE *parent, BOOL bList)
{
    TREE *file, *child, *list;

    if (verbose > 1)
    {
        printf("%s: AddFile(\"%s\",\"%s\",%s)\n",
                        progname, name, (parent?parent->file_name:"<NULL>"),
                        (bList?"TRUE":"FALSE"));
    }

    /* Add to front of list */

    file = (TREE *)malloc(sizeof(TREE) + strlen(name));
    if (file != NULL)
    {
        strcpy(file->file_name, name);
        file->child = file->sibling = file->parent = NULL;

        /* If we already found this guy in the list, don't add him again */
        if (bList)
        {
            file->next = file_list;
            file_list = file;
            if (verbose > 2)
            {
                printf("%s: current file list:\n", progname);
                for (list = file_list; list; list = list->next)
                {
                    printf("\t%s\n", list->file_name);
                }
            }
        }
        else
        {
            file->next = NULL;
        }
    }

    /* Add to parent tree (if any) */

    if (parent != NULL)
    {
        if (parent->child == NULL)
        {
            if (verbose > 1)
            {
                printf("%s: AddFile => first child of \"%s\"\n", progname, parent->file_name);
            }
            parent->child = file;
        }
        else
        {
            for (child = parent->child; child->sibling != NULL; child = child->sibling)
            {
                ;
            }
            child->sibling = file;
            if (verbose > 1)
            {
                printf("%s: AddFile => sibling of \"%s\"\n", progname, child->file_name);
            }
        }
        file->parent = parent;
        if (verbose > 2)
        {
            printf("%s: current tree for %s:\n", progname, parent->file_name);
            PrintTree("\t", parent->child, 1, TRUE);
        }
    }
    return file;
}


/*******************************************************************************
*       Search an entire tree for name                                         *
*******************************************************************************/
TREE *SearchTree(TREE *tree, char *name)
{
    TREE *match;

    while (tree)
    {
        if (strcmpi(tree->file_name, name) == 0)
        {
            if (verbose > 1)
            {
                printf("%s: SearchTree found \"%s\"\n", progname, name);
            }
            break;
        }
        if ((match = SearchTree(tree->child, name)) != NULL)
        {
            return match;
        }
        tree = tree->sibling;
    }
    return tree;
}


/*******************************************************************************
*       Search up parent chain for name                                        *
*******************************************************************************/
TREE *SearchParents(TREE *list, char *name)
{
    TREE *parent;

    for (parent = list; parent; parent = parent->parent)
    {
        if (strcmpi(parent->file_name, name) == 0)
        {
            break;
        }
    }
    return parent;
}


/*******************************************************************************
*       Copy an entire tree (root and below)                                   *
*******************************************************************************/
void CopyTree(TREE *tree, TREE *parent, TREE *main)
{
    TREE *copy, *child;

    if (!SearchTree(main, tree->file_name))
    {
        copy = AddFile(tree->file_name, parent, FALSE);
    }
    else
    {
        copy = parent;
    }
    for (child = tree->child; child; child = child->sibling)
    {
        CopyTree(child, copy, main);
    }
}


/*******************************************************************************
*       Print one file name according to format (quote if necessary)           *
*******************************************************************************/
static void PrintName(char *format, char *name)
{
    char path[_MAX_PATH+3], newpath[_MAX_PATH];
    char drive[_MAX_DRIVE], dir[_MAX_DIR], fname[_MAX_FNAME], ext[_MAX_EXT];
    MACRO_LIST *pMacro;

    /* Handle macro substitutions in path part of file name */
    if (pMacroList)
    {
        _splitpath(name, drive, dir, fname, ext);
        for (pMacro = pMacroList; pMacro; pMacro = pMacro->next)
        {
            /* Note: this syntax is backwards:
               i.e. /MOBJPATH=..\debug\tmp\
               would set name=OBJPATH
               and value=..\debug\tmp\
               so we need to compare on value and substitute name */

            if (strcmpi(dir, pMacro->value) == 0)
            {
                if (*pMacro->name != '\0')
                {
                    switch (macro_format)
                    {
                        case AMK:
                            sprintf(dir, "(%s)", pMacro->name);
                            break;
                        case NMAKE:
                        default:
                            sprintf(dir, "$(%s)", pMacro->name);
                            break;
                    }
                }
                else
                {
                    *dir = '\0';
                }
                _makepath(newpath, drive, dir, fname, ext);
                name = newpath;
                break;
            }
        }
    }

    /* Must quote the file name if it contains spaces */
    if (strchr(name, ' ') != NULL)
    {
        sprintf(path, "\"%s\"", name);
        name = path;
    }
    printf(format, name);
}


/*******************************************************************************
*       Print one entry of tree of included files                              *
*******************************************************************************/
static void PrintEntry(char *file, TREE *list, int level, BOOL bIndent)
{
    int i;

    switch (macro_format)
    {
        case NMAKE:
        default:
            PrintName("%s: ", file);
            break;
        case AMK:
            printf("\t");
            break;
    }
    if (bIndent)
    {
        for (i = 0; i < level; i++)
            printf(" ");
    }
    PrintName("%s\n", list->file_name);
}


/*******************************************************************************
*       Print tree of included files                                           *
*******************************************************************************/
static void PrintTree(char *file, TREE *tree, int level, BOOL bIndent)
{
    while (tree)
    {
        PrintEntry(file, tree, level, bIndent);
        PrintTree(file, tree->child, level + 1, bIndent);
        tree = tree->sibling;
    }
}


/*******************************************************************************
*       Add a new location to the list of "known" include file locations       *
*******************************************************************************/
static BOOL AddInclude(char *include)
{
    unsigned len;
    char *ptr, *eop;

    if (verbose > 1)
    {
        printf("%s: AddInclude(\"%s\")\n", progname, include);
    }

    if (include != NULL && *include != '\0')
    {
        len = strlen(include) + 1 + IncludeLen;
        if ((ptr = malloc(len)) != NULL)
        {
            if (IncludePath != NULL)
            {
                eop = stpcpy(ptr, IncludePath);
                *eop++ = ';';
            }
            else
            {
                eop = ptr;
            }
            strcpy(eop, include);
            if (IncludePath != NULL)
            {
                free(IncludePath);
            }
            IncludePath = ptr;
            IncludeLen  = len;
            return TRUE;
        }
    }
    return FALSE;
}


/*******************************************************************************
*	Search among known places for included file			       *
*******************************************************************************/
static BOOL SearchInclude(char *name, char *path)
{
    unsigned len;
    char *ptr, *eop;

    if (verbose > 1)
    {
        printf("%s: SearchInclude(\"%s\")\n", progname, name);
    }

    /* Try name as given first */

    if (_access(name, 0) == 0)
    {
        strcpy(path, name);
        if (verbose > 1)
        {
            printf("%s: SearchInclude => \"%s\"\n", progname, path);
        }
        return TRUE;
    }

    /* Try source file path (if any) */

    if (src_path[0])
    {
        _makepath(path, NULL, src_path, name, NULL);
        if (verbose > 2)
        {
            printf("%s: SearchInclude trying \"%s\"\n", progname, path);
        }
        if (_access(path, 0) == 0)
        {
            if (verbose > 1)
            {
                printf("%s: SearchInclude => \"%s\"\n", progname, path);
            }
            return TRUE;
        }
    }

    /* Else begin searching down path */

    for (ptr = IncludePath; ptr && *ptr; ptr += len)
    {
        if ((eop = strchr(ptr, ';')) != NULL)
        {
            *eop = '\0';
        }
        len = strlen(ptr);

        /* Ignore empty path elements (since we already tried name by itself) */

        if (len != 0)
        {
            _makepath(path, NULL, ptr, name, NULL);
            if (verbose > 2)
            {
                printf("%s: SearchInclude trying \"%s\"\n", progname, path);
            }

            if (_access(path, 0) == 0)
            {
                if (verbose > 1)
                {
                    printf("%s: SearchInclude => \"%s\"\n", progname, path);
                }
                if (eop != NULL)
                {
                    *eop = ';';
                }
                return TRUE;
            }
        }

        if (eop != NULL)
        {
            *eop = ';';
            len++;
        }
    }
    return FALSE;
}


/*******************************************************************************
*	Process an include file directive - recursively calls ProcessFile      *
*	on include file to get nested includes				       *
*******************************************************************************/
static BOOL ProcessInclude(TREE *main, char *ptr, int level,
                           char *this_file, unsigned long lineno,
                           TREE *parent)
{
    unsigned len;
    char path[_MAX_PATH], type, *file;
    BOOL ret = FALSE;
    TREE *list = NULL;

    file = main->file_name;

    if (verbose > 1)
    {
        printf("%s: ProcessInclude(\"%s\",%s,%d,\"%s\",%ld,%p,%p)\n",
                        progname, file, ptr, level, this_file, lineno, parent, main);
    }

    type = (*ptr);
    if (type == '<')
    {
        ptr++;
        len = strlen(ptr) - 1;
        if (ptr[len] == '>')
	{
            ptr[len] = '\0';
	}
    }
    else if (type == '"')
    {
        ptr++;
        len = strlen(ptr) - 1;
        if (ptr[len] == '"')
	{
            ptr[len] = '\0';
	}
    }
    else
    {
        /* This might be COBOL, ASM or other strange file type */
        type = ' ';
    }

    /* Ignore standard includes if flag set */

    if (ignore_std && type == '<')
    {
        ret = TRUE;
    }

    /* Then, see if we already have it */

    else if ((list = SearchFile(ptr)) != NULL)
    {
        /* Three case where we will be here:  */
        /* 1. found this file in another source altogether    */
        /*    => copy the tree into current trees             */
        /* 2. found this file in current source in another    */
        /*    branch (i.e. not infinite recursion)            */
        /*    => add to current tree, but not main source     */
        /* 3. found in parent chain (i.e. recursive include)  */
        /*    => just quit here, don't copy anything anywhere */
        if (verbose > 2)
        {
            printf("%s: copying tree to \"%s\" (main=\"%s\"):\n", progname, (parent?parent->file_name:"<NULL>"),(main?main->file_name:"<NULL>"));
            PrintEntry("\t", list, 0, TRUE);
            PrintTree("\t", list->child, 1, TRUE);
        }
        if (!SearchParents(parent, list->file_name))
        {
            if (!SearchTree(main, list->file_name))
            {
                if (parent)
                    CopyTree(list, parent, main);
                CopyTree(list, main, main);
            }
            else
            {
                if (parent)
                    CopyTree(list, parent, parent);
            }
        }
        ret = TRUE;
    }

    /* Next, see if we can find it */

    else if (SearchInclude(ptr, path))
    {
        list = AddFile(path, parent, TRUE);
        ProcessFile(main, path, level + 1, list);
        if (!SearchTree(main, list->file_name))
        {
            CopyTree(list, main, main);
        }
        ret = TRUE;
    }
    else
    {
        fprintf(stderr, "%s: Couldn't access include file \"%s\", file %s(%lu)\n", progname, ptr, this_file, lineno);
    }
    return ret;
}


/*******************************************************************************
*       Process a file looking for "#include" directives.  If found process    *
*	that file (and call ourselves recursively if needed).		       *
*******************************************************************************/
static int ProcessFile(TREE *main, char *path, int level, TREE *parent)
{
    FILE *fp;
    char line[512+1], *ptr, *eow, c;
    unsigned len;
    unsigned num = 0;
    unsigned long lineno = 0L;
    BOOL includes = FALSE;

    if (verbose)
    {
        printf("%s: ProcessFile(\"%s\",%d)\n", progname, path, level);
    }

    if ((fp = fopen(path, "r")) != NULL)
    {
        while (!feof(fp) && !ferror(fp))
        {
            if (fgets(line, sizeof(line), fp) != NULL)
            {
                lineno++;
                len = strlen(line) - 1;
                if (line[len] == '\n')
                {
                    line[len] = '\0';
                }
                if (*(ptr = stpblk(line)) != '\0')
                {
                    /********************************************************
                    *   This processes C/C++ style include directives only! *
                    ********************************************************/
                    if (*ptr == '#')
                    {
                        ptr++;
			ptr = stpblk(ptr);

			switch (*ptr)
                        {
                            case 'i':
                            case 'I':
                                /* if or include */
                                switch (ptr[1])
                                {
                                    case 'n':
                                    case 'N':
                                        /* Definitely include */
                                        while (((c = *ptr) != '\0') &&
                                               (c != ' ' && c != '\t' && c != '\n') &&
                                               (c != '<' && c != '"'))
                                        {
                                            ptr++;
                                        }
                                        ptr = stpblk(ptr);
                                        /* null terminate the filename word */
                                        eow = ptr + 1;
                                        while (((c = *eow) != '\0') &&
                                               (c != '>' && c != '"'))
                                        {
                                            eow++;
                                        }
                                        if (c == '>' || c == '"')
                                        {
                                            eow++;
                                        }
                                        *eow = '\0';
					if (!includes && num == 0 && level == 0)
					{
					    includes = TRUE;
					}
                                        if (ProcessInclude(main, ptr, level, path, lineno, parent))
					{
					    num++;
					}
                                }
                                break;
                            case 'e':
                            case 'E':
				/* else, elseif or endif */
                                break;
                            case 'd':
                            case 'D':
                                /* define */
                                break;
                        }
                    }
                    /********************************************************
                    *   Here we try to process ASM style INCLUDE pseudo-op! *
                    ********************************************************/
                    else if (_strnicmp(ptr, "INCLUDE", 7) == 0)
                    {
                        /* we must have some trailing blanks followed by   */
                        /* a non-blank "word" for this to be syntactically */
                        /* correct                                         */
                        ptr += 7;
                        if (*ptr == ' ' || *ptr == '\t')
                        {
                            /* skip the include word and all the blanks afterward */

                            ptr = stpblk(ptr + 1);
                            if (*ptr != '\0')
                            {
                                eow = ptr + 1;
                                while ((c = *eow) != '\0' && c != ' ' && c != '\t')
                                {
                                    eow++;
                                }
                                *eow = '\0';
                                /* Make sure of no trailing words (unless   */
                                /* preceeded by ; (comment indicator)       */
                                /* Note: this is Microsoft MASM format only */

                                if ((c = *stpblk(eow + 1)) == '\0' || (c == ';'))
                                {
                                    if (!includes && num == 0 && level == 0)
                                    {
                                        includes = TRUE;
                                    }
                                    if (ProcessInclude(main, ptr, level, path, lineno, parent))
                                    {
                                        num++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        fclose(fp);
        return (int)num;
    }
    return -1;
}


int processprogress(int flag, int level, char *arg)
{
    return 0;
}


int process(char *path, unsigned attrib, time_t date, long size)
{
    char drive[_MAX_DRIVE], dir[_MAX_PATH], file[_MAX_FNAME], ext[_MAX_EXT];
    char outpath[_MAX_PATH];
    TREE *main;
    int num = 0;

    if ((main = AddFile(path, NULL, FALSE)) != NULL)
    {
        /* Construct output file name: outdir + file + outext */
        _splitpath(path, drive, dir, file, ext);
        sprintf(outpath, "%s%s%s", def_path, file, def_ext);

        /* Output main dependency: outpath: path */
        switch (macro_format)
        {
            case NMAKE:
            default:
                PrintName("%s: ", outpath);
                PrintName("%s\n", path);
                break;
            case AMK:
                PrintName("#make\t%s\n", outpath);
                PrintName("#with\t%s\n", path);
                break;
        }

        /* Construct current source file path */
        _makepath(src_path, drive, dir, NULL, NULL);

        if ((num = ProcessFile(main, path, 0, NULL)) > 0)
        {
            PrintTree(outpath, main->child, 0, (verbose > 1));
        }

        if (macro_format == AMK)
        {
            printf("#by %s %s\n#endmake\n", compile_method, file);
        }

        printf("\n");
    }
    return num;
}


int processuserarg(char *arg, unsigned flags)
{
    int ilen;
    char *ptr;

    switch (*arg)
    {
        /* Define symbol for conditional compilation */
        case 'd':
        case 'D':
            return 1;

        /* Specify extension for main output file (default .OBJ) */
        case 's':
            if (arg[1] == '.')
                strcpy(def_ext, &arg[1]);
            else
                strcpy(&def_ext[1], &arg[1]);
            return 1;

        /* Path for main output file */
        case 'S':
            strcpy(def_path, &arg[1]);
            if (*stpblk(def_path))
            {
                ilen = strlen(def_path) - 1;
                if (def_path[ilen] != '\\' && def_path[ilen] != ':')
                {
                    strcat(def_path, "\\");
                }
            }
            return 1;

        /* These are added for compatibility with WB Includes utility */
        /* but aren't currently implemented...                        */
        case 'l':
        case 'L':
        /* specify alternate input file extensions */
        case 'c':
        case 'C':
            return 1;

        /* Add include paths */
        case 'i':
        case 'I':
            AddInclude(&arg[1]);
            return 1;

        case 'v':
        case 'V':
            verbose = 1;
            if (arg[1] == '+')
                verbose = 2;
            else if (arg[1] == '*')
                verbose = 3;
            else if (arg[1] == '!')
                verbose = 4;
            return 1;

        case 'e':       /* Don't ask why 'e' */
        case 'E':
            ignore_std = TRUE;
            return 1;

        case 'm':
        case 'M':
            if ((ptr = strchr(arg, '=')) == NULL)
            {
                /* Must specify a replacement value */
                return -1;
            }
            else if (*stpblk(ptr+1) == '\0')
            {
                /* Must specify a non-empty replacement value */
                return -1;
            }
            else
            {
                MACRO_LIST *pMacro = malloc(sizeof(MACRO_LIST));
                if (pMacro)
                {
                    *ptr = '\0';
                    memset(pMacro, 0, sizeof(MACRO_LIST));
                    pMacro->name = malloc(strlen(&arg[1]) + 1);
                    if (pMacro->name)
                    {
                        strcpy(pMacro->name, &arg[1]);
                        pMacro->value = malloc(strlen(ptr + 1) + 1);
                        if (pMacro->value)
                        {
                            strcpy(pMacro->value, ptr + 1);
                            pMacro->next = pMacroList;
                            pMacroList = pMacro;
                        }
                        else
                        {
                            free(pMacro->name);
                            free(pMacro);
                            fprintf(stderr, "Not enough memory to save macro value\n");
                            return -1;
                        }
                    }
                    else
                    {
                        free(pMacro);
                        fprintf(stderr, "Not enough memory to save macro value\n");
                        return -1;      /* not enough memory (unlikely) */
                    }
                }
            }
            return 1;

        case 'f':
        case 'F':
            if (arg[1] >= '0' && arg[1] <= '9')
            {
                macro_format = atoi(&arg[1]);
                if (macro_format < NMAKE || macro_format > AMK)
                    return -1;
            }
            else
                return -1;
            return 1;

        case 'p':
        case 'P':
            strcpy(compile_method, &arg[1]);
            return 1;

        case '?':
            return -1;  /* will cause failure and call to instruct() above! */
    }
    return 0;
}


int main(int argc, char **argv)
{
    /* Process all command line switches first */
    if (processcmdline(argc, argv, PROCESS_OPTIONS) < 0 || argc < 2)
    {
        instruct();
        return 1;
    }

    /* Allow switch processing to override INCLUDE environment variable */
    AddInclude(getenv("INCLUDE"));

    /* Now process all the files */
    processcmdline(argc, argv, PROCESS_FILES);

    return 0;
}


