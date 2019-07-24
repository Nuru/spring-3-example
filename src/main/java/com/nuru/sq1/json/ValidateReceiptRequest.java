package com.nuru.sq1.json;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuru.sq1.json.iTunes.VerifyReceiptRequest;

public class ValidateReceiptRequest {
	private static Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
	
	public static ValidateReceiptRequest fromJSON(String json) {
		return GSON.fromJson(json, ValidateReceiptRequest.class);
	}

	private String receiptData;
	
	public ValidateReceiptRequest() {
		receiptData = null;
	}

	public ValidateReceiptRequest(String receiptData) {
		this.receiptData = receiptData;
	}
	
	public String makeVerifyReceiptRequestJSON() {
		return new VerifyReceiptRequest(receiptData).toJSON();
	}


	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public String getReceiptData() {
        return this.receiptData;
    }

	public void setReceiptData(String receiptData) {
        this.receiptData = receiptData;
    }
}
