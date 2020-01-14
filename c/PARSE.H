/*******************************************************************************
*       Date/Time Parsing Functions Header                                     *
*******************************************************************************/
/* Must include "time.h" first */

int parse_date(char *ptr, struct tm *date, char **endptr);
int parse_time(char *ptr, struct tm *tm, char **endptr);

