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
 */
 package info.rlwhitcomb.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.TerminalNode;


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

	LValueContext(LValueContext p, CalcParser.VarContext var, Object ctxt, String nm) {
	    this.parent  = p;
	    this.varCtx  = var;
	    this.context = ctxt;
	    this.name    = nm;
	    this.index   = -1;
	}

	LValueContext(LValueContext p, CalcParser.VarContext var, Object ctxt, int idx) {
	    this.parent  = p;
	    this.varCtx  = var;
	    this.context = ctxt;
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
		Map<String, Object> obj = (Map<String, Object>)context;
		// Special checks for loop vars (not defined means we are outside the loop)
		if (name.startsWith("$")) {
		    if (!obj.containsKey(name))
		        throw new CalcExprException(varCtx, "Loop variable '%1$s' is not available outside its loop", name);
		}
		return obj.get(name);
	    }
	    else if (index >= 0) {
		List<Object> arr = (List<Object>)context;
		return arr.get(index);
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
		Map<String, Object> obj = (Map<String, Object>)context;
		obj.put(name, value);
	    }
	    else if (index >= 0) {
		List<Object> arr = (List<Object>)context;
		arr.set(index, value);
	    }
	    else {
		// Should never happen
		throw new IllegalStateException("Assignment to " + this + " without name or index.");
	    }

	    // For convenience for the assignment operators, return the value
	    return value;
	}

	@SuppressWarnings("unchecked")
	private LValueContext makeMapLValue(CalcParser.VarContext var, String memberName) {
	    Map<String, Object> obj = null;
	    Object objValue = getContextObject();
	    if (objValue != null && objValue instanceof Map) {
		obj = (Map<String, Object>)objValue;
	    }
	    else if (objValue == null) {
		obj = new HashMap<>();
		putContextObject(obj);
	    }
	    else {
		throw new CalcExprException(var, "Variable '%1$s' already has a non-object value", this);
	    }

	    if (memberName != null) {
		return new LValueContext(this, var, obj, memberName);
	    }

	    return this;
	}

	@SuppressWarnings("unchecked")
	public static LValueContext getLValue(CalcObjectVisitor visitor, CalcParser.VarContext var, LValueContext lValue) {
	    if (var instanceof CalcParser.IdVarContext) {
		CalcParser.IdVarContext idVar = (CalcParser.IdVarContext)var;
		return new LValueContext(lValue, idVar, lValue.getContextObject(), idVar.ID().getText());
	    }
	    else if (var instanceof CalcParser.LoopVarContext) {
		CalcParser.LoopVarContext loopVar = (CalcParser.LoopVarContext)var;
		return new LValueContext(lValue, loopVar, lValue.getContextObject(), loopVar.LOOPVAR().getText());
	    }
	    else if (var instanceof CalcParser.ArrVarContext) {
		CalcParser.ArrVarContext arrVar = (CalcParser.ArrVarContext)var;
		LValueContext arrLValue = getLValue(visitor, arrVar.var(), lValue);
		int index = visitor.getIntValue(arrVar.expr());

		if (index < 0)
		    throw new CalcExprException(arrVar, "Index %1$d cannot be negative", index);

		List<Object> list = null;
		Object arrValue = arrLValue.getContextObject();
		if (arrValue != null && arrValue instanceof List) {
		    list = (List<Object>)arrValue;
		}
		else if (arrValue == null) {
		    list = new ArrayList<>();
		    arrLValue.putContextObject(list);
		}
		else {
		    throw new CalcExprException(var, "Variable '%1$s' already has a non-array value", arrLValue);
		}

		// Set empty values up to the index desired
		int size = list.size();
		for (int i = size; i <= index; i++)
		    list.add(null);

		return new LValueContext(arrLValue, arrVar, list, index);
	    }
	    else if (var instanceof CalcParser.ObjVarContext) {
		CalcParser.ObjVarContext objVar = (CalcParser.ObjVarContext)var;
		LValueContext objLValue = getLValue(visitor, objVar.var(0), lValue);

		objLValue = objLValue.makeMapLValue(var, null);

		List<TerminalNode> strings = objVar.STRING();
		if (strings.size() > 0) {
		    for (TerminalNode string : strings) {
			objLValue = objLValue.makeMapLValue(var, string.getText());
		    }
		}

		CalcParser.VarContext rhsVar = objVar.var(1);
		if (rhsVar != null) {
		    if (strings.size() > 0)
			objLValue = objLValue.makeMapLValue(var, null);
		    return getLValue(visitor, rhsVar, objLValue);
		}
		else
		    return objLValue;
	    }
	    else {
		throw new CalcExprException(var, "ERROR: unknown var context subclass: %1$s", var.getClass().getName());
	   }
	}

}
