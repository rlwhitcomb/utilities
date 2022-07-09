/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 *	Visitor for JSON parse trees.
 *
 *  History:
 *      17-Feb-2022 (rlwhitcomb)
 *	    #196: Initial coding.
 *	    Simplify by eliminating layers.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.json;

import info.rlwhitcomb.util.CharUtil;
import org.antlr.v4.runtime.ParserRuleContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Visitor for JSON parse trees that produces regular Java objects.
 */
public class JSONObjectVisitor extends JSONBaseVisitor<Object>
{
	public JSONObjectVisitor() {
	}

	@Override
	public Object visitJson(JSONParser.JsonContext ctx) {
	    return visit(ctx.entity());
	}

	@Override
	public Object visitEntity(JSONParser.EntityContext ctx) {
	    ParserRuleContext subCtx = ctx.obj();
	    if (subCtx != null)
		return visit(subCtx);

	    subCtx = ctx.arr();
	    if (subCtx != null)
		return visit(subCtx);

	    return visit(ctx.value());
	}

	@Override
	public Object visitObj(JSONParser.ObjContext ctx) {
	    JSONParser.PairListContext pairList = ctx.pairList();
	    Map<String, Object> map = new LinkedHashMap<>();

	    String id;
	    Object value;

	    for (JSONParser.PairContext pair : pairList.pair()) {
		if (pair.id() != null)
		    id = pair.id().getText();
		else
		    id = toString(pair.STRING().getText());

		value = visit(pair.entity());

		map.put(id, value);
	    }

	    return map;
	}

	@Override
	public Object visitArr(JSONParser.ArrContext ctx) {
	    JSONParser.EntityListContext entityList = ctx.entityList();
	    List<Object> list = new ArrayList<>();

	    for (JSONParser.EntityContext entity : entityList.entity()) {
		list.add(visit(entity));
	    }

	    return list;
	}

	private String toString(String textValue) {
	    String strippedValue = CharUtil.stripAnyQuotes(textValue, true);

	    return CharUtil.convertEscapeSequences(strippedValue);
	}

	@Override
	public Object visitStringValue(JSONParser.StringValueContext ctx) {
	    return toString(ctx.STRING().getText());
	}

	private Object toDecimal(String input) {
	    BigDecimal dValue = new BigDecimal(input).stripTrailingZeros();

	    if (dValue.scale() <= 0)
		return toInteger(input, 10);

	    // TODO: Needs more work with MIN_NORMAL, MIN_VALUE and MIN/MAX_EXPONENT values
	    if (dValue.abs().compareTo(MAX_FLOAT) <= 0)
		return Float.valueOf(dValue.floatValue());
	    if (dValue.abs().compareTo(MAX_DOUBLE) <= 0)
		return Double.valueOf(dValue.doubleValue());

	    return dValue;
	}

	@Override
	public Object visitNumberValue(JSONParser.NumberValueContext ctx) {
	    String textValue = ctx.NUMBER().getText();

	    return toDecimal(textValue);
	}

	@Override
	public Object visitNullValue(JSONParser.NullValueContext ctx) {
	    return null;
	}

	@Override
	public Object visitBooleanValue(JSONParser.BooleanValueContext ctx) {
	    String textValue = ctx.BOOL_CONST().getText().toLowerCase();

	    return Boolean.valueOf(textValue);
	}

	private Object toInteger(String input, int base) {
	    BigInteger iValue = new BigInteger(input, base);

	    if (iValue.compareTo(MIN_BYTE) >= 0 && iValue.compareTo(MAX_BYTE) <= 0) {
		return Byte.valueOf(iValue.byteValue());
	    }
	    if (iValue.compareTo(MIN_SHORT) >= 0 && iValue.compareTo(MAX_SHORT) <= 0) {
		return Short.valueOf(iValue.shortValue());
	    }
	    if (iValue.compareTo(MIN_INT) >= 0 && iValue.compareTo(MAX_INT) <= 0) {
		return Integer.valueOf(iValue.intValue());
	    }
	    if (iValue.compareTo(MIN_LONG) >= 0 && iValue.compareTo(MAX_LONG) <= 0) {
		return Long.valueOf(iValue.longValue());
	    }

	    return iValue;
	}

	@Override
	public Object visitBinaryValue(JSONParser.BinaryValueContext ctx) {
	    String text = ctx.BIN_CONST().getText();

	    return toInteger(text.substring(2), 2);
	}

	@Override
	public Object visitOctalValue(JSONParser.OctalValueContext ctx) {
	    String text = ctx.OCT_CONST().getText();

	    return toInteger(text.substring(1), 8);
	}

	@Override
	public Object visitHexValue(JSONParser.HexValueContext ctx) {
	    String text = ctx.HEX_CONST().getText();

	    return toInteger(text.substring(2), 16);
	}

}

