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

	LValueContext(Object ctxt) {
	    this.parent  = null;
	    this.varCtx  = null;
	    this.context = ctxt;
	    this.name    = null;
	    this.index   = -1;
	}

	LValueContext(LValueContext p, CalcParser.VarContext ctx, Object obj, String nm) {
	    this.parent  = p;
	    this.varCtx  = ctx;
	    this.context = obj;
	    this.name    = nm;
	    this.index   = -1;
	}

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

	@SuppressWarnings("unchecked")
	public Object getContextObject() {
	    if (name != null) {
		Map<String, Object> map = (Map<String, Object>) context;
		// Special checks for loop vars (not defined means we are outside the loop)
		if (name.startsWith("$")) {
		    if (!map.containsKey(name))
		        throw new CalcExprException(varCtx, "%calc#loopVarNotAvail", name);
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
		    return str.substring(index, index + 1);
		}
		else {
		    // Should never happen
		    throw new IllegalStateException(Intl.formatKeyString("%calc#badAssign", this));
		}
	    }
	    else {
		// This should only ever be in the outermost "variables" context
		// where the context is just the variables map
		return context;
	    }
	}

	@SuppressWarnings("unchecked")
	public Object putContextObject(Object value) {
	    if (name != null) {
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
		    String newValue = CalcUtil.toStringValue(value, false, false, "");
		    int newLen = index + newValue.length();
		    // Ensure the builder has enough length to do the replacement
		    while (buf.length() < newLen) {
			buf.append(' ');
		    }
		    buf.replace(index, newLen, newValue);
		    this.context = buf.toString();
		    // Have to update the parent as well with the new string
		    parent.putContextObject(this.context);
		}
	    }
	    else {
		// Should never happen
		throw new IllegalStateException(Intl.formatKeyString("%calc#badAssign", this));
	    }

	    // For convenience for the assignment operators, return the value
	    return value;
	}

	@SuppressWarnings("unchecked")
	private LValueContext makeMapLValue(CalcParser.VarContext ctx, String memberName) {
	    Map<String, Object> map = null;
	    Object objValue = getContextObject();
	    if (objValue == null) {
		map = new HashMap<>();
		putContextObject(map);
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

	@SuppressWarnings("unchecked")
	public static LValueContext getLValue(CalcObjectVisitor visitor, CalcParser.VarContext ctx, LValueContext lValue) {
	    if (ctx instanceof CalcParser.IdVarContext) {
		CalcParser.IdVarContext idVarCtx = (CalcParser.IdVarContext) ctx;
		return new LValueContext(lValue, idVarCtx, lValue.getContextObject(), idVarCtx.ID().getText());
	    }
	    else if (ctx instanceof CalcParser.LoopVarContext) {
		CalcParser.LoopVarContext loopVarCtx = (CalcParser.LoopVarContext) ctx;
		return new LValueContext(lValue, loopVarCtx, lValue.getContextObject(), loopVarCtx.LOOPVAR().getText());
	    }
	    else if (ctx instanceof CalcParser.ArrVarContext) {
		CalcParser.ArrVarContext arrVarCtx = (CalcParser.ArrVarContext) ctx;
		LValueContext arrLValue = getLValue(visitor, arrVarCtx.var(), lValue);
		int index = visitor.getIntValue(arrVarCtx.expr());

		if (index < 0)
		    throw new CalcExprException(arrVarCtx, "%calc#indexNegative", index);

		List<Object> list = null;
		Object arrValue = arrLValue.getContextObject();
		if (arrValue == null) {
		    list = new ArrayList<>();
		    arrLValue.putContextObject(list);
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

		objLValue = objLValue.makeMapLValue(objVarCtx, null);

		List<TerminalNode> strings = objVarCtx.STRING();
		if (strings.size() > 0) {
		    for (TerminalNode string : strings) {
			objLValue = objLValue.makeMapLValue(objVarCtx, string.getText());
		    }
		}

		CalcParser.VarContext rhsVarCtx = objVarCtx.var(1);
		if (rhsVarCtx != null) {
		    if (strings.size() > 0)
			objLValue = objLValue.makeMapLValue(objVarCtx, null);
		    return getLValue(visitor, rhsVarCtx, objLValue);
		}
		else
		    return objLValue;
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#unknownVarCtx", ctx.getClass().getName());
	   }
	}

}
