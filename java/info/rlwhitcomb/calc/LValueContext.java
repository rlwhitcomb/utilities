/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2024 Roger L. Whitcomb.
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
 *      Deal with LValue context in Calc -- mostly for nested variables (such as objects and arrays).
 *
 * History:
 *  08-Jan-21 rlw ----	Extracted from CalcObjectVisitor.
 *  18-Jan-21 rlw ----	Move text to the resource file.
 *  20-Jan-21 rlw ----	Allow indexing into strings. Some renaming of parameters and variables.
 *  25-Jan-21 rlw ----	Add Javadoc; one more rename; one more error check on loop variables.
 *  26-Jan-21 rlw ----	Allow ["name"] to extract fields of map objects.
 *  29-Jan-21 rlw ----	Use new Intl Exception variants.
 *  17-Feb-21 rlw ----	Add "visitor" parameter for function evaluation.
 *  22-Feb-21 rlw ----	Refactor "loopvar" to "localvar".
 *  25-Mar-21 rlw ----	Check for string index out of bounds in getContextObject.
 *  07-Apr-21 rlw ----	Implement Unicode subscripts as array indexes.
 *  20-Apr-21 rlw ----	Simplify objVar parsing. Partially add "functionVar" processing.
 *  02-Jul-21 rlw ----	Changes for always displaying thousands separators.
 *  10-Jul-21 rlw ----	Implement "ignore case" for variable / member names.
 *  25-Aug-21 rlw ----	Implement global variables ($nn, set on command line) and do
 *			special error handling for them.
 *  09-Sep-21 rlw ----	Allow interpolated strings as member names; fix potential
 *			problems with string names having escape sequences.
 *  06-Oct-21 rlw #24	Fully implement function parameters.
 *  07-Oct-21 rlw ----	Add context parameter to "toStringValue", move function call setup
 *			to CalcObjectVisitor so it can be called from there if needed also.
 *  08-Oct-21 rlw ----	Error if function is undefined.
 *  14-Oct-21 rlw ----	Allow the "mode" option values as IDs.
 *  16-Oct-21 rlw #33	If we have a function var context (that is a function call with parameters)
 *			and the context object is a FunctionScope (that is another function call) then we
 *			need to call it, and setup the context with the result.
 *  19-Oct-21 rlw ----	Special mode for "getContextObject" to throw if the variable/member is not defined.
 *  28-Oct-21 rlw ----	Changes for new predefined value paradigm.
 *  03-Nov-21 rlw #69	New global variables "$#" and "$*".
 *  09-Nov-21 rlw #74	Improve error messages.
 *  17-Nov-21 rlw #96	Add "visitor" to "getContextObject" parameters so that functions can be evaluated.
 *			Two changes to make LValues that are function results work correctly for indexes and members.
 *  31-Dec-21 rlw #180	Change parameters to "toStringValue".
 *  21-Jan-22 rlw #135	Add support for constant values.
 *  30-Jan-22 rlw #229	Fix defaulting of missing actual parameters.
 *  05-Feb-22 rlw #233	Implement calling "setValue" for SystemValue.
 *  13-Feb-22 rlw #199	Redo the local / global variable handling.
 *  02-May-22 rlw #68	Allow indexing by integer index value (using key list), including
 *			negative indexes (offset from length).
 *  11-May-22 rlw #318	Rename "evaluateFunction" to "evaluate".
 *  17-May-22 rlw #333	Redo awkward error message.
 *  27-May-22 rlw ----	Move "setupFunctionCall" to FunctionDeclaration.
 *			Make parameters final.
 *  04-Jun-22 rlw #361	Don't access the LHS of an array object twice during dereference.
 *  23-Jun-22 rlw #314	Turn the empty collection into an ObjectScope when necessary.
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  10-Jul-22 rlw #392	Create objects with proper sorting of keys; allow "a['ten'] = 20" to
 *			create an object based on the non-numeric index value.
 *  13-Jul-22 rlw #407	Another case where we need to promote the empty CollectionScope to a real map.
 *  19-Jul-22 rlw #412	Refactor parameter to "toStringValue" using "StringFormat" structure.
 *  15-Aug-22 rlw #440	Use correct math context for index conversion.
 *  25-Sep-22 rlw ----	Rename "toNonNullString" to "getNonNullString".
 *  17-Dec-22 rlw #572	Regularize member name access.
 *  24-Dec-22 rlw #83	One more index Unicode character.
 *  16-May-23 rlw ----	Rename some helper methods.
 *  24-May-23 rlw ----	Try a slight variation for the new History format.
 *		  #611	Move processing of builtin functions to here.
 *  27-Sep-23 rlw #630	Add indexing into sets.
 *  23-Mar-24 rlw #664	Named parameter changes.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.Intl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.regex.Pattern;

