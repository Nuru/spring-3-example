package com.nuru.sq1.json.iTunes;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuru.sq1.json.ValidateReceiptRequest;

public class VerifyReceiptRequest {
	private static Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
	private String receiptData;

	public VerifyReceiptRequest() {
	}

	public VerifyReceiptRequest(String receiptData) {
		if (receiptData == null)
			throw new NullPointerException("no receipt data");
		this.receiptData = receiptData;
	}
	
	public String toJSON() {
		return GSON.toJson(this);
	}
	

	public String getReceiptData() {
        return this.receiptData;
    }

	public void setReceiptData(String receiptData) {
        this.receiptData = receiptData;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
