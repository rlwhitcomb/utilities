/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016,2020 Roger L. Whitcomb.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *	"General-purpose" (but not full-featured) SQL pretty printer.
 *
 *  History:
 *      17-May-2016 (rlwhitcomb)
 *          Cloned from earlier attempt and moved to general-purpose package.
 *          Tweak for use with View definitions.
 *          Do some refactoring and renaming for clarity; eliminate unused variables.
 *      25-May-2016 (rlwhitcomb)
 *          More work for View, Procedure and Rule formatting.
 *      01-Jun-2016 (rlwhitcomb)
 *          And yet more work for View, Procedure and Rule formatting.
 *      20-Jun-2016 (rlwhitcomb)
 *          More work on Procedure formatting. Rename LexicalState to just State.
 *          Change StringBuffer to StringBuilder since we only use them internally
 *          and we don't need to sync across threads.
 *      21-Jun-2016 (rlwhitcomb)
 *          Finish up our known fail cases for Procedure and Rule formatting.
 *          Leave the debugging code in and add "debug" flag to enable it.
 *      05-Aug-2016 (rlwhitcomb)
 *          More cases of Procedures that fail.  Rename several methods for brevity.
 *          Massive refactoring to use keyword lookup everywhere, which eliminates
 *          a lot of UPPER/lower casing along the way.  Also refactor to use a
 *          common object for the data type queue stuff.
 *	11-Aug-2016 (rlwhitcomb)
 *	    One more glitch for Procedures that have no local declarations:  test
 *	    for BEGIN right after AS.  Put the "DECLARE" or "BEGIN" on a separate
 *	    line.
 *	16-Apr-2020 (rlwhitcomb)
 *	    Cleanup and prepare for GitHub.
 */
package info.rlwhitcomb.util;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Very basic formatter / pretty printer for SQL. It was initially limited to handling the "CREATE TABLE"
 * SQL, but has since been extended (a little bit) to at least partially handle some other cases,
 * such as Views, Procedures and Rules.
 *
 * It is intended that pretty printed SQL is used for display purposes only. The raw generated SQL should be the SQL that
 * is actually executed.
 *
 * We use a two pass approach - the first gathers information that is used to enhance the formatting/alignment of the
 * SQL, while the second does the actual formatting.
 */
public class SQLFormatter
{
    private enum Keyword
    {
        _NOT_A_KEYWORD_,
        AND,
        AFTER,
        AS,
        BEFORE,
        BEGIN,
        CREATE,
        DECLARE,
        DEFAULT,
        DELETE,
        EACH,
        END,
        EXECUTE,
        FIRST,
        FOR,
        FROM,
        HASH,
        INSERT,
        INTO,
        KEY,
        LOCATION,
        NOT,
        NULL,
        ON,
        OF,
        PRIMARY,
        PROCEDURE,
        RESULT,
        RETURN,
        ROW,
        RULE,
        SELECT,
        STATEMENT,
        TABLE,
        UPDATE,
        VIEW,
        WHERE,
        WITH;

        public static Keyword lookup(String upperWord) {
            Keyword kw = reverseLookupMap.get(upperWord);
            if (kw == null)
                kw = Keyword._NOT_A_KEYWORD_;
            return kw;
        }

        private static Map<String, Keyword> reverseLookupMap = new HashMap<>();
        static {
            for (Keyword kw : values()) {
                reverseLookupMap.put(kw.toString(), kw);
            }
        }
    }

    /**
     * An enumeration of the lexical states that the formatter can be in.
     */
    private enum State
    {
        /**
         * Looking at the initial keyword (what type of SQL statement this is).
         */
        INITIAL_KEYWORD,

        /**
         * In the create object keywords.
         */
        CREATE_KEY_WORDS,

        /**
         * In the table/view or other object name.
         */
        OBJECT_NAME,

        /**
         * In the name part of a column declaration.
         */
        COLUMN_DECLARATION_NAME,

        /**
         * The data type part of a column declaration.
         */
        COLUMN_DECLARATION_DATA_TYPE,

        /**
         * We could be still in a data type declaration or the nullability.
         */
        COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY,

        /**
         * The nullability part of column declaration.
         */
        COLUMN_DECLARATION_NULLABILITY,

        /**
         * The default part of a column declaration.
         */
        COLUMN_DECLARATION_DEFAULT,

        /**
         * In the name part of a param declaration.
         */
        PARAM_DECLARATION_NAME,

        /**
         * The data type part of a param declaration.
         */
        PARAM_DECLARATION_DATA_TYPE,

        /**
         * We could be still in a data type declaration or the nullability.
         */
        PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY,

        /**
         * The nullability part of param declaration.
         */
        PARAM_DECLARATION_NULLABILITY,

        /**
         * The default part of a param declaration.
         */
        PARAM_DECLARATION_DEFAULT,

        /**
         * The data type part of a result row declaration.
         */
        RESULT_ROW_DECLARATION_DATA_TYPE,

        /**
         * We could be still in a data type declaration or the nullability.
         */
        RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY,

        /**
         * The nullability part of result row declaration.
         */
        RESULT_ROW_DECLARATION_NULLABILITY,

