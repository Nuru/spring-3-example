package com.nuru.sq1.domain;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
public class ReceiptIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private ReceiptDataOnDemand dod;

	@Test
    public void testCountReceipts() {
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", dod.getRandomReceipt());
        long count = Receipt.countReceipts();
        Assert.assertTrue("Counter for 'Receipt' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindReceipt() {
        Receipt obj = dod.getRandomReceipt();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to provide an identifier", id);
        obj = Receipt.findReceipt(id);
        Assert.assertNotNull("Find method for 'Receipt' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Receipt' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllReceipts() {
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", dod.getRandomReceipt());
        long count = Receipt.countReceipts();
        Assert.assertTrue("Too expensive to perform a find all test for 'Receipt', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Receipt> result = Receipt.findAllReceipts();
        Assert.assertNotNull("Find all method for 'Receipt' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Receipt' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindReceiptEntries() {
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", dod.getRandomReceipt());
        long count = Receipt.countReceipts();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Receipt> result = Receipt.findReceiptEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Receipt' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Receipt' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        Receipt obj = dod.getRandomReceipt();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to provide an identifier", id);
        obj = Receipt.findReceipt(id);
        Assert.assertNotNull("Find method for 'Receipt' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyReceipt(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Receipt' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        Receipt obj = dod.getRandomReceipt();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to provide an identifier", id);
        obj = Receipt.findReceipt(id);
        boolean modified =  dod.modifyReceipt(obj);
        Integer currentVersion = obj.getVersion();
        Receipt merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Receipt' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", dod.getRandomReceipt());
        Receipt obj = dod.getNewTransientReceipt(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Receipt' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Receipt' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'Receipt' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        Receipt obj = dod.getRandomReceipt();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Receipt' failed to provide an identifier", id);
        obj = Receipt.findReceipt(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Receipt' with identifier '" + id + "'", Receipt.findReceipt(id));
    }
}
