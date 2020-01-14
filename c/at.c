#include <stdio.h>
#include <malloc.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <signal.h>
#include <setjmp.h>
#include <float.h>
#include <limits.h>
#include "util.h"

#ifdef OS2  /* for now hopefully! */
#define sinl    sin
#define cosl    cos
#define acosl   acos
#define tanl    tan
#define log10l  log10
#define logl    log
#define expl    exp
#define powl    pow
#define sqrtl   sqrt
#endif

/* Newer C has "strtold" not "_strtold" */
/*#define _strtold strtold*/

long double evaluate(char *s, char **end, int level);


enum ids {ID_COS, ID_SIN, ID_TAN, ID_ACOS, ID_ASIN, ID_ATAN, ID_LOG, ID_LN, ID_SQRT};
static struct
{
    char     *name;
    enum ids id;
} functions[] =
{
    { "cos" , ID_COS  },
    { "sin" , ID_SIN  },
    { "tan" , ID_TAN  },
    { "acos", ID_ACOS },
    { "asin", ID_ASIN },
    { "atan", ID_ATAN },
    { "log" , ID_LOG  },
    { "ln"  , ID_LN   },
    { "sqrt", ID_SQRT }
};

static char invalid[] = "Invalid operand %.18Lg for %s function.\n";

static jmp_buf mark;
static int fperr = 0;

void sig_handler(int sig, int num)
{
    if (num != _FPE_INEXACT)
    {
	fperr = num;
	_fpreset();
	longjmp(mark, -1);
    }
}


long convert(long double v)
{
    long lv;

    lv = (long)v;
    if (v > ULONG_MAX || v < LONG_MIN)
    {
        printf("Overflow:  converted %.18Lg to %ld\n", v, lv);
    }
    return lv;
}


/*******************************************************************************
*	Skip over trailing white space					       *
*******************************************************************************/
char *skipwhite(char *s)
{
    while (*s && (*s == ' ' || *s == '\t'))
	s++;
    return s;
}


long double function(char *s, char **end, int *match, int level)
{
    unsigned len;
    int i;
    long double v = 0.0;

    *match = 0;
    for (i = 0; i < sizeof(functions) / sizeof(functions[0]); i++)
    {
        len = strlen(functions[i].name);
        if (strnicmp(s, functions[i].name, len) == 0)
        {
            s += len;
	    v = evaluate(s, end, level + 1);
            switch (functions[i].id)
            {
                case ID_COS:
                    v = cosl(v);
                    break;

                case ID_SIN:
                    v = sinl(v);
                    break;

                case ID_TAN:
                    v = tanl(v);
                    break;

                case ID_ACOS:
                    if (v < -1.0 || v > +1.0)
                    {
                        printf(invalid, v, functions[i].name);
                        v = 0.0;
                    }
                    else
                    {
                        v = acosl(v);
                    }
                    break;

                case ID_ASIN:
                    if (v < -1.0 || v > +1.0)
                    {
                        printf(invalid, v, functions[i].name);
                        v = 0.0;
                    }
                    else
                    {
                        v = asinl(v);
                    }
                    break;

                case ID_ATAN:
                    v = atanl(v);
                    break;

                case ID_LOG:
                    if (v <= 0.0)
                    {
			printf(invalid, v, functions[i].name);
                        v = 0.0;
                    }
                    else
                    {
                        v = log10l(v);
                    }
                    break;

                case ID_LN:
                    if (v <= 0.0)
                    {
			printf(invalid, v, functions[i].name);
                        v = 0.0;
                    }
                    else
                    {
                        v = logl(v);
                    }
                    break;

                case ID_SQRT:
                    if (v < 0.0)
                    {
			printf(invalid, v, functions[i].name);
                        v = 0.0;
                    }
                    else
                    {
                        v = sqrtl(v);
                    }
                    break;

                default:
                    v = 0.0;
                    break;

            }
            *match = 1;
        }
    }
    return v;
}