        /**
         * The default part of a result row declaration.
         */
        RESULT_ROW_DECLARATION_DEFAULT,

        /**
         * The WITH after the column declarations.
         */
        WITH,

        /**
         * After the column declarations.
         */
        ADDITIONAL_OPTIONS,

        /**
         * A primary key declaration.
         */
        PRIMARY_KEY_DECLARATION,

        /**
         * Parsing any "FIRST n" expression for "SELECT".
         */
        FIRST_N,

        /**
         * Parsing column names or expressions for "SELECT".
         */
        COLUMN_NAME_OR_EXPRESSION,

        /**
         * At end of column list, looking for "FROM" (in a "SELECT").
         */
        FROM,

        /**
         * Saw "AS", looking for "SELECT" or "DECLARE" (View/Procedure).
         */
        AS,

        /**
         * Saw "WHERE" during SELECT processing.
         */
        WHERE,

        /**
         * Saw "RESULT" going into "ROW" for a row-producing procedure.
         */
        RESULT,

        /**
         * Working on "AFTER"/"BEFORE" clause of a rule.
         */
        AFTER_BEFORE,

        /**
         * Working on "FOR EACH" clause of a rule.
         */
        FOR_EACH,

        /**
         * Saw "BEGIN" going into procedure body.
         */
        PROCEDURE_BODY,

        /**
         * Working on a "RETURN ROW" statement in a row-producing procedure.
         */
        RETURN_ROW
    }

    /**
     * Class used to buffer tokens in order to calculate maximum lengths.
     */
    private class TypeNameQueue
    {
        Queue<String> typeNameQueue = new LinkedList<>();
        StringBuilder typeNameBuffer = new StringBuilder();
        int maxNameLength = 0;
        int maxTypeNameLength = 0;

        public void bufferTypeToken(String token) {
            if (firstPass) {
                typeNameBuffer.append(token);
            }
        }

        public void clear() {
            typeNameQueue.clear();
        }

        public void resetLength() {
            typeNameBuffer.setLength(0);
        }

        public int size() {
            return typeNameQueue.size();
        }

        public void addToDataType(boolean atEnd) {
            if ((firstDefaultToken || atEnd) && firstPass) {
                String typeName = typeNameBuffer.toString();
                maxTypeNameLength = Math.max(maxTypeNameLength, typeName.length());
                String trimmedName = typeName.trim();
                typeNameQueue.add(trimmedName);
                printDebug("typeNameQueue added \"%1$s\" => queue length = %2$d, maxTypeNameLength = %3$d%n", trimmedName, size(), maxTypeNameLength);
            }
        }

        public void addSpace() {
            if (firstPass) {
                typeNameBuffer.append(" ");
            }
            else {
                output(" ");
            }
        }

        public String getMaxName(String name) {
            if (firstPass) {
                maxNameLength = Math.max(maxNameLength, name.length());
                return name;    // not really used by callers
            }
            else {
                return CharUtil.padToWidth(name, maxNameLength);
            }
        }

        public void appendOutputType(String token) {
            if (firstPass) {
                typeNameBuffer.append(token);
            }
            else {
                // Pop a data type off the queue and ignore the token.
                output(CharUtil.padToWidth(typeNameQueue.remove(), maxTypeNameLength));
            }
        }

    }

    /**
     * The current lexical state.
     */
    private State state;

    /**
     * The initial keyword of this SQL statement, which pretty much
     * determines the format of the rest of the statement.
     */
    private Keyword initialKeyword;

    /**
     * The type of object (for CREATE).
     */
    private Keyword objectType;

    /**
     * The indentation.
     */
    private static final String INDENT = "    ";

    /**
     * The things that we will regard as whitespace.
     */
    private static final String WHITESPACE = " \n\r\f\t";

    /**
     * The current token.
     */
    private String token;

    /**
     * The UPPER CASE equivalent of the current token (for
     * convenience of lookup, comparison, etc.)
     */
    private String upperToken;

    /**
     * The current token, converted to one of our keywords
     * (can be {@code null} if the token is NOT a keyword).
     */
    private Keyword currentKeyword;

    /**
     * Whether the last token was whitespace.
     */
    private boolean lastTokenWasWhitespace;

    /**
     * Whether we are doing the first pass (scanning) or the second pass
     * (constructing the output). Set by {@link #process}.
     */
    private boolean firstPass;

    /**
     * The string buffer holding the pretty printed / formatted SQL.
     */
    private StringBuilder result;

    /**
     * The tokens.
     */
    private StringTokenizer tokens;

    /**
     * The current count of open parentheses.
     */
    private int openParenthesesCount;

    /**
     * The object containing information about the column data types.
     */
    private TypeNameQueue dataTypeQueue;

    /**
     * The information about the parameter types (for procedures).
     */
    private TypeNameQueue paramTypeQueue;

    /**
     * Information about the result row types (again for procedures).
     */
    private TypeNameQueue resultRowTypeQueue;

    /**
     * Whether or not any columns are defined as "NOT NULL".
     */
    private boolean hasNotNull;

    /**
     * Whether or not any columns are defined as "WITH NULL".
     */
    private boolean hasWithNull;

    /**
     * Whether or not any columns are defined as "NOT DEFAULT"
     */
    private boolean hasNotDefault;

