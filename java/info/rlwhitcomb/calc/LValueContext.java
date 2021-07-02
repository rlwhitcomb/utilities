/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *      Utility methods for the command-line calculator.
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
 */
 package info.rlwhitcomb.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.TerminalNode;

import info.rlwhitcomb.util.Intl;


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

	/**
	 * Construct given the base context object (which should be the global variables map).
	 *
	 * @param obj	The context object (base variables map).
	 */
	LValueContext(Object obj) {
	    this.parent  = null;
	    this.varCtx  = null;
	    this.context = obj;
	    this.name    = null;
	    this.index   = -1;
	}

	/**
	 * Construct for a <code>map.member</code> reference.
	 *
	 * @param p	The parent lvalue.
	 * @param ctx	The parser context we're working in.
	 * @param obj	The map object we're referencing into.
	 * @param nm	The name of the map member to reference.
	 */
	LValueContext(LValueContext p, CalcParser.VarContext ctx, Object obj, String nm) {
	    this.parent  = p;
	    this.varCtx  = ctx;
	    this.context = obj;
	    this.name    = nm;
	    this.index   = -1;
	}

	/**
	 * Construct for a <code>arr[idx]</code> reference.
	 *
	 * @param p	The parent lvalue.
	 * @param ctx	The parser context we're working in.
	 * @param obj	The array (or string) object we're referencing into.
	 * @param idx	The index into the object.
	 */
	LValueContext(LValueContext p, CalcParser.VarContext ctx, Object obj, int idx) {
	    this.parent  = p;
	    this.varCtx  = ctx;
	    this.context = obj;
	    this.name    = null;
	    this.index   = idx;
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
	    else if (index >= 0)
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
	 * @return The member or indexed value extracted from the base object.
	 */
	@SuppressWarnings("unchecked")
	public Object getContextObject() {
	    if (name != null) {
		Map<String, Object> map = (Map<String, Object>) context;
		// Special checks for local vars (not defined means we are outside the loop or function)
		if (name.startsWith("$")) {
		    if (!map.containsKey(name))
		        throw new CalcExprException(varCtx, "%calc#localVarNotAvail", name);
		}
		return map.get(name);
	    }
	    else if (index >= 0) {
		if (context instanceof List) {
		    List<Object> list = (List<Object>) context;
		    return list.get(index);
		}
		else if (context instanceof String) {
		    String str = (String) context;
		    if (index >= str.length()) {
			return null;
		    }
		    return str.substring(index, index + 1);
		}
		else {
		    // Should never happen
		    throw new Intl.IllegalStateException("calc#badAssign", this);
		}
	    }
	    else {
		// This should only ever be in the outermost "variables" context
		// where the context is just the variables map
		return context;
	    }
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
	 * @return	  This value (for convenience in the assign operators.
	 */
	@SuppressWarnings("unchecked")
	public Object putContextObject(final CalcObjectVisitor visitor, final Object value) {
	    if (name != null) {
		if (name.startsWith("$")) {
		    throw new CalcExprException(varCtx, "%calc#localVarNoAssign", name);
		}
		Map<String, Object> map = (Map<String, Object>) context;
		map.put(name, value);
	    }
	    else if (index >= 0) {
		if (context instanceof List) {
		    List<Object> list = (List<Object>) context;
		    list.set(index, value);
		}
		else if (context instanceof String) {
		    StringBuilder buf = new StringBuilder((String) context);
		    String newValue = CalcUtil.toStringValue(visitor, value, false, false, false, "");
		    int newLen = index + newValue.length();
		    // Ensure the builder has enough length to do the replacement
		    while (buf.length() < newLen) {
			buf.append(' ');
		    }
		    buf.replace(index, newLen, newValue);
		    this.context = buf.toString();
		    // Have to update the parent as well with the new string
		    parent.putContextObject(visitor, this.context);
		}
	    }
	    else {
		// Should never happen
		throw new Intl.IllegalStateException("calc#badAssign", this);
	    }

	    // For convenience for the assignment operators, return the value
	    return value;
	}

	/**
	 * An intermediate step used for maps with members as a string (as in <code>map."field"</code>).
	 *
	 * @param visitor	The visitor object (for function evaluation).
	 * @param ctx		The parser context we are working in.
	 * @param memberName	The (quoted) member name to use at this level.
	 * @return		The new context for the <code>map."member"</code>.
	 */
	@SuppressWarnings("unchecked")
	private LValueContext makeMapLValue(final CalcObjectVisitor visitor, final CalcParser.VarContext ctx, final String memberName) {
	    Map<String, Object> map = null;
	    Object objValue = getContextObject();
	    if (objValue == null) {
		map = new HashMap<>();
		putContextObject(visitor, map);
	    }
	    else if (objValue instanceof Map) {
		map = (Map<String, Object>) objValue;
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#nonObjectValue", this);
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
	public static LValueContext getLValue(CalcObjectVisitor visitor, CalcParser.VarContext ctx, LValueContext lValue) {
	    if (ctx instanceof CalcParser.IdVarContext) {
		CalcParser.IdVarContext idVarCtx = (CalcParser.IdVarContext) ctx;
		return new LValueContext(lValue, idVarCtx, lValue.getContextObject(), idVarCtx.ID().getText());
	    }
	    else if (ctx instanceof CalcParser.LocalVarContext) {
		CalcParser.LocalVarContext localVarCtx = (CalcParser.LocalVarContext) ctx;
		return new LValueContext(lValue, localVarCtx, lValue.getContextObject(), localVarCtx.LOCALVAR().getText());
	    }
	    else if (ctx instanceof CalcParser.ArrVarContext) {
		CalcParser.ArrVarContext arrVarCtx = (CalcParser.ArrVarContext) ctx;
		LValueContext arrLValue = getLValue(visitor, arrVarCtx.var(), lValue);
		Object arrValue = arrLValue.getContextObject();

		// Okay, here the "arrValue" could be null, an array, a string, OR an object (map)
		if (arrValue != null && arrValue instanceof Map) {
		    // The "index" expression should be a string (meaning a member name)
		    LValueContext objLValue = arrLValue.makeMapLValue(visitor, arrVarCtx, null);

		    String memberName = visitor.getStringValue(arrVarCtx.expr());
		    return objLValue.makeMapLValue(visitor, arrVarCtx, memberName);
		}

		// By now, the object must either be null, a list, a string, or a simple value (an error)
		// but we should be able to safely evaluate the index expression as an integer
		CalcParser.ExprContext expr = arrVarCtx.expr();
		int index;

		if (expr == null) {
		    String indexString = arrVarCtx.INDEXES().getText();
		    index = (int) indexString.charAt(0) - 0x2080;
		}
		else {
		    index = visitor.getIntValue(expr);
		}

		if (index < 0)
		    throw new CalcExprException(arrVarCtx, "%calc#indexNegative", index);

		List<Object> list = null;
		if (arrValue == null) {
		    list = new ArrayList<>();
		    arrLValue.putContextObject(visitor, list);
		}
		else if (arrValue instanceof List) {
		    list = (List<Object>) arrValue;
		}
		else if (arrValue instanceof String) {
		    return new LValueContext(arrLValue, arrVarCtx, arrValue, index);
		}
		else {
		    throw new CalcExprException(arrVarCtx, "%calc#nonArrayValue", arrLValue);
		}

		// Set empty values up to the index desired
		int size = list.size();
		for (int i = size; i <= index; i++)
		    list.add(null);

		return new LValueContext(arrLValue, arrVarCtx, list, index);
	    }
	    else if (ctx instanceof CalcParser.ObjVarContext) {
		CalcParser.ObjVarContext objVarCtx = (CalcParser.ObjVarContext) ctx;
		LValueContext objLValue = getLValue(visitor, objVarCtx.var(0), lValue);

		objLValue = objLValue.makeMapLValue(visitor, objVarCtx, null);

		TerminalNode string = objVarCtx.STRING();
		if (string != null) {
		    objLValue = objLValue.makeMapLValue(visitor, objVarCtx, string.getText());
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
		CalcParser.ActualParamsContext actuals = funcVarCtx.actualParams();

// ... finish
		/* ?? */ return funcLValue;
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#unknownVarCtx", ctx.getClass().getName());
	   }
	}

}
