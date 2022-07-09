/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *  History:
 *	08-Jan-2021 (rlwhitcomb)
 *	    Extracted from CalcObjectVisitor.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Move text to the resource file.
 *	20-Jan-2021 (rlwhitcomb)
 *	    Allow indexing into strings. Some renaming of parameters and variables.
 *	25-Jan-2021 (rlwhitcomb)
 *	    Add Javadoc; one more rename; one more error check on loop variables.
 *	26-Jan-2021 (rlwhitcomb)
 *	    Allow ["name"] to extract fields of map objects.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use new Intl Exception variants.
 *	17-Feb-2021 (rlwhitcomb)
 *	    Add "visitor" parameter for function evaluation.
 *	22-Feb-2021 (rlwhitcomb)
 *	    Refactor "loopvar" to "localvar".
 *	25-Mar-2021 (rlwhitcomb)
 *	    Check for string index out of bounds in getContextObject.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Implement Unicode subscripts as array indexes.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Simplify objVar parsing. Partially add "functionVar" processing.
 *	02-Jul-2021 (rlwhitcomb)
 *	    Changes for always displaying thousands separators.
 *	10-Jul-2021 (rlwhitcomb)
 *	    Implement "ignore case" for variable / member names.
 *	25-Aug-2021 (rlwhitcomb)
 *	    Implement global variables ($nn, set on command line) and do
 *	    special error handling for them.
 *	09-Sep-2021 (rlwhitcomb)
 *	    Allow interpolated strings as member names; fix potential
 *	    problems with string names having escape sequences.
 *	06-Oct-2021 (rlwhitcomb)
 *	    #24 Fully implement function parameters.
 *	07-Oct-2021 (rlwhitcomb)
 *	    Add context parameter to "toStringValue", move function call setup
 *	    to CalcObjectVisitor so it can be called from there if needed also.
 *	08-Oct-2021 (rlwhitcomb)
 *	    Error if function is undefined.
 *	14-Oct-2021 (rlwhitcomb)
 *	    Allow the "mode" option values as IDs.
 *	16-Oct-2021 (rlwhitcomb)
 *	    #33: If we have a function var context (that is a function call with parameters)
 *	    and the context object is a FunctionScope (that is another function call) then we
 *	    need to call it, and setup the context with the result.
 *	19-Oct-2021 (rlwhitcomb)
 *	    Special mode for "getContextObject" to throw if the variable/member is not defined.
 *	28-Oct-2021 (rlwhitcomb)
 *	    Changes for new predefined value paradigm.
 *	03-Nov-2021 (rlwhitcomb)
 *	    #69: New global variables "$#" and "$*".
 *	09-Nov-2021 (rlwhitcomb)
 *	    #74: Improve error messages.
 *	17-Nov-2021 (rlwhitcomb)
 *	    #96: Add "visitor" to "getContextObject" parameters so that functions can be evaluated.
 *	    Two changes to make LValues that are function results work correctly for indexes and members.
 *	31-Dec-2021 (rlwhitcomb)
 *	    #180: Change parameters to "toStringValue".
 *	21-Jan-2022 (rlwhitcomb)
 *	    #135: Add support for constant values.
 *	30-Jan-2022 (rlwhitcomb)
 *	    #229: Fix defaulting of missing actual parameters.
 *	05-Feb-2022 (rlwhitcomb)
 *	    #233: Implement calling "setValue" for SystemValue.
 *	13-Feb-2022 (rlwhitcomb)
 *	    #199: Redo the local / global variable handling.
 *	02-May-2022 (rlwhitcomb)
 *	    #68: Allow indexing by integer index value (using key list), including
 *	    negative indexes (offset from length).
 *	11-May-2022 (rlwhitcomb)
 *	    #318: Rename "evaluateFunction" to "evaluate".
 *	17-May-2022 (rlwhitcomb)
 *	    #333: Redo awkward error message.
 *	27-May-2022 (rlwhitcomb)
 *	    Move "setupFunctionCall" to FunctionDeclaration.
 *	    Make parameters final.
 *	04-Jun-2022 (rlwhitcomb)
 *	    #361: Don't access the LHS of an array object twice during dereference.
 *	23-Jun-2022 (rlwhitcomb)
 *	    #314: Turn the empty collection into an ObjectScope when necessary.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.Intl;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.MathContext;
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
	 * @param obj	The array (or string) object we're referencing into.
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
		    String newValue = CalcUtil.toStringValue(visitor, varCtx, value, false, false);
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
		map = new ObjectScope();
		putContextObject(visitor, map);
	    }
	    else if (objValue instanceof ObjectScope) {
		map = (ObjectScope) objValue;
		if (memberName == null) {
		    return new LValueContext(this, ctx, map, null);
		}
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
		    arrValue = new ObjectScope();
		    arrLValue.putContextObject(visitor, arrValue);
		}

		CalcParser.ExprContext expr = arrVarCtx.expr();

		// Okay, here the "arrValue" could be null, an array, a string, OR an object (map)
		if (arrValue != null && arrValue instanceof ObjectScope) {
		    // The "index" expression should be a string (meaning a member name)
		    // but it could be a numeric index into the key set (return from "index")
		    Object indexValue = visitor.evaluate(expr);

		    if (indexValue instanceof Number) {
			int index = CalcUtil.toIntValue(visitor, indexValue, MathContext.DECIMAL128, expr);
			return new LValueContext(arrLValue, arrVarCtx, arrValue, index);
		    }
		    else {
			String memberName = visitor.toNonNullString(expr, indexValue);
			return arrLValue.makeMapLValue(visitor, arrVarCtx, arrValue, memberName);
		    }
		}

		// By now, the object must either be null, a list, a string, or a simple value (an error)
		// but we should be able to safely evaluate the index expression as an integer
		int index;

		if (expr == null) {
		    String indexString = arrVarCtx.INDEXES().getText();
		    index = (int) indexString.charAt(0) - 0x2080;
		}
		else {
		    index = visitor.getIntValue(expr);
		}

		ArrayScope list = null;
		if (arrValue == null) {
		    list = new ArrayScope();
		    arrLValue.putContextObject(visitor, list);
		}
		else if (arrValue instanceof ArrayScope) {
		    list = (ArrayScope) arrValue;
		}
		else if (arrValue instanceof String) {
		    return new LValueContext(arrLValue, arrVarCtx, arrValue, index);
		}
		else {
		    throw new CalcExprException(arrVarCtx, "%calc#nonArrayValue", arrLValue, typeof(arrValue));
		}

		return new LValueContext(arrLValue, arrVarCtx, list, index);
	    }
	    else if (ctx instanceof CalcParser.ObjVarContext) {
		CalcParser.ObjVarContext objVarCtx = (CalcParser.ObjVarContext) ctx;
		LValueContext objLValue = getLValue(visitor, objVarCtx.var(0), lValue);

		objLValue = objLValue.makeMapLValue(visitor, objVarCtx, null);

		TerminalNode string = objVarCtx.STRING();
		if (string != null) {
		    objLValue = objLValue.makeMapLValue(visitor, objVarCtx, getStringMemberName(string.getText()));
		}
		string = objVarCtx.ISTRING();
		if (string != null) {
		    objLValue = objLValue.makeMapLValue(visitor, objVarCtx, getIStringValue(visitor, string, objVarCtx));
		}

		CalcParser.VarContext rhsVarCtx = objVarCtx.var(1);
		if (rhsVarCtx != null) {
		    if (string != null)
			objLValue = objLValue.makeMapLValue(visitor, objVarCtx, null);

		    return getLValue(visitor, rhsVarCtx, objLValue);
		}
		else
		    return objLValue;
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
		List<CalcParser.OptExprContext> exprs = funcVarCtx.actualParams().optExpr();

		return new LValueContext(funcLValue, funcVarCtx, func.setupFunctionCall(funcVarCtx, visitor, exprs));
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#unknownVarCtx", ctx.getClass().getName());
	   }
	}

}