long double operand(char *s, char **end, int level)
{
    char *p;
    long double v, v1;
    unsigned long ulv;
    long lv;
    int match, sign;

    s = skipwhite(s);

    if (*s == '(')
    {
	v = evaluate(s+1, &p, level + 1);
        if (*p != ')')
        {
            printf("Expected \")\" before \"%s\".\n", p);
        }
        else
            p++;
    }
    else if (*s == '-' || *s == '+')
    {
        sign = (*s++ == '-');
        while (*s && (*s == '-' || *s == '+'))
	{
            if (*s++ == '-')
		sign = !sign;
	    s = skipwhite(s);
	}
	v = evaluate(s, &p, level + 1);
	if (sign)
	    v = -v;
    }
    else if ((s[0] == 'p' || s[0] == 'P') && (s[1] == 'i' || s[1] == 'I'))
    {
        p = s+2;
        v = acosl(-1.0);
    }
    else if (s[0] == 'e' || s[0] == 'E')
    {
        p = s+1;
        v = expl(1.0);
    }
    else if (s[0] == '0' && (s[1] == 'x' || s[1] == 'X'))
    {
	ulv = strtoul(s, &p, 0);
	v = ulv;
    }
    else if (*s == '~')
    {
	s++;
	sign = 1;
        while (*s && (*s == '~'))
	{
	    s++;
	    sign = !sign;
	    s = skipwhite(s);
	}
	v = evaluate(s, &p, level + 1);
	lv = convert(v);
	if (sign)
	    v = ~lv;
    }
    else
    {
	v = function(s, &p, &match, level);
        if (!match)
        {
            v = _strtold(s, &p);
            /* Must handle embedded thousand's separators */
            while ((*p == ',' || *p == ';') && (p[1] >= '0' && p[1] <= '9'))
	    {
		v = v * 1000 + _strtold(p + 1, &p);
	    }
            /* Must handle time values: hh:mm[:ss[.ms]][am|pm|a|p] */
            /* Convert internally to milliseconds past midnight    */
            if (*p == ':' && (p[1] >= '0' && p[1] <= '9'))
            {
                v = v * 60 + _strtold(p + 1, &p);   /* hours:minutes */
                if (*p == ':' && (p[1] >= '0' && p[1] <= '9'))
                {
                    v = v * 60 + _strtold(p + 1, &p);
                    if (*p == '.' && (p[1] >= '0' && p[1] <= '9'))
                    {
                        v1 = _strtold(p + 1, &p);
                        if (v1 < 10)
                            v1 *= 100;
                        else if (v1 < 100)
                            v1 *= 10;
                        v = v * 1000 + v1;
                    }
                    else
                        v *= 1000;
                }
                else
                {
                    v *= (long double)60 * 1000;
                }
            }
            else if (*p == 'a' || *p == 'A' || *p == 'p' || *p == 'P')
            {
                v *= (long double)60 * 60 * 1000;
            }
            /* Handle am/pm indicator here so 10am is also a valid time */
            if (*p == 'a' || *p == 'A' || *p == 'p' || *p == 'P')
            {
                v1 = (long double)12 * 60 * 60 * 1000;
                if (*p == 'a' || *p == 'A')
                {
                    if (v >= v1)
                        v -= v1;
                }
                else
                {
                    if (v < v1)
                        v += v1;
                }
                p++;
                if (*p == 'm' || *p == 'M')
                    p++;
            }
        }
    }

    p = skipwhite(p);

    while (*p == '!')
    {
	p++;
	lv = convert(v);
	if (lv < 0 || lv != v)
	{
            printf(invalid, v, "!");
	}
	else
	{
	    v = 1.0;
	    while (lv)
	    {
		v *= lv;
		lv--;
	    }
	}
	p = skipwhite(p);
    }

    *end = p;
    return v;
}