    /**
     * Whether or not any columns are defined as "WITH DEFAULT"
     */
    private boolean hasWithDefault;

    /**
     * Whether or not this is the first token in a nullability clause.
     */
    private boolean firstNullabilityToken;

    /**
     * Whether or not this is the first token in a default clause.
     */
    private boolean firstDefaultToken;

    /**
     * Saved flag to say we are parsing inside a procedure body.
     */
    private boolean insideProcBody;

    /**
     * The input sql.
     */
    private String sql;

    /**
     * Any peeked tokens.
     */
    private Queue<String> peekedTokens;

    /**
     * The saved exception (if any) encountered during parsing.
     */
    private Throwable parsingException;

    /**
     * Whether we are doing debug printing.
     */
    private boolean debug = false;

    /**
     * Hash set of additional options keywords that could appear inside parentheses.
     */
    private Set<Keyword> additionalOptionsKeywords = EnumSet.of(Keyword.HASH, Keyword.ON, Keyword.WITH, Keyword.LOCATION);

    /**
     * Initializes a new instance of the SQL formatter.
     */
    public SQLFormatter() {
    }

    /**
     * Set the {@link #debug} flag to print out helpful information.
     * @param flag The new debug flag setting.
     */
    public void setDebug(boolean flag) {
        this.debug = flag;
    }

    /**
     * Whether we wish to allow the appending of whitespace for the next {@link #output} operation.
     */
    private boolean addWhitespace;

    /**
     * Appends a string to the result if we're in the second pass.
     *
     * @param string The string to append.
     */
    private void output(String string) {
        if (!firstPass) {
            if (addWhitespace || !isWhitespace(string)) {
                result.append(string);
            }
            addWhitespace = true;
        }
    }

    /**
     * Writes a token to the result.
     */
    private void outputToken() {
        output(token);
    }

    /**
     * Change token to UPPER CASE and write it
     * to the output.
     */
    private void outputUpperToken() {
        output(upperToken);
    }

    /**
     * Output the current keyword (UPPER CASE).
     */
    private void outputKeyword() {
        output(currentKeyword.toString());
    }

    /**
     * Starts a new line.
     *
     * @param indent Whether to indent after the new line.
     */
    private void newline(boolean indent) {
        if (!firstPass) {
            output("\n");
            if (indent) {
                output(INDENT);
                // Add extra space if adding 'WITH' options.
                if (state == State.ADDITIONAL_OPTIONS) {
                    output(" ");
                }
            }
            addWhitespace = false;
        }
    }

    /**
     * Peeks at the next token.
     *
     * @return The next token.
     */
    private String peekToken() {
        if (!tokens.hasMoreTokens()) {
            return null;
        }

        String token = tokens.nextToken();
        peekedTokens.add(token);
        return token;
    }

    /**
     * Gets the next token.
     *
     * @return The next token.
     */
    private String nextToken() {
        if (peekedTokens.size() > 0) {
            return peekedTokens.remove();
        }

        if (!tokens.hasMoreTokens()) {
            return null;
        }

        return tokens.nextToken();
    }

    private void resetFlags() {
        // Restart these variables that have been used for the param stuff
        hasNotNull = false;
        hasWithNull = false;
        hasWithDefault = false;
        hasNotDefault = false;
        firstNullabilityToken = true;
        firstDefaultToken = false;
    }

    /**
     * Closes parentheses.
     */
    private void closeParentheses() {
        printDebug("closeParen: state=%1$s, parenCount=%2$d%n", state, openParenthesesCount);

        // Decrement the counter.
        openParenthesesCount--;

        switch (state) {
            case ADDITIONAL_OPTIONS:
            case COLUMN_NAME_OR_EXPRESSION:
            case WHERE:
            case PROCEDURE_BODY:
            case RETURN_ROW:
            case FOR_EACH:
                outputToken();
                return;
        }

        // Handle case of ')' in a primary key declaration.
        if (state == State.PRIMARY_KEY_DECLARATION) {
            outputToken();
            state = State.COLUMN_DECLARATION_NAME;
            resetFlags();
            return;
        }

        // If we won't have any open parentheses...
        boolean inProcColumns = false;
        if (objectType == Keyword.PROCEDURE) {
            switch (state) {
                case COLUMN_DECLARATION_NAME:
                case COLUMN_DECLARATION_DATA_TYPE:
                case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                case COLUMN_DECLARATION_NULLABILITY:
                case COLUMN_DECLARATION_DEFAULT:
                    inProcColumns = true;
                    break;
            }
        }
        printDebug("closeParen: checking count=%1$d, state=%2$s, objectType=%3$s, inProcColumns=%4$s%n", openParenthesesCount, state, objectType, inProcColumns);
        if (openParenthesesCount == 0 && !inProcColumns) {
            // We want the final close parenthesis on a new line.
            newline(false);
            // Write token and add another new line.
            outputToken();
            newline(false);
            // Set appropriate lexical state.
            if (objectType == Keyword.RULE)
                state = State.FOR_EACH;
            else
                state = State.WITH;
        }
        else if (state == State.PARAM_DECLARATION_DEFAULT) {
            outputToken();
        }
        else {
            printDebug("closeParen: state=%1$s, transition to COLUMN/PARAM/RESULT_ROW_DECLARATION_NULLABILITY%n", state);
            switch (state) {
                case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                    state = State.COLUMN_DECLARATION_NULLABILITY;
                    dataTypeQueue.bufferTypeToken(token);
                    break;
                case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                    state = State.PARAM_DECLARATION_NULLABILITY;
                    paramTypeQueue.bufferTypeToken(token);
                    break;
                case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                    state = State.RESULT_ROW_DECLARATION_NULLABILITY;
                    resultRowTypeQueue.bufferTypeToken(token);
                    break;
            }
        }
    }

