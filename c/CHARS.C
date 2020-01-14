#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv)
{
    char *args, line[4096], outline[8192], name[128], *out, *in, *env;
    int i, len, linelen, index, index2;

    /* Concatenate all command line args (with spaces between) into one string */
    for (i = 1, len = 0; i < argc; i++)
    {
        len += strlen(argv[i]) + 1; /* +1 is either space or trailing EOS */
    }
    args = _alloca(len);
    *args = 0x00;

    /* Now copy the args into the concatenated string */
    for (i = 1; i < argc; i++)
    {
        if (i > 1)
            strcat(args, " ");
        strcat(args, argv[i]);
    }

    /* For each line of input, format output line according to arg string */
    while (!ferror(stdin) && !feof(stdin))
    {
        if (fgets(line, sizeof(line), stdin) != NULL)
        {
            /* First, strip off EOL sequence from input */
            linelen = strlen(line);
            while (line[linelen-1] == '\r' || line[linelen-1] == '\n')
                linelen--;
            line[linelen] = 0x00;

            out = outline;
            for (in = args; *in; in++)
            {
                switch (*in)
                {
                    /* Embedded format character, process */
                    case '$':
                        in++;
                        switch (*in)
                        {
                            /* double $$ puts single $ into stream */
                            case '$':
                                *out++ = *in;
                                break;

                            /* $nn puts n-th character of input line into stream */
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                for (index = 0; *in >= '0' && *in <= '9'; in++)
                                {
                                    index = index * 10 + (*in - '0');
                                }
                                if (*in == '.')
                                {
                                    in++;
                                    /* We have possible range here */
                                    if (*in >= '0' && *in <= '9')
                                    {
                                        for (index2 = 0; *in >= '0' && *in <= '9'; in++)
                                        {
                                            index2 = index2 * 10 + (*in - '0');
                                        }
                                    }
                                    else
                                        index2 = linelen;

                                    if (index >= 1 && index <= linelen &&
                                        index2 >= 1 && index2 <= linelen &&
                                        index <= index2)
                                    {
                                        len = index2 - index + 1;
                                        memcpy(out, &line[index - 1], len);
                                        out += len;
                                    }
                                }
                                else
                                {
                                    /* Index is 1-based index into input line */
                                    if (index >= 1 && index <= linelen)
                                    {
                                        *out++ = line[index - 1];
                                    }
                                }
                                in--;
                                break;

                            /* $-nn puts n-th character from end of input into stream */
                            case '-':
                                in++;
                                if (*in >= '0' && *in <= '9')
                                {
                                    for (index = 0; *in >= '0' && *in <= '9'; in++)
                                    {
                                        index = index * 10 + (*in - '0');
                                    }
                                    /* Index is 1-based index into input line */
                                    if (index >= 1 && index <= linelen)
                                    {
                                        *out++ = line[linelen - index];
                                    }
                                    in--;
                                }
                                break;

                            /* $* puts entire input line into stream */
                            case '*':
                                memcpy(out, line, linelen);
                                out += linelen;
                                break;

                            /* $(name) puts environment variable into stream */
                            case '(':
                                for (i = 0, in++; *in && *in != ')'; in++)
                                {
                                    if (i < sizeof(name)-1)
                                    {
                                        name[i++] = *in;
                                    }
                                }
                                name[i] = 0x00;

                                /* Now retrieve environment variable */
                                env = getenv(name);
                                if (env != NULL)
                                {
                                    len = strlen(env);
                                    memcpy(out, env, len);
                                    out += len;
                                }
                                break;

                            /* The following put special ASCII escapes into stream */
                            case 'r':
                            case 'R':
                            case 'n':
                            case 'N':
                                *out++ = '\n';
                                break;
                            case 't':
                            case 'T':
                                *out++ = '\t';
                                break;
                        }
                        break;
                    /* Nothing special, just copy format char to output */
                    default:
                        *out++ = *in;
                        break;
                }
            }
            /* Terminate the output line */
            if (out == outline || *(out-1) != '\n')
            {
                *out++ = '\n';
            }
            *out = 0x00;

            /* Finally, output now thoroughly doctored output line */
            fputs(outline, stdout);
        }
    }
    return 0;
}