long double term(char *s, char **end, int level)
{
    char *p, op;
    long double v, v1, v2;
    long lv1, lv2;

    v1 = operand(s, &p, level);

    for (;;)
    {
        if (*p != '*' && *p != '/' && *p != '%' && *p != '^' &&
            *p != '&' && *p != '#')
        {
            v = v1;
            break;
        }
        else
        {
            op = *p++;

	    v2 = operand(p, &p, level);

            switch (op)
            {
                case '*':
                    v = v1 * v2;
                    break;
                case '/':
                    if (v2 != 0.0)
                        v = v1 / v2;
                    else
                    {
                        printf("Division by zero.\n");
                        v = 0.0;
                    }
                    break;
                case '%':
		    if (v2 != 0.0)
			v = fmodl(v1, v2);
		    else
                    {
                        printf("Division by zero.\n");
                        v = 0.0;
                    }
		    break;
                case '^':
                    v = powl(v1, v2);
                    break;
                case '&':
		    lv1 = convert(v1);
		    lv2 = convert(v2);
		    v = (lv1 & lv2);
		    break;
                case '#':
		    lv1 = convert(v1);
		    lv2 = convert(v2);
		    v = (lv1 ^ lv2);
		    break;
                default:
                    printf("Invalid operator \"%c\".\n", op);
                    v = v1;
                    break;
            }
        }
        /* Prepare for another operator -- set left operand to result so far */
        v1 = v;
    }
    *end = p;
    return v;
}


long double evaluate(char *s, char **end, int level)
{
    char *p, op;
    long double v, v1, v2;
    long lv1, lv2;

    v1 = term(s, &p, level);

    for (;;)
    {
        if (*p == '\0' || *p == ')' || *p == ',' || *p == ';')
        {
            v = v1;
            break;
        }
        else
        {
            op = *p++;

	    v2 = term(p, &p, level);

            switch (op)
            {
                case '+':
                    v = v1 + v2;
                    break;
                case '-':
                    v = v1 - v2;
                    break;
                case '|':
		    lv1 = convert(v1);
		    lv2 = convert(v2);
		    v = (lv1 | lv2);
		    break;
                default:
                    printf("Invalid operator \"%c\".\n", op);
                    v = v1;
                    break;
            }
        }
        /* Prepare for another operator -- set left operand to result so far */
        v1 = v;
    }
    *end = p;
    return v;
}