    /**
     * Opens parentheses.
     */
    private void openParentheses() {
        // Increment the counter
        openParenthesesCount++;

        // Handle case of '(' in a primary key declaration and elsewhere.
        switch (state) {
            case ADDITIONAL_OPTIONS:
            case PRIMARY_KEY_DECLARATION:
            case COLUMN_NAME_OR_EXPRESSION:
            case WHERE:
            case FOR_EACH:
            case PROCEDURE_BODY:
            case RETURN_ROW:
                outputToken();
                return;
        }

        // If the open paren count is 1...
        boolean inProcColumns = false;
        if (objectType == Keyword.PROCEDURE) {
            switch (state) {
                case COLUMN_DECLARATION_NAME:
                case COLUMN_DECLARATION_DATA_TYPE:
                case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                case COLUMN_DECLARATION_NULLABILITY:
                case COLUMN_DECLARATION_DEFAULT:
                    inProcColumns = true;
                    break;
            }
        }
        if (openParenthesesCount == 1 && !inProcColumns) {
            // ... must be the parenthesis from: create table blah as (
            // so start a new line
            newline(false);
            outputToken();
            newline(true);
            // Set appropriate lexical state.
            printDebug("openParen: state=%1$s, objectType=%2$s%n", state, objectType);
            switch (state) {
                case COLUMN_DECLARATION_DATA_TYPE:
                case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                case PARAM_DECLARATION_DATA_TYPE:
                case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                case RESULT_ROW_DECLARATION_DATA_TYPE:
                case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                    break;
                case OBJECT_NAME:
                case AFTER_BEFORE:
                    if (objectType == Keyword.PROCEDURE)
                        state = State.PARAM_DECLARATION_NAME;
                    else
                        state = State.COLUMN_DECLARATION_NAME;
                    break;
            }
        }
        else {
            printDebug("openParen: state=%1$s, append to xxxTypeNameBuffer%n", state);
            switch (state) {
                case COLUMN_DECLARATION_DATA_TYPE:
                case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                    dataTypeQueue.bufferTypeToken(token);
                    break;
                case PARAM_DECLARATION_DATA_TYPE:
                case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                    paramTypeQueue.bufferTypeToken(token);
                    break;
                case RESULT_ROW_DECLARATION_DATA_TYPE:
                case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                    resultRowTypeQueue.bufferTypeToken(token);
                    break;
            }
        }
    }

    /**
     * Formats / pretty prints an SQL string.
     *
     * @param sql The SQL string.
     *
     * @return The formatted SQL string.
     */
    public String formatSQL(String sql) {

        // Perform any required initialization tasks.
        init();

        this.sql = sql;

        try {
            // Perform the first pass of the SQL to determine the require alignment information.
            process(true);

            // Switch to second pass and reprocess to generate the SQL.
            process(false);

            // Return the result, trimming any spaces from the end of the line.
            return result.toString().replaceAll("\\s+\n", "\n");
        }
        catch (Throwable ex) {
            // Something went wrong, so just return the original SQL, un-formatted.
            Logging.Except("SQLFormatter: formatSQL error", ex);
            parsingException = ex;
            return sql;
        }
    }

    /**
     * @return Any parsing exception encountered during {@link #formatSQL}.
     */
    public Throwable getException() {
        return parsingException;
    }

    /**
     * Performs any required initialization tasks.
     */
    public void init() {
        dataTypeQueue      = new TypeNameQueue();
        paramTypeQueue     = new TypeNameQueue();
        resultRowTypeQueue = new TypeNameQueue();
        openParenthesesCount = 0;
        peekedTokens = new LinkedList<>();
        parsingException = null;
        insideProcBody = false;
        resetFlags();
    }


