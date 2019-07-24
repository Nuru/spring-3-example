/**
 * 
 */
package com.nuru.sq1.json.iTunes;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jeremy Grodberg
 *
 */
public class TestVerifyReceiptResponse {
	private final static String JSON_SUCCESS_PRODUCT_ID = "com.zoogis.pettalk.309";
	private final static String JSON_DATA_SUCCESS = 
			"{\"receipt\":{\"item_id\":\"413309881\", " +
			"\"original_transaction_id\":\"1000000001279003\", " +
			"\"bvrs\":\"1.0.1512\", \"product_id\":\"" + JSON_SUCCESS_PRODUCT_ID + "\", " +
			"\"purchase_date\":\"2011-01-05 22:47:05 Etc/GMT\", \"quantity\":\"1\", " +
			"\"bid\":\"com.zoogis.pettalk\", " +
			"\"original_purchase_date\":\"2011-01-05 22:47:05 Etc/GMT\", " +
			"\"transaction_id\":\"1000000001279003\"}, \"status\":0}";
	private final static String JSON_DATA_FAIL = 
			"{\"status\":21002, \"exception\":\"com.apple.jingle.foundation.exceptions.MZCodedException\"}";

	/**
	 * Test method for {@link com.nuru.sq1.json.iTunes.VerifyReceiptResponse#fromJSON(java.lang.String)}.
	 */
	@Test
	public void testFromJSON() {
		VerifyReceiptResponse response = VerifyReceiptResponse.fromJSON(JSON_DATA_SUCCESS);
		assertEquals(0, response.getStatus());
		assertEquals(JSON_SUCCESS_PRODUCT_ID, response.getProductId());
		response = VerifyReceiptResponse.fromJSON(JSON_DATA_FAIL);
		assertFalse("Failed request converted to zero status", 0 == response.getStatus());
		assertNull(response.getProductId());
	}

}
