package com.nuru.sq1.json;

import java.util.ArrayList;
import java.util.Set;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuru.sq1.domain.ReceiptProduct;
import com.nuru.sq1.json.ValidateReceiptResponse.DownloadKey;

public class ValidateReceiptResponse {
	private static Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

	//  Would prefer to use Enums, but they cause unnecessary complication with JSON
	public static final String STATUS_OK = "ok";
	public static final String STATUS_FAIL = "fail";
	private static final DownloadKey[] DOWNLOAD_KEY_ARRAY_TYPE = new DownloadKey[0];	
	
	private String status = STATUS_FAIL;
	private DownloadKey[] products = null;
	
	public ValidateReceiptResponse() {
	}	
	
	public ValidateReceiptResponse(Set<ReceiptProduct> receiptProducts) {
		if (receiptProducts == null)
			return;
		ArrayList<DownloadKey> productList = new ArrayList<DownloadKey>(receiptProducts.size());
		for (ReceiptProduct rp : receiptProducts) {
			productList.add(new DownloadKey(rp.getProduct().getRemoteProductId(), rp.getDownloadKey()));
		}
		products = productList.toArray(DOWNLOAD_KEY_ARRAY_TYPE);
		status = STATUS_OK;		
	}

	public String toJSON() {
		return GSON.toJson(this);
	}

	public static class DownloadKey {
		private String productId;
		private String downloadKey;
		
		public DownloadKey() {	
		}
		
		private DownloadKey(String productId, String downloadKey) {
			super();
			this.productId = productId;
			this.downloadKey = downloadKey;
		}
		
		public String getProductId() {
			return productId;
		}
		public void setProductId(String productId) {
			this.productId = productId;
		}
		public String getDownloadKey() {
			return downloadKey;
		}
		public void setDownloadKey(String downloadKey) {
			this.downloadKey = downloadKey;
		}
		@Override
		public String toString() {
			return "[product-id : " + productId + ", download-key :"
					+ downloadKey + "]";
		}
		
		
	}
}