import static info.rlwhitcomb.calc.CalcUtil.*;


/**
 * Represent a nested context for objects and arrays.
 */
class LValueContext
{
	/** The parent context (for nested naming purposes). */
	LValueContext parent;
	/** The variable context (for error reporting). */
	CalcParser.VarContext varCtx;
	/** The surrounding context to reference into (a {@code Map} or {@code List}). */
	Object context;
	/** The variable / member name to reference into a {@code Map}. */
	String name;
	/** The integer index value to reference into a {@code List}. */
	int index;
	/** The "ignore case for members" flag. */
	boolean ignoreCase;

	/** A pattern for global variables - must match Calc.g4 */
	private static final Pattern GLOBALVAR = Pattern.compile("(\\$([#\\*0]|[1-9][0-9]*))");

	/** A pattern for local variables - must match Calc.g4 */
	private static final Pattern LOCALVAR = Pattern.compile("(_([_#\\*0]|[1-9][0-9]*))");


	/**
	 * Set a new "ignore case" mode (only for the global context).
	 *
	 * @param ignoreNameCase The new global "ignore case" value.
	 */
	void setIgnoreCase(final boolean ignoreNameCase) {
	    ignoreCase = ignoreNameCase;
	}


	/**
	 * Construct given the base context object (which should be the global variables map).
	 *
	 * @param obj            The context object (base variables map).
	 * @param ignoreNameCase The global "ignore case" value.
	 */
	LValueContext(final Object obj, final boolean ignoreNameCase) {
	    parent     = null;
	    varCtx     = null;
	    context    = obj;
	    name       = null;
	    index      = Integer.MIN_VALUE;
	    ignoreCase = ignoreNameCase;
	}

	/**
	 * Construct for a <code>map.member</code> reference.
	 *
	 * @param p	The parent lvalue.
	 * @param ctx	The parser context we're working in.
	 * @param obj	The map object we're referencing into.
	 * @param nm	The name of the map member to reference.
	 */
	LValueContext(final LValueContext p, final CalcParser.VarContext ctx, final Object obj, final String nm) {
	    parent     = p;
	    varCtx     = ctx;
	    context    = obj;
	    name       = nm;
	    index      = Integer.MIN_VALUE;
	    ignoreCase = p.ignoreCase;
	}

	/**
	 * Construct for a <code>arr[idx]</code> reference.
	 *
	 * @param p	The parent lvalue.
	 * @param ctx	The parser context we're working in.
	 * @param obj	The array, set, or string object we're referencing into.
	 * @param idx	The index into the object.
	 */
	LValueContext(final LValueContext p, final CalcParser.VarContext ctx, final Object obj, final int idx) {
	    parent     = p;
	    varCtx     = ctx;
	    context    = obj;
	    name       = null;
	    index      = idx;
	    ignoreCase = p.ignoreCase;
	}

	/**
	 * Construct for a function reference.
	 *
	 * @param p	The parent lvalue.
	 * @param ctx	The parser context we're working in.
	 * @param obj	The new function scope.
	 */
	LValueContext(final LValueContext p, final CalcParser.VarContext ctx, final Object obj) {
	    parent     = p;
	    varCtx     = ctx;
	    context    = obj;
	    name       = null;
	    index      = Integer.MIN_VALUE;
	    ignoreCase = p.ignoreCase;
	}

