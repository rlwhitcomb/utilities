#include <string.h>
#include "match.h"

/*******************************************************************************
*	Case Conversion Tables						       *
*******************************************************************************/
static char UpperCaseTable[] =
{
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
    0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 
    0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 
    0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 
     ' ',  '!',  '"',  '#',  '$',  '%',  '&', '\'',
     '(',  ')',  '*',  '+',  ',',  '-',  '.',  '/',
     '0',  '1',  '2',  '3',  '4',  '5',  '6',  '7',
     '8',  '9',  ':',  ';',  '<',  '=',  '>',  '?',
     '@',  'A',  'B',  'C',  'D',  'E',  'F',  'G',
     'H',  'I',  'J',  'K',  'L',  'M',  'N',  'O',
     'P',  'Q',  'R',  'S',  'T',  'U',  'V',  'W',
     'X',  'Y',  'Z',  '[', '\\',  ']',  '^',  '_',
     '`',  'A',  'B',  'C',  'D',  'E',  'F',  'G',
     'H',  'I',  'J',  'K',  'L',  'M',  'N',  'O',
     'P',  'Q',  'R',  'S',  'T',  'U',  'V',  'W',
     'X',  'Y',  'Z',  '{',  '|',  '}',  '~',  '',
     'Ä',  'ö',  'ê',  'A',  'A',  'A',  'è',  'Ä',
     'E',  'E',  'E',  'I',  'I',  'I',  'é',  'è',
     'ê',  'í',  'í',  'O',  'ô',  'O',  'U',  'U',
     'Y',  'ô',  'ö',  'õ',  'ú',  'ù',  'û',  'ü',
     'A',  'I',  'O',  'U',  '•',  '•',  '¶',  'ß',
     '®',  '©',  '™',  '´',  '¨',  '≠',  'Æ',  'Ø',
     '∞',  '±',  '≤',  '≥',  '¥',  'µ',  '∂',  '∑',
     '∏',  'π',  '∫',  'ª',  'º',  'Ω',  'æ',  'ø',
     '¿',  '¡',  '¬',  '√',  'ƒ',  '≈',  '∆',  '«',
     '»',  '…',  ' ',  'À',  'Ã',  'Õ',  'Œ',  'œ',
     '–',  '—',  '“',  '”',  '‘',  '’',  '÷',  '◊',
     'ÿ',  'Ÿ',  '⁄',  '€',  '‹',  '›',  'ﬁ',  'ﬂ',
     '‡',  '·',  '‚',  '„',  '‰',  'Â',  'Ê',  'Á',
     'Ë',  'È',  'Í',  'Î',  'Ï',  'Ì',  'Ó',  'Ô',
     '',  'Ò',  'Ú',  'Û',  'Ù',  'ı',  'ˆ',  '˜',
     '¯',  '˘',  '˙',  '˚',  '¸',  '˝',  '˛',  'ˇ',
};

static char LowerCaseTable[] =
{
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
    0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
    0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f,
     ' ',  '!',  '"',  '#',  '$',  '%',  '&', '\'',
     '(',  ')',  '*',  '+',  ',',  '-',  '.',  '/',
     '0',  '1',  '2',  '3',  '4',  '5',  '6',  '7',
     '8',  '9',  ':',  ';',  '<',  '=',  '>',  '?',
     '@',  'a',  'b',  'c',  'd',  'e',  'f',  'g',
     'h',  'i',  'j',  'k',  'l',  'm',  'n',  'o',
     'p',  'q',  'r',  's',  't',  'u',  'v',  'w',
     'x',  'y',  'z',  '[', '\\',  ']',  '^',  '_',
     '`',  'a',  'b',  'c',  'd',  'e',  'f',  'g',
     'h',  'i',  'j',  'k',  'l',  'm',  'n',  'o',
     'p',  'q',  'r',  's',  't',  'u',  'v',  'w',
     'x',  'y',  'z',  '{',  '|',  '}',  '~',  '',
     'á',  'Å',  'Ç',  'É',  'Ñ',  'Ö',  'Ü',  'á',
     'à',  'â',  'ä',  'ã',  'å',  'ç',  'Ñ',  'Ü',
     'Ç',  'ë',  'ë',  'ì',  'î',  'ï',  'ñ',  'ó',
     'ò',  'î',  'Å',  'õ',  'ú',  'ù',  'û',  'ü',
     '†',  '°',  '¢',  '£',  '§',  '§',  '¶',  'ß',
     '®',  '©',  '™',  '´',  '¨',  '≠',  'Æ',  'Ø',
     '∞',  '±',  '≤',  '≥',  '¥',  'µ',  '∂',  '∑',
     '∏',  'π',  '∫',  'ª',  'º',  'Ω',  'æ',  'ø',
     '¿',  '¡',  '¬',  '√',  'ƒ',  '≈',  '∆',  '«',
     '»',  '…',  ' ',  'À',  'Ã',  'Õ',  'Œ',  'œ',
     '–',  '—',  '“',  '”',  '‘',  '’',  '÷',  '◊',
     'ÿ',  'Ÿ',  '⁄',  '€',  '‹',  '›',  'ﬁ',  'ﬂ',
     '‡',  '·',  '‚',  '„',  '‰',  'Â',  'Ê',  'Á',
     'Ë',  'È',  'Í',  'Î',  'Ï',  'Ì',  'Ó',  'Ô',
     '',  'Ò',  'Ú',  'Û',  'Ù',  'ı',  'ˆ',  '˜',
     '¯',  '˘',  '˙',  '˚',  '¸',  '˝',  '˛',  'ˇ',
};


