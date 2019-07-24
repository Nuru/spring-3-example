/**
 * 
 */
package com.nuru.sq1.json.iTunes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Jeremy Grodberg
 *
 */
public class TestVerifyReceiptRequest {

	private static final String RECEIPT_DATA = "foo";

	/**
	 * Test method for {@link com.nuru.sq1.json.iTunes.VerifyReceiptRequest#toJSON()}.
	 */
	@Test
	public void testToJSON() {
		VerifyReceiptRequest request = new VerifyReceiptRequest(RECEIPT_DATA);
		String json = request.toJSON();
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(json).getAsJsonObject();
		JsonElement elem = obj.get("receipt-data");
		assertEquals(RECEIPT_DATA, elem.getAsString());
	}

}