	@Override
	public String toString() {
	    String parentName = parent == null ? "" : parent.toString();
	    if (name != null) {
		if (parent.parent == null)
		    return parentName + name;
		else
		    return parentName + "." + name;
	    }
	    else if (index != Integer.MIN_VALUE)
		return parentName + "[" + index + "]";
	    else
		return "";
	}

	/**
	 * At the current variable nesting level, extract the referenced value.
	 * <p> For a map, this would be <code>map.member</code>, for an array
	 * or string, this would be <code>arr[index]</code>.
	 * <p> One special check is made for error reporting purposes on local variables.
	 *
	 * @param allowUndefined if {@code true} (which is the default) then undefined
	 *                       variables or members are allowed, otherwise error out
	 * @return The member or indexed value extracted from the base object.
	 */
	@SuppressWarnings("unchecked")
	public Object getPredefinedContextObject(final boolean allowUndefined) {
	    Object contextObject = context;
	    if (contextObject instanceof ValueScope) {
		contextObject = ((ValueScope) contextObject).getValue();
	    }
	    Object result;
	    if (name != null) {
		ObjectScope map = (ObjectScope) contextObject;
		// Special checks for local vars (not defined means we are outside the loop or function)
		if (LOCALVAR.matcher(name).matches()) {
		    if (!map.isDefined(name, ignoreCase)) {
			throw new CalcExprException(varCtx, "%calc#localVarNotAvail", name);
		    }
		}
		if (!allowUndefined) {
		    if (!map.isDefined(name, ignoreCase)) {
			throw new CalcExprException(varCtx, "%calc#undefined", name);
		    }
		}
		result = map.getValue(name, ignoreCase);
	    }
	    else if (index != Integer.MIN_VALUE) {
		if (contextObject instanceof ArrayScope) {
		    ArrayScope list = (ArrayScope) contextObject;
		    result = list.getValue(index);
		}
		else if (contextObject instanceof ObjectScope) {
		    ObjectScope obj = (ObjectScope) contextObject;
		    return obj.valueAt(index);
		}
		else if (contextObject instanceof SetScope) {
		    SetScope set = (SetScope) contextObject;
		    return set.get(index);
		}
		else if (contextObject instanceof String) {
		    String str = (String) contextObject;
		    if (index >= str.length()) {
			return null;
		    }
		    else if (index < 0) {
			index += str.length();
		    }
		    return str.substring(index, index + 1);
		}
		else {
		    // Should never happen
		    throw new Intl.IllegalStateException("calc#badAssign", this);
		}
	    }
	    else {
		// This should only ever be in the outermost global context
		// where the context is just the global variables scope
		result = contextObject;
	    }

	    return result;
	}

	/**
	 * At the current variable nesting level, extract the referenced value.
	 * <p> For a map, this would be <code>map.member</code>, for an array
	 * or string, this would be <code>arr[index]</code>.
	 * <p> One special check is made for error reporting purposes on local variables.
	 *
	 * @param visitor The object visitor used to do calculations.
	 * @return The member or indexed value extracted from the base object.
	 */
	public Object getContextObject(final CalcObjectVisitor visitor) {
	    return getContextObject(visitor, true);
	}

	public Object getContextObject(final CalcObjectVisitor visitor, final boolean allowUndefined) {
	    Object result = getPredefinedContextObject(allowUndefined);

	    if (result instanceof CalcParser.BuiltinFunctionContext) {
		result = visitor.evaluate((ParserRuleContext) result);
	    }
	    if (result instanceof FunctionScope) {
		result = visitor.evaluate(varCtx, result);
	    }
	    if (result instanceof ValueScope) {
		ValueScope valueObj = (ValueScope) result;
		result = valueObj.getValue();
	    }

	    return result;
	}