void results(char *line)
{
    long double v, v1;
    long lv;
    unsigned long ulv;
    long hr, min, sec, millisec;
    char *ptr, ampm, em, *sign;
    int  format, time_fmt;
    char number[200];
    static char *formats[] =
    {
        "%.18Lg",
        "%ld",
        "0x%lX",
        "%s%2ld:%02ld:%02ld.%03ld%c%c",
        "'%c'",
        "'%c%c'",
        "'%c%c%c'",
        "'%c%c%c%c'",
        "%.18Lg KB",
        "%.18Lg MB",
        "%.18Lg GB"
    };
    enum fmts {FMT_DEFAULT, FMT_INTEGER, FMT_HEX, FMT_TIME,
               FMT_CHAR, FMT_CHAR2, FMT_CHAR3, FMT_CHAR4,
               FMT_KBYTE, FMT_MBYTE, FMT_GBYTE};
    enum time_fmts {FMT_12HOUR, FMT_24HOUR, FMT_HOURMIN };

    format = FMT_DEFAULT;
    if (setjmp(mark) == 0)
    {
	v = evaluate(line, &ptr, 0);

	/* Process trailing format specifier */
        if (*ptr == ',' || *ptr == ';')
	{
	    ptr = skipwhite(ptr + 1);
            if (*ptr != '\0')
	    {
		switch (*ptr)
		{
                    case 'x':
                    case 'X':
			ptr++;
			format = FMT_HEX;
			break;
                    case 'i':
                    case 'I':
                    case 'd':
                    case 'D':
                    case 'l':
                    case 'L':
			ptr++;
			format = FMT_INTEGER;
			break;
                    case 'e':
                    case 'E':
                    case 'f':
                    case 'F':
			ptr++;
			break;
                    case 'g':
                    case 'G':
                        ptr++;
                        format = FMT_GBYTE;
                        break;
                    case 'm':
                    case 'M':
                        ptr++;
                        format = FMT_MBYTE;
                        break;
                    case 'k':
                    case 'K':
                        ptr++;
                        format = FMT_KBYTE;
                        break;
                    case 'c':
                    case 'C':
                        ptr++;
                        format = FMT_CHAR;
                        break;
                    case 'h':
                    case 'H':
                        ptr++;
                        format = FMT_TIME;
                        time_fmt = FMT_HOURMIN;
                        break;
                    case 't':
                    case 'T':
                        format = FMT_TIME;
                        time_fmt = (*ptr == 'T')? FMT_24HOUR : FMT_12HOUR;
                        ptr++;
                        break;
                    default:
			/* Will get error message below */
			break;
		}
	    }
	    ptr = skipwhite(ptr);
	}
        if (*ptr != '\0')
        {
            printf("Unexpected characters \"%s\".\n", ptr);
        }
    }
    else
    {
        switch (fperr)
        {
            case _FPE_INVALID:
                printf("Invalid number.\n");
                break;
            case _FPE_OVERFLOW:
                printf("Numeric overflow.\n");
                break;
            case _FPE_UNDERFLOW:
                printf("Numeric underflow.\n");
                break;
            case _FPE_ZERODIVIDE:
                printf("Division by zero.\n");
                break;
	    default:
                printf("Numeric error %x.\n", fperr);
		break;
        }
        v = 0.0;
    }

    switch (format)
    {
        case FMT_GBYTE:
            v /= (long double)1024;     /* fall through */
        case FMT_MBYTE:
            v /= (long double)1024;     /* fall through */
        case FMT_KBYTE:
            v /= (long double)1024;     /* fall through */
        case FMT_DEFAULT:
	    sprintf(number, formats[format], v);
	    reformat_number(number);
	    break;
	case FMT_INTEGER:
	    lv = convert(v);
	    sprintf(number, formats[format], lv);
	    reformat_number(number);
	    break;
	case FMT_HEX:
	    lv = convert(v);
	    sprintf(number, formats[format], lv);
	    break;
        case FMT_TIME:
            if (time_fmt == FMT_12HOUR)
            {
                em = 'm';
                v1 = (long double)12 * 60 * 60 * 1000;
                if (v >= v1)
                {
                    ampm = 'p';
                    v -= v1;
                }
                else
                {
                    ampm = 'a';
                }
                if (v < (long double)1 * 60 * 60 * 1000)
                    v += v1;
            }
            else
            {
                ampm = 0x00;
                em = 0x00;
            }
            if (v >= 0.0)
            {
                sign = "";
            }
            else
            {
                v = -v;
                sign = "-";
            }
            hr = convert(v / ((long double)60 * 60 * 1000));
            v1 = (long double)hr * 60 * 60 * 1000;
            v -= v1;
            min = convert(v / ((long double)60 * 1000));
            v1 = (long double)min * 60 * 1000;
            v -= v1;
            sec = convert(v / 1000);
            v1 = (long double)sec * 1000;
            v -= v1;
            millisec = convert(v);
            sprintf(number, formats[format], sign, hr, min, sec, millisec, ampm, em);
            break;
        case FMT_CHAR:
            ulv = (unsigned long)convert(v);
            /* Could be 1, 2, 3, or 4 characters depending on size */
            if (ulv <= 255)
            {
                sprintf(number, formats[format], ulv);
            }
            else if (ulv <= 65535)
            {
                sprintf(number, formats[FMT_CHAR2], (char)(ulv >> 8),
                                                           ulv & 0xFF);
            }
            else if (ulv <= 16777215)
            {
                sprintf(number, formats[FMT_CHAR3], (char) (ulv >> 16),
                                                    (char)((ulv >>  8) & 0xFF),
                                                            ulv & 0xFF);
            }
            else
            {
                sprintf(number, formats[FMT_CHAR4], (char) (ulv >> 24),
                                                    (char)((ulv >> 16) & 0xFF),
                                                    (char)((ulv >>  8) & 0xFF),
                                                            ulv & 0xFF);
            }
            break;
    }
    printf("%s = %s\n", line, number);

}


void instruct(void)
{
    printf("Command Line Calculator\n");
    printf("