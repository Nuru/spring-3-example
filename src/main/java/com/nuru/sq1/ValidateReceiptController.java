/**
 * 
 */
package com.nuru.sq1;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.nuru.sq1.domain.Product;
import com.nuru.sq1.domain.ReceiptProduct;
import com.nuru.sq1.domain.Receipt;
import com.nuru.sq1.json.ValidateReceiptRequest;
import com.nuru.sq1.json.ValidateReceiptResponse;
import com.nuru.sq1.json.iTunes.VerifyReceiptResponse;

/**
 * Implements the validate-receipt service.
 * 
 * @author Jeremy Grodberg
 *
 */

@Controller
@Transactional
@RequestMapping("/validate-receipt")
public class ValidateReceiptController {
	private static Log log =  LogFactory.getLog(ValidateReceiptController.class);
	
	// These should be read from configuration
	private static final String ITUNES_URL = "https://sandbox.itunes.apple.com/verifyReceipt"; 
	private static final long VALID_SECS = 180 * 1000;
	
	@RequestMapping(method = RequestMethod.POST)
	public void handle(@RequestBody String body, HttpServletResponse response) throws IOException {
		log.debug("Request starts: " + body.substring(0, 40));
		ValidateReceiptRequest request = ValidateReceiptRequest.fromJSON(body);
		
		Receipt receipt = Receipt.lookupReceipt(request.getReceiptData());
		
		Set<ReceiptProduct> receiptProducts = null;
		if ((receipt != null) && receipt.getExpirationTimestamp().after(new Date())) {
			receiptProducts = receipt.getReceiptProducts();
		}
		else {
			//
			// Make the call to the iTunes store
			//
			
			// Create the request helper
			RestTemplate restTemplate = new RestTemplate();
			
			// Set the request content-type to application/json
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Set the request body 
			HttpEntity<String> entity = 
					new HttpEntity<String>(request.makeVerifyReceiptRequestJSON(),headers);
			// Make the request, read the response
			try {
				String iRespStr = restTemplate.postForObject(ITUNES_URL, entity, String.class);
				log.debug("iTunes says: " + iRespStr);
				VerifyReceiptResponse iResp = VerifyReceiptResponse.fromJSON(iRespStr);
				if (iResp.isValid()) {
					Date expires = new Date(System.currentTimeMillis() + VALID_SECS);
					if (receipt != null) {
						receipt.setExpirationTimestamp(expires);
						receipt.updateDownloadKeyForRemoteProductId(iResp.getProductId());
						receiptProducts = receipt.getReceiptProducts();
					}
					else {
						receiptProducts = saveNewReceipt(request.getReceiptData(), iResp, expires);
					}
				}
			}
			catch (Exception e) {
				log.error("Error validating receipt", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				receiptProducts = null;
			}
		}
		
		ValidateReceiptResponse responseObject = new ValidateReceiptResponse(receiptProducts);
		response.setContentType("text/json");
		Writer writer = response.getWriter();
		writer.write(responseObject.toJSON());
	}
	
	private Set<ReceiptProduct> saveNewReceipt(String receiptData, VerifyReceiptResponse iReceipt, Date expires) {
		Product product = Product.lookupProduct(iReceipt.getProductId());
		
		Receipt receipt = new Receipt();
		receipt.setReceipt(receiptData);
		receipt.setExpirationTimestamp(expires);
		
		ReceiptProduct rp = new ReceiptProduct(receipt, product);
		receipt.persist();
		
		return receipt.getReceiptProducts();
	}

	/**
	 * Called if the request is form-urlencoded.  Decodes the request and passes it along
	 * to the main handler.
	 * @param body
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, headers ="Content-type=application/x-www-form-urlencoded")
	public void handleEncoded(@RequestBody String body, HttpServletResponse response) throws IOException {
		log.debug("Decoding: " + body.substring(0, 40));
		handle(java.net.URLDecoder.decode(body,"UTF-8"), response);
	}
		
}