	/**
	 * At the current nesting level, set the given value as the object.
	 * <p> For a map, this would be <code>map.member = value</code>, for an
	 * array or string, this would be <code>arr[index] = value</code>.
	 * <p> One special check is made on local variables because we don't
	 * want them to be reassigned apart from the <code>loop</code> or
	 * <code>def</code> construct.
	 *
	 * @param visitor The visitor (for function evaluation).
	 * @param value	  The new value to assign to this context.
	 * @return	  This value (for convenience in the assign operators).
	 */
	@SuppressWarnings("unchecked")
	public Object putContextObject(final CalcObjectVisitor visitor, final Object value) {
	    Object contextObject = context;
	    Object returnValue = value;

	    if (contextObject instanceof ValueScope && ((ValueScope) contextObject).isImmutable()) {
		ValueScope constant = (ValueScope) contextObject;
		throw new CalcExprException(varCtx, "%calc#noChangeValue", constant.toString(), constant.getName(),
			name != null ? "." + name : "[" + String.valueOf(index) + "]");
	    }

	    if (name != null) {
		if (GLOBALVAR.matcher(name).matches())
		    throw new CalcExprException(varCtx, "%calc#globalArgNoAssign", name);
		else if (LOCALVAR.matcher(name).matches())
		    throw new CalcExprException(varCtx, "%calc#localVarNoAssign", name);

		ObjectScope map = (ObjectScope) context;
		Object oldValue = map.getValue(name, ignoreCase);
		if (oldValue instanceof ValueScope && ((ValueScope) oldValue).isImmutable()) {
		    ValueScope constant = (ValueScope) oldValue;
		    throw new CalcExprException(varCtx, "%calc#noChangeValue", constant.toString(), constant.getName(), "");
		}
		else if (oldValue instanceof SystemValue) {
		    SystemValue sysValue = (SystemValue) oldValue;
		    sysValue.setValue(value);
		    // Transformation might have happened in the "setValue", so reget the value
		    // afterward so we know what happened in transit
		    returnValue = sysValue.getValue();
		}
		else {
		    map.setValue(name, ignoreCase, value);
		}
	    }
	    else if (index != Integer.MIN_VALUE) {
		if (context instanceof ArrayScope) {
		    ArrayScope list = (ArrayScope) context;
		    list.setValue(index, value);
		}
		else if (context instanceof ObjectScope) {
		    ObjectScope map = (ObjectScope) context;
		    map.setValue(index, value);
		}
		else if (context instanceof String) {
		    String str = (String) context;
		    StringBuilder buf = new StringBuilder(str);
		    String newValue = CalcUtil.toStringValue(visitor, varCtx, value, new StringFormat(false, false));
		    int newLen = index + newValue.length();
		    if (index < 0)
			newLen += str.length();

		    // Ensure the builder has enough length to do the replacement
		    while (buf.length() < newLen) {
			buf.append(' ');
		    }
		    buf.replace(index < 0 ? index + str.length() : index, newLen, newValue);
		    context = buf.toString();
		    // Have to update the parent as well with the new string
		    parent.putContextObject(visitor, context);
		}
		else {
		    // Should never happen
		    throw new Intl.IllegalStateException("calc#badAssign", this);
		}
	    }
	    else {
		// Should never happen
		throw new Intl.IllegalStateException("calc#badAssign", this);
	    }

	    // For convenience for the assignment operators, return the value
	    return returnValue;
	}

	/**
	 * An intermediate step used for maps with members as a string (as in <code>map."field"</code>).
	 *
	 * @param visitor	The visitor object (for function evaluation).
	 * @param ctx		The parser context we are working in.
	 * @param memberName	The (quoted) member name to use at this level.
	 * @return		The new context for the <code>map."member"</code>.
	 */
	private LValueContext makeMapLValue(final CalcObjectVisitor visitor, final CalcParser.VarContext ctx, final String memberName) {
	    return makeMapLValue(visitor, ctx, getContextObject(visitor), memberName);
	}

