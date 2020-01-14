#ifndef TRUE
#define FALSE	0
#define TRUE	!FALSE
#endif
#ifndef NULL
#define NULL	0L
#endif
#ifndef EOS
#define EOS     '\0'
#endif

int string_match(char *input, char *pattern, int case_sensitive);

