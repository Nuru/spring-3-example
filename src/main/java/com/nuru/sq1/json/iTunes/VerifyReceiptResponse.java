package com.nuru.sq1.json.iTunes;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VerifyReceiptResponse {
	private static Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	public static VerifyReceiptResponse fromJSON(String json) {
		return GSON.fromJson(json, VerifyReceiptResponse.class);
	}
	private int status;
	private Receipt receipt;
	
	public VerifyReceiptResponse() {
		status = -1;
		receipt = null;
	}

	public boolean isValid() {
		return status == 0;
	}
	
	public int getStatus() {
		return status;
	}

	public static class Receipt {
		String productId;
		
		public Receipt() {
			productId = null;
		}

		public String getProductId() {
			return productId;
		}
		
	}

	public String getProductId() {
		return receipt == null ? null : receipt.getProductId();
	}
}