	/**
	 * An intermediate step used for maps with members as a string (as in <code>map."field"</code>).
	 *
	 * @param visitor	The visitor object (for function evaluation).
	 * @param ctx		The parser context we are working in.
	 * @param contextObj	The context object.
	 * @param memberName	The (quoted) member name to use at this level.
	 * @return		The new context for the <code>map."member"</code>.
	 */
	private LValueContext makeMapLValue(final CalcObjectVisitor visitor, final CalcParser.VarContext ctx, final Object contextObj, final String memberName) {
	    ObjectScope map = null;
	    Object objValue = contextObj;

	    if (objValue == null) {
		map = new ObjectScope(visitor.getSettings().sortKeys);
		putContextObject(visitor, map);
	    }
	    else if (objValue instanceof ObjectScope) {
		map = (ObjectScope) objValue;
		if (memberName == null) {
		    return new LValueContext(this, ctx, map, null);
		}
	    }
	    else if (objValue.equals(CollectionScope.EMPTY)) {
		map = new ObjectScope(visitor.getSettings().sortKeys);
		putContextObject(visitor, map);
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#nonObjectValue", this, typeof(objValue));
	    }

	    if (memberName != null) {
		return new LValueContext(this, ctx, map, memberName);
	    }

	    return this;
	}

	/**
	 * The main workhorse method of this class: given a context, and the surrounding <code>lValue</code>,
	 * get the next level of lValue.
	 *
	 * @param visitor	The current visitor, for use in evaluating index expressions.
	 * @param ctx		The current parse context.
	 * @param lValue	The surrounding lValue; for global variables, this references the global map.
	 * @return		The next level of <code>lValue</code> as determined by the new <code>ctx</code> type.
	 */
	@SuppressWarnings("unchecked")
	public static LValueContext getLValue(final CalcObjectVisitor visitor, final CalcParser.VarContext ctx, final LValueContext lValue) {
	    if (ctx instanceof CalcParser.IdVarContext) {
		CalcParser.IdVarContext idVarCtx = (CalcParser.IdVarContext) ctx;
		return new LValueContext(lValue, idVarCtx, lValue.getPredefinedContextObject(true), idVarCtx.id().getText());
	    }
	    else if (ctx instanceof CalcParser.BuiltinVarContext) {
		CalcParser.BuiltinVarContext builtinCtx = (CalcParser.BuiltinVarContext) ctx;
		return new LValueContext(lValue, builtinCtx, builtinCtx.builtinFunction());
	    }
	    else if (ctx instanceof CalcParser.GlobalVarContext) {
		CalcParser.GlobalVarContext globalVarCtx = (CalcParser.GlobalVarContext) ctx;
		return new LValueContext(lValue, globalVarCtx, lValue.getPredefinedContextObject(true), globalVarCtx.GLOBALVAR().getText());
	    }
	    else if (ctx instanceof CalcParser.ArrVarContext) {
		CalcParser.ArrVarContext arrVarCtx = (CalcParser.ArrVarContext) ctx;
		LValueContext arrLValue = getLValue(visitor, arrVarCtx.var(), lValue);
		Object arrValue = arrLValue.getContextObject(visitor);

		if (arrValue != null && arrValue.equals(CollectionScope.EMPTY)) {
		    // a = {}; a[x] = y; So, convert the empty object to a map
		    arrValue = new ObjectScope(visitor.getSettings().sortKeys);
		    arrLValue.putContextObject(visitor, arrValue);
		}

		CalcParser.ExprContext expr = arrVarCtx.expr();

		// Okay, here the "arrValue" could be null, an array, a set, an object (map), or a string.
		if (arrValue != null && arrValue instanceof ObjectScope) {
		    // The "index" expression should be a string (meaning a member name)
		    // but it could be a numeric index into the key set (return from "index")
		    Object indexValue = visitor.evaluate(expr);

		    if (indexValue instanceof Number) {
			int index = CalcUtil.convertToInt(indexValue, visitor.getSettings().mc, expr);
			return new LValueContext(arrLValue, arrVarCtx, arrValue, index);
		    }
		    else {
			String memberName = visitor.getNonNullString(expr, indexValue);
			return arrLValue.makeMapLValue(visitor, arrVarCtx, arrValue, memberName);
		    }
		}

		// By now, the object must either be null, a list, set, string, or a simple value (an error)
		// so we need to decide if the index is a string or a number to decide if a null value
		// should create and object or an array.
		int index = Integer.MIN_VALUE;
		String memberName = null;

		if (expr == null) {
		    String indexString = arrVarCtx.INDEXES().getText();
		    char indexCh = indexString.charAt(0);
		    if (indexCh == '\u23E8')
			index = 10;
		    else
			index = (int) (indexCh - 0x2080);
		}
		else {
		    Object indexValue = visitor.evaluate(expr);

		    if (indexValue instanceof Number) {
			index = CalcUtil.convertToInt(indexValue, visitor.getSettings().mc, expr);
		    }
		    else {
			memberName = visitor.getNonNullString(expr, indexValue);
		    }
		}

		ArrayScope list = null;
		ObjectScope map = null;
		SetScope set = null;

		if (arrValue == null) {
		    if (memberName != null) {
			map = new ObjectScope(visitor.getSettings().sortKeys);
			arrLValue.putContextObject(visitor, map);
		    }
		    else {
			list = new ArrayScope();
			arrLValue.putContextObject(visitor, list);
		    }
		}
		else if (arrValue instanceof ArrayScope) {
		    list = (ArrayScope) arrValue;
		}
		else if (arrValue instanceof SetScope) {
		    set = (SetScope) arrValue;
		}
		else if (arrValue instanceof String) {
		    return new LValueContext(arrLValue, arrVarCtx, arrValue, index);
		}
		else {
		    throw new CalcExprException(arrVarCtx, "%calc#nonArrayValue", arrLValue, typeof(arrValue));
		}

		if (list != null) {
		    return new LValueContext(arrLValue, arrVarCtx, list, index);
		}
		else if (set != null) {
		    return new LValueContext(arrLValue, arrVarCtx, set, index);
		}
		else {
		    return arrLValue.makeMapLValue(visitor, arrVarCtx, map, memberName);
		}
	    }
	    else if (ctx instanceof CalcParser.ObjVarContext) {
		CalcParser.ObjVarContext objVarCtx = (CalcParser.ObjVarContext) ctx;
		LValueContext objLValue = getLValue(visitor, objVarCtx.var(), lValue);

		String memberName = getMemberName(visitor, objVarCtx.member());
		return objLValue.makeMapLValue(visitor, objVarCtx, memberName);
	    }
	    else if (ctx instanceof CalcParser.FunctionVarContext) {
		CalcParser.FunctionVarContext funcVarCtx = (CalcParser.FunctionVarContext) ctx;
		LValueContext funcLValue = getLValue(visitor, funcVarCtx.var(), lValue);
		Object funcObj = funcLValue.getContextObject(visitor);

		if (funcObj instanceof FunctionScope) {
		    // We are already setup to make the function call with this FunctionScope, so just do it
		    // and hope the return value is itself the function object we need to call...
		    funcObj = visitor.evaluate(funcVarCtx, funcObj);
		}

		if (funcObj == null || !(funcObj instanceof FunctionDeclaration))
		    throw new CalcExprException(funcVarCtx, "%calc#undefinedFunction", getTreeText(funcVarCtx.var()));

		FunctionDeclaration func = (FunctionDeclaration) funcObj;
		List<CalcParser.OptParamContext> params = funcVarCtx.actualParams().optParam();

		return new LValueContext(funcLValue, funcVarCtx, func.setupFunctionCall(funcVarCtx, visitor, params));
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#unknownVarCtx", ctx.getClass().getName());
	   }
	}

}
