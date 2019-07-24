package com.nuru.sq1.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Configurable
@Component
public class ReceiptProductDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<ReceiptProduct> data;

	@Autowired
    private ProductDataOnDemand productDataOnDemand;

	@Autowired
    private ReceiptDataOnDemand receiptDataOnDemand;

	public ReceiptProduct getNewTransientReceiptProduct(int index) {
        ReceiptProduct obj = new ReceiptProduct();
        setDownloadKey(obj, index);
        setProduct(obj, index);
        setReceipt(obj, index);
        return obj;
    }

	public void setDownloadKey(ReceiptProduct obj, int index) {
        String downloadKey = "downloadKey_" + index;
        obj.setDownloadKey(downloadKey);
    }

	public void setProduct(ReceiptProduct obj, int index) {
        Product product = productDataOnDemand.getRandomProduct();
        obj.setProduct(product);
    }

	public void setReceipt(ReceiptProduct obj, int index) {
        Receipt receipt = receiptDataOnDemand.getRandomReceipt();
        obj.setReceipt(receipt);
    }

	public ReceiptProduct getSpecificReceiptProduct(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        ReceiptProduct obj = data.get(index);
        Long id = obj.getId();
        return ReceiptProduct.findReceiptProduct(id);
    }

	public ReceiptProduct getRandomReceiptProduct() {
        init();
        ReceiptProduct obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return ReceiptProduct.findReceiptProduct(id);
    }

	public boolean modifyReceiptProduct(ReceiptProduct obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = ReceiptProduct.findReceiptProductEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'ReceiptProduct' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<ReceiptProduct>();
        for (int i = 0; i < 10; i++) {
            ReceiptProduct obj = getNewTransientReceiptProduct(i);
            try {
                obj.persist();
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
}
