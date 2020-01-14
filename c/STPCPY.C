char *stpcpy(char *t, char *s)
{
    while (*s)
        *t++ = *s++;
    *t = '\0';
    return t;
}