/*******************************************************************************
*       Case conversion routines (using these tables)                          *
*******************************************************************************/
char char_upper(char in)
{
    return UpperCaseTable[in];
}

char char_lower(char in)
{
    return LowerCaseTable[in];
}


/*******************************************************************************
*       Change case of entire string (using the above tables)                  *
*******************************************************************************/
char *string_upper(char *input)
{
    char *ptr, ch;

    for (ptr = input; (ch = *ptr) != EOS; )
        *ptr++ = UpperCaseTable[ch];
    return input;
}

char *string_lower(char *input)
{
    char *ptr, ch;

    for (ptr = input; (ch = *ptr) != EOS; )
        *ptr++ = LowerCaseTable[ch];
    return input;
}


/*******************************************************************************
*       Check to see if pattern is all wild (i.e. "*" or "????"                *
*******************************************************************************/
static int allwild(char *pattern)
{
    char *p;

    if (pattern == NULL)
	return FALSE;

    for (p = pattern; *p; p++)
    {
        if (*p != '*' && *p != '?')
	    return FALSE;
    }
    return TRUE;
}


int char_match(char in, char pat, int case_sensitive)
{
    if (pat == '?')
	return TRUE;

    if (in == pat)
	return TRUE;

    if (!case_sensitive)
    {
	if (UpperCaseTable[in] == UpperCaseTable[pat] ||
	    LowerCaseTable[in] == LowerCaseTable[pat])
	    return TRUE;
    }

    return FALSE;
}


int string_compare(char *input, char *pattern, int case_sensitive)
{
    char ch, pat;

    /* If case matters, then strings must match exactly */
    if (case_sensitive)
        return strcmp(input, pattern);

    for ( ; (ch = *input) != EOS; input++, pattern++)
    {
        if ((pat = *pattern) == EOS)
            break;

        if (UpperCaseTable[ch] != UpperCaseTable[pat] &&
            LowerCaseTable[ch] != LowerCaseTable[pat])
            return (int)ch - (int)pat;
    }
    /* We could have a premature end of input (trailing pattern) */
    return (int)(ch - *pattern);
}


int string_match(char *input, char *pattern, int case_sensitive)
{
    char *inptr, *i, *p;
    int wild;

    /* Check default empty string situations */

    if (pattern == NULL || *pattern == EOS)
    {
	if (input == NULL || *input == EOS)
	    return TRUE;
	else
	    return FALSE;
    }

    wild = allwild(pattern);

    /* Null input string only matches wildcard pattern */

    if (input == NULL || *input == EOS || wild)
    {
	return wild;
    }

    /* Wild cards: ? matches any single character    */
    /*		   * matches zero or more characters */

    for (p = pattern, inptr = input; *p; p++)
    {
	/* Exact or wild-card single character match */

	if (char_match(*inptr, *p, case_sensitive))
	{
	    inptr++;
	}

	/* Wild-card run match */

        else if (*p == '*')
	{
            while (*p == '*')
		p++;

            /* If no more pattern after "*" then we match regardless of input */

	    if (*p == EOS)
		return TRUE;

	    /* Look for following pattern character in input:	 */
	    /* if not found, not a match, but if found, continue */
	    /* matching from there				 */

	    for (i = inptr; *i; i++)
	    {
		if (char_match(*i, *p, case_sensitive))
		{
		    if (string_match(i+1, p+1, case_sensitive))
			return TRUE;
		}
	    }
	    return FALSE;
	}

	/* Else not a match, so stop here */

	else
	    return FALSE;
    }

    /* Here we ran out of pattern, but did we run out of input? */

    if (*inptr == EOS)
	return TRUE;

    return FALSE;
}