    /**
     * Performs the processing for the formatter.
     *
     * @param firstPass Flag for which pass over the input we're doing.
     * @throws Exception An exception if something went wrong.
     */
    private void process(boolean firstPass)
            throws Exception
    {
        this.firstPass = firstPass;
        printDebug("%n**************************%n******* %1$s Pass ******%n**************************%n", firstPass ? "First" : "Second");

        if (firstPass) {
            dataTypeQueue.clear();
            paramTypeQueue.clear();
            resultRowTypeQueue.clear();
        }
        else {
            // Start the buffer for the final results
            result = new StringBuilder();

            printDebug("dataTypeNameQueue length=%1$d, paramTypeNameQueue length=%2$d, resultRowTypeNameQueue length=%3$d%n", dataTypeQueue.size(), paramTypeQueue.size(), resultRowTypeQueue.size());
        }

        openParenthesesCount = 0;

        // Set the lexical state.
        state = State.INITIAL_KEYWORD;

        // Tokenize the string.
        tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[],;" + WHITESPACE, true);

        // Loop over the tokens...
        while ((token = nextToken()) != null) {
            lastTokenWasWhitespace = false;
            currentKeyword = null;
            printDebug("pass %1$d: state=%2$s, token=\"%3$s\"%n", firstPass ? 1 : 2, state, token);

            switch (token) {
                case "(":
                    openParentheses();
                    break;
                case ")":
                    switch (state) {
                        case PARAM_DECLARATION_NULLABILITY:
                        case PARAM_DECLARATION_DEFAULT:
                        case RESULT_ROW_DECLARATION_NULLABILITY:
                        case RESULT_ROW_DECLARATION_DEFAULT:
                            if (openParenthesesCount == 1) {
                                handleDataType(true);
                            }
                            break;
                    }
                    closeParentheses();
                    break;
                case ",":
                    handleDataType(false);
                    handleComma(token);
                    break;
                case ";":
                    handleDataType(true);
                    handleComma(token);
                    break;
                default:
                    if (isWhitespace(token)) {
                        lastTokenWasWhitespace = true;
                        handleWhitespace();
                    }
                    else {
                        handleEverythingElse();
                    }
                    break;
            }
        }
        // End with a newline after everything
        newline(false);
    }

    /**
     * Handle a data type name.
     * @param atEnd Whether or not this is the end of the whole declaration (that is, at "," or ";").
     */
    private void handleDataType(boolean atEnd) {
        printDebug("handleDataType(atEnd=%1$s): state=%2$s, firstDefaultToken=%3$s, firstPass=%4$s%n", atEnd, state, firstDefaultToken, firstPass);
        switch (state) {
            case COLUMN_DECLARATION_DATA_TYPE:
            case COLUMN_DECLARATION_NULLABILITY:
            case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                if (!atEnd)
                    break;
                // else fall through
            case COLUMN_DECLARATION_DEFAULT:
                dataTypeQueue.addToDataType(atEnd);
                break;
            case PARAM_DECLARATION_DATA_TYPE:
            case PARAM_DECLARATION_NULLABILITY:
            case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                if (!atEnd)
                    break;
                // else fall through
            case PARAM_DECLARATION_DEFAULT:
                paramTypeQueue.addToDataType(atEnd);
                break;
            case RESULT_ROW_DECLARATION_DATA_TYPE:
            case RESULT_ROW_DECLARATION_NULLABILITY:
            case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                if (!atEnd)
                    break;
                // else fall through
            case RESULT_ROW_DECLARATION_DEFAULT:
                resultRowTypeQueue.addToDataType(atEnd);
                break;
        }
    }

    /**
     * Handles whitespace.
     */
    private void handleWhitespace() {
        switch (state) {
            case COLUMN_DECLARATION_NAME:
            case PARAM_DECLARATION_NAME:
            case RESULT_ROW_DECLARATION_DATA_TYPE:
            case WITH:
                break;
            case COLUMN_DECLARATION_DATA_TYPE:
            case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                dataTypeQueue.addSpace();
                break;
            case PARAM_DECLARATION_DATA_TYPE:
            case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                paramTypeQueue.addSpace();
                break;
            case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                resultRowTypeQueue.addSpace();
                break;
            default:
                output(" ");
                break;
        }
    }

    /**
     * Handles comma.
     * @param whichToken We can be called for several values, so which one was it.
     */
    private void handleComma(String whichToken) {
        printDebug("handleComma:  state=%1$s%n", state);
        // Write the token to the output
        outputToken();

        switch (state) {
            // Handle case of ',' in additional options and elsewhere.
            case ADDITIONAL_OPTIONS:
            case OBJECT_NAME:
                if (openParenthesesCount > 0) {
                    output(" ");
                }
                return;

            case RETURN_ROW:
                if (whichToken.equals(";")) {
                    newline(true);
                    state = State.PROCEDURE_BODY;
                }
                return;

            // Handle case of ',' in a "SELECT" column list
            case COLUMN_NAME_OR_EXPRESSION:
            case WHERE:
                if (openParenthesesCount > 0) {
                    return;
                }
                break;

            // Handle case of ',' in a primary key declaration.
            case PRIMARY_KEY_DECLARATION:
            case FOR_EACH:
                output(" ");
                return;
        }

        // Start a new line, indenting always.
        newline(true);

        // Set appropriate next lexical state.
        switch (state) {
            case ADDITIONAL_OPTIONS:
            case COLUMN_NAME_OR_EXPRESSION:
            case WHERE:
            case PROCEDURE_BODY:
            case FOR_EACH:
                break;
            case PARAM_DECLARATION_DEFAULT:
                state = State.PARAM_DECLARATION_NAME;
                break;
            case RESULT_ROW_DECLARATION_DEFAULT:
                state = State.RESULT_ROW_DECLARATION_DATA_TYPE;
                break;
            default:
                printDebug("handleComma next state: state=%1$s, going to COLUMN_DECLARATION_NAME%n", state);
                state = State.COLUMN_DECLARATION_NAME;
                break;
        }
    }

