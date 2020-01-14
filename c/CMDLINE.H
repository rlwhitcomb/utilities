/*******************************************************************************
*	Include file for use with MULTI.C				       *
*******************************************************************************/

/* Global variables defined here and set by caller */

extern int quiet;	    /* Print error messages when file not found?      */
extern int ignore_error;    /* Ignore errors returned from process()?	      */
extern int hidden;	    /* Process hidden files as well as normal?	      */
extern int sysfile;	    /* Process system files as well as normal?	      */
extern int directs;	    /* Process directories along with files?	      */
extern int subdir;	    /* Process file specs in subdirectories also?     */

extern int dononwild;       /* Process non-wild card names even if not found  */
extern int dirlist;         /* Process input (esp. @file) as output from DIR  */

extern char progname[];     /* Filled in from argv[0]			      */

#define MAXLEVEL    (50)    /* Stack size = 0x6000 */


/* Attribute values for attrib argument to process (see DOS.H) */

#define A_NORMAL        0x00    /* Normal file - No read/write restrictions   */
#define A_RDONLY        0x01    /* Read only file                             */
#define A_HIDDEN	0x02	/* Hidden file				      */
#define A_SYSTEM	0x04	/* System file				      */
#define A_VOLID		0x08	/* Volume ID file			      */
#define A_SUBDIR	0x10	/* Subdirectory				      */
#define A_ARCH		0x20	/* Archive file				      */

/* Flag values for processprogress() */

#define PROGRESS_RECURSE_INTO   0
#define PROGRESS_RECURSE_OUT    1
#define PROGRESS_WILD_START     2
#define PROGRESS_WILD_FINISH    3

/* Caller's processing routines */

extern int processprogress(int flag, int level, char *arg);
extern int process(char *path, unsigned attrib, time_t date, long size);
extern int processuserarg(char *arg, unsigned flags);


/* Main command line processing routine */

extern int processcmdline(int argc, char *argv[], unsigned flags);

#define PROCESS_OPTIONS     0x0001
#define PROCESS_FILES       0x0002

/* Helper functions (called from processcmdline but also callable separately) */

char *stpblk(char *s);
char *stpwrd(char *s);
char *stppath(char *s);
void extractprogramname(char *arg);
int  processarg(char *arg, unsigned flags);
int  processfilelist(char *arg, int level, unsigned flags);


/* End of file CMDLINE.H */
