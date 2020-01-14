/*******************************************************************************
*	Prototypes for utility subroutines				       *
*******************************************************************************/

#ifndef TRUE
#define TRUE	(1)
#endif
#ifndef FALSE
#define FALSE	(0)
#endif

void reformat_number(char *buf);
void format_long(long num, char *buf, int unsign);
#if _MSC_VER > 1000
void format_int64(__int64 num, char *buf, int unsign);
#endif /* _MSC_VER > 1000 */

unsigned int number_string(char *buf, unsigned long num, int capitalize);

char *stpcpy(char *t, char *s);