    private void handleEverythingElse() {
        upperToken = token.toUpperCase();
        currentKeyword = Keyword.lookup(upperToken);
        printDebug("handleEverythingElse:  state=%1$s, token=\"%2$s\", upperToken=\"%3$s\", keyword=%4$s%n", state, token, upperToken, currentKeyword);
        switch (state) {
            case INITIAL_KEYWORD:
                initialKeyword = currentKeyword;
                outputKeyword();
                switch (initialKeyword) {
                    case CREATE:
                        state = State.CREATE_KEY_WORDS;
                        break;
                    case SELECT:
                        state = State.FIRST_N;
                        break;
                }
                break;

            case CREATE_KEY_WORDS:
                // Set appropriate lexical state.
                switch (currentKeyword) {
                    case TABLE:
                    case VIEW:
                    case PROCEDURE:
                    case RULE:
                        objectType = currentKeyword;
                        outputKeyword();
                        state = State.OBJECT_NAME;
                        break;
                    default:
                        outputUpperToken();
                        break;
                }
                break;

            case FIRST_N:
                if (currentKeyword == Keyword.FIRST) {
                    newline(false);
                    output("  ");  // indent just 2 here
                    outputKeyword();
                }
                else {
                    try {
                        Integer.parseInt(token);
                        outputToken();
                        newline(true);
                    }
                    catch (NumberFormatException nfe) {
                        // Then this must be a column name (or start of expression)
                        newline(true);
                        token = token.toLowerCase();
                        outputToken();
                    }
                    state = State.COLUMN_NAME_OR_EXPRESSION;
                }
                break;

            case OBJECT_NAME:
                switch (currentKeyword) {
                    case AS:
                        newline(false);
                        outputKeyword();
                        state = State.AS;
                        break;
                    case WHERE:
                        newline(false);
                        outputKeyword();
                        newline(true);
                        state = State.WHERE;
                        break;
                    case RESULT:
                        if (objectType == Keyword.PROCEDURE) {
                            state = State.RESULT;
                            newline(false);
                        }
                        outputKeyword();
                        break;
                    case AFTER:
                    case BEFORE:
                        if (objectType == Keyword.RULE) {
                            state = State.AFTER_BEFORE;
                            newline(false);
                        }
                        outputKeyword();
                        break;
                    default:
                        token = token.toLowerCase();
                        outputToken();
                        break;
                }
                break;

            case AFTER_BEFORE:
                switch (currentKeyword) {
                    case WHERE:
                        newline(false);
                        outputKeyword();
                        newline(true);
                        state = State.WHERE;
                        break;
                    case INSERT:
                    case UPDATE:
                    case DELETE:
                    case FROM:
                    case INTO:
                    case OF:
                        outputKeyword();
                        break;
                    default:
                        outputUpperToken();
                        break;
                }
                break;

            case COLUMN_NAME_OR_EXPRESSION:
                if (token.equals("*") && openParenthesesCount <= 0 ) {
                    outputToken();
                    // We can't have any more column names, so move on
                    state = State.FROM;
                }
                else if (currentKeyword == Keyword.FROM) {
                    newline(false);
                    outputKeyword();
                    newline(true);
                    state = State.OBJECT_NAME;
                }
                else {
                    // Lower case the name or expression and write to result.
                    token = token.toLowerCase();
                    outputToken();
                }
                break;

            case FROM:
                outputUpperToken();
                if (currentKeyword == Keyword.FROM) {
                    newline(true);
                    state = State.OBJECT_NAME;
                }
                break;

            case PRIMARY_KEY_DECLARATION:
                if (currentKeyword == Keyword.KEY) {
                    outputKeyword();
                }
                else {
                    outputToken();
                }
                break;

            case PROCEDURE_BODY:
            case RETURN_ROW:
                switch (currentKeyword) {
                    case END:
                        // Take out any indent before this
                        if (!firstPass) {
                            if (result.lastIndexOf(INDENT) == result.length() - INDENT.length()) {
                                result.setLength(result.length() - INDENT.length());
                            }
                        }
                        outputKeyword();
                        break;
                    case RETURN:
                        newline(true);
                        outputKeyword();
                        break;
                    case ROW:
                        outputKeyword();
                        state = State.RETURN_ROW;
                        break;
                    default:
                        outputToken();
                        break;
                }
                break;

            case COLUMN_DECLARATION_NAME:
            case PARAM_DECLARATION_NAME:
                // Handle case of primary key declaration.
                if (currentKeyword == Keyword.PRIMARY) {
                    state = State.PRIMARY_KEY_DECLARATION;
                    outputKeyword();
                    break;
                }

                // Handle case of "BEGIN" (in Procedure definitions)
                if (currentKeyword == Keyword.BEGIN) {
                    printDebug("BEGIN found, transition to PROCEDURE_BODY, state=%1$s, addWhitespace=%2$s%n", state, addWhitespace);
                    // We likely had a "newline(true)" right before this, but we don't want
                    // this indented, so delete any indent that was there
                    if (!firstPass) {
                        if (result.lastIndexOf(INDENT) == result.length() - INDENT.length()) {
                            result.setLength(result.length() - INDENT.length());
                        }
                    }
                    newline(false);
                    outputKeyword();
                    newline(true);
                    state = State.PROCEDURE_BODY;
                    insideProcBody = true;
                    break;
                }

                String name = token;
                if (token.equals("\"")) {
                    StringBuilder nameBuilder = new StringBuilder();
                    nameBuilder.append(token);
                    while ((token = nextToken()) != null) {
                        nameBuilder.append(token);
                        if (token.equals("\"")) {
                            String peekedToken = peekToken();
                            if (peekedToken != "\"") {
                                break;
                            }
                        }
                    }
                    name = nameBuilder.toString();
                }

                firstNullabilityToken = true;
                switch (state) {
                    case COLUMN_DECLARATION_NAME:
                        name = dataTypeQueue.getMaxName(name);
                        break;
                    case PARAM_DECLARATION_NAME:
                        name = paramTypeQueue.getMaxName(name);
                        break;
                }
                output(name);
                switch (state) {
                    case COLUMN_DECLARATION_NAME:
                        state = State.COLUMN_DECLARATION_DATA_TYPE;
                        break;
                    case PARAM_DECLARATION_NAME:
                        state = State.PARAM_DECLARATION_DATA_TYPE;
                        break;
                }
                break;

            case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
            case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
            case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                switch (currentKeyword) {
                    case WHERE:
                        newline(false);
                        outputToken();
                        newline(true);
                        state = State.WHERE;
                        break;
                    case NOT:
                    case WITH:
                        // Hit a 'NOT/WITH' as part of a 'NOT/WITH NULL'
                        if (firstPass) {
                            hasNotNull = currentKeyword == Keyword.NOT;
                            hasWithNull = currentKeyword == Keyword.WITH;
                        }
                        else {
                            if (currentKeyword == Keyword.NOT && hasWithNull) {
                                upperToken = upperToken + " ";
                            }
                        }

                        firstNullabilityToken = false;

                        switch (state) {
                            case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                state = State.COLUMN_DECLARATION_NULLABILITY;
                                break;
                            case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                state = State.PARAM_DECLARATION_NULLABILITY;
                                break;
                            case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                state = State.RESULT_ROW_DECLARATION_NULLABILITY;
                                break;
                        }
                        outputUpperToken();
                        break;
                    case NULL:
                        // Hit a 'NULL'
                        if (!firstPass) {
                            if (firstNullabilityToken && hasNotNull) {
                                upperToken = String.format("     %1$s", upperToken);
                            }
                        }

                        firstNullabilityToken = false;
                        switch (state) {
                            case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                state = State.COLUMN_DECLARATION_DEFAULT;
                                break;
                            case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                state = State.PARAM_DECLARATION_DEFAULT;
                                break;
                            case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                state = State.RESULT_ROW_DECLARATION_DEFAULT;
                                break;
                        }
                        firstDefaultToken = true;
                        outputUpperToken();
                        break;
                    default:
                        // Must still be in a data type
                        switch (state) {
                            case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                dataTypeQueue.bufferTypeToken(upperToken);
                                break;
                            case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                paramTypeQueue.bufferTypeToken(upperToken);
                                break;
                            case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                resultRowTypeQueue.bufferTypeToken(upperToken);
                                break;
                        }
                        break;
                }
                break;

            case COLUMN_DECLARATION_NULLABILITY:
            case PARAM_DECLARATION_NULLABILITY:
            case RESULT_ROW_DECLARATION_NULLABILITY:
                switch (currentKeyword) {
                    case NOT:
                    case WITH:
                        // Hit a 'NOT/WITH' as part of a 'NOT/WITH NULL'
                        if (firstPass) {
                            hasNotNull = currentKeyword == Keyword.NOT;
                            hasWithNull = currentKeyword == Keyword.WITH;
                        }
                        else {
                            if (currentKeyword == Keyword.NOT && hasWithNull) {
                                upperToken = upperToken + " ";
                            }
                        }
                        firstNullabilityToken = false;
                        break;
                    case NULL:
                        firstNullabilityToken = false;
                        switch (state) {
                            case COLUMN_DECLARATION_NULLABILITY:
                                state = State.COLUMN_DECLARATION_DEFAULT;
                                break;
                            case PARAM_DECLARATION_NULLABILITY:
                                state = State.PARAM_DECLARATION_DEFAULT;
                                break;
                            case RESULT_ROW_DECLARATION_NULLABILITY:
                                state = State.RESULT_ROW_DECLARATION_DEFAULT;
                                break;
                        }
                        firstDefaultToken = true;
                        break;
                }
                outputUpperToken();
                break;

            case COLUMN_DECLARATION_DATA_TYPE:
            case PARAM_DECLARATION_DATA_TYPE:
            case RESULT_ROW_DECLARATION_DATA_TYPE:
                switch (state) {
                    case COLUMN_DECLARATION_DATA_TYPE:
                        dataTypeQueue.resetLength();
                        break;
                    case PARAM_DECLARATION_DATA_TYPE:
                        paramTypeQueue.resetLength();
                        break;
                    case RESULT_ROW_DECLARATION_DATA_TYPE:
                        resultRowTypeQueue.resetLength();
                        break;
                }

                switch (currentKeyword) {
                    case AS:
                        newline(false);
                        outputKeyword();
                        state = State.AS;
                        break;
                    case WHERE:
                        newline(false);
                        outputKeyword();
                        newline(true);
                        state = State.WHERE;
                        break;
                    default:
                        printDebug("State=%1$s, pass=%2$s, switching to DATA_TYPE_OR_NULLABILITY on token %3$s%n", state, firstPass ? "First" : "Second", token);
                        switch (state) {
                            case COLUMN_DECLARATION_DATA_TYPE:
                            case COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                dataTypeQueue.appendOutputType(upperToken);
                                state = State.COLUMN_DECLARATION_DATA_TYPE_OR_NULLABILITY;
                                break;
                            case PARAM_DECLARATION_DATA_TYPE:
                            case PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                paramTypeQueue.appendOutputType(upperToken);
                                state = State.PARAM_DECLARATION_DATA_TYPE_OR_NULLABILITY;
                                break;
                            case RESULT_ROW_DECLARATION_DATA_TYPE:
                            case RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY:
                                resultRowTypeQueue.appendOutputType(upperToken);
                                state = State.RESULT_ROW_DECLARATION_DATA_TYPE_OR_NULLABILITY;
                                break;
                        }
                        break;
                }
                break;

            case WITH:
                switch (currentKeyword) {
                    case AS:
                        state = State.AS;
                        newline(false);
                        break;
                    case RESULT:
                        if (objectType == Keyword.PROCEDURE)
                            state = State.RESULT;
                        break;
                    default:
                        state = State.ADDITIONAL_OPTIONS;
                        break;
                }
                outputUpperToken();
                break;

            case AS:
                // Looking for "SELECT" (for Views) or "DECLARE"/"BEGIN" (for Procedures)
                switch (currentKeyword) {
                    case SELECT:
                        outputKeyword();
                        newline(true);
                        state = State.FIRST_N;
                        break;
                    case DECLARE:
                        newline(false);
                        outputKeyword();
                        newline(true);
                        state = State.COLUMN_DECLARATION_NAME;
                        resetFlags();
                        break;
                    case BEGIN:
                        newline(false);
                        outputKeyword();
                        newline(true);
                        state = State.PROCEDURE_BODY;
                        insideProcBody = true;
                        break;
                }
                break;

            case RESULT:
                switch (currentKeyword) {
                    case ROW:
                        if (objectType == Keyword.PROCEDURE) {
                            state = State.RESULT_ROW_DECLARATION_DATA_TYPE;
                            resetFlags();
                        }
                        break;
                }
                outputUpperToken();
                break;

            case WHERE:
                switch (currentKeyword) {
                    case AND:
                        newline(false);
                        output("  ");
                        outputKeyword();
                        newline(true);
                        break;
                    case FOR:
                        newline(false);
                        outputKeyword();
                        state = State.FOR_EACH;
                        break;
                    default:
                        outputToken();
                        break;
                }
                break;

            case FOR_EACH:
                switch (currentKeyword) {
                     case EACH:
                     case STATEMENT:
                     case PROCEDURE:
                         outputKeyword();
                         break;
                     case EXECUTE:
                         newline(false);
                         outputKeyword();
                         break;
                     case WHERE:
                        newline(false);
                        outputKeyword();
                        newline(true);
                        state = State.WHERE;
                        break;
                     default:
                         outputToken();
                         break;
                }
                break;

            case COLUMN_DECLARATION_DEFAULT:
            case PARAM_DECLARATION_DEFAULT:
            case RESULT_ROW_DECLARATION_DEFAULT:
                handleDataType(false);

                if (firstPass) {
                    if (currentKeyword == Keyword.WITH) {
                        hasWithDefault = true;
                    }
                    else if (currentKeyword == Keyword.NOT) {
                        hasNotDefault = true;
                    }
                }
                else {
                    if (firstDefaultToken) {
                        if (currentKeyword == Keyword.DEFAULT && (hasNotDefault || hasWithDefault)) {
                            upperToken = String.format("     %1$s", upperToken);
                        }
                        else if (currentKeyword == Keyword.NOT && hasWithDefault) {
                            upperToken = upperToken + " ";
                        }
                    }
                }

                firstDefaultToken = false;
                outputUpperToken();
                break;

            case ADDITIONAL_OPTIONS:
                if (openParenthesesCount > 0) {
                    if (additionalOptionsKeywords.contains(currentKeyword)) {
                        token = upperToken;
                    }
                }
                else {
                    token = upperToken;
                }
                if (token.equals("=")) {
                    // Ensure opening and closing whitespace around '=' signs.
                    if (!lastTokenWasWhitespace) {
                        output(" ");
                    }
                    outputToken();
                    output(" ");
                    addWhitespace = false;
                }
                else {
                    outputToken();
                }
                break;
        }
    }

    /**
     * Helper method for determining if a token is whitespace.
     *
     * @param token The token.
     *
     * @return Whether the token is whitespace.
     */
    private static boolean isWhitespace(final String token) {
        return WHITESPACE.indexOf(token) >= 0;
    }

    private void printDebug(String format, Object... options) {
        if (debug)
            System.out.format(format, options);
    }

}
