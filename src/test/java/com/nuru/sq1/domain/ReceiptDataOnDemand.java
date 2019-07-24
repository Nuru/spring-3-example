package com.nuru.sq1.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class ReceiptDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Receipt> data;

	public Receipt getNewTransientReceipt(int index) {
        Receipt obj = new Receipt();
        setExpirationTimestamp(obj, index);
        setReceipt(obj, index);
        return obj;
    }

	public void setExpirationTimestamp(Receipt obj, int index) {
        Date expirationTimestamp = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setExpirationTimestamp(expirationTimestamp);
    }

	public void setReceipt(Receipt obj, int index) {
        String receipt = "receipt_" + index;
        if (receipt.length() > 4000) {
            receipt = receipt.substring(0, 4000);
        }
        obj.setReceipt(receipt);
    }

	public Receipt getSpecificReceipt(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Receipt obj = data.get(index);
        Long id = obj.getId();
        return Receipt.findReceipt(id);
    }

	public Receipt getRandomReceipt() {
        init();
        Receipt obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return Receipt.findReceipt(id);
    }

	public boolean modifyReceipt(Receipt obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = Receipt.findReceiptEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Receipt' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Receipt>();
        for (int i = 0; i < 10; i++) {
            Receipt obj = getNewTransientReceipt(i);
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
