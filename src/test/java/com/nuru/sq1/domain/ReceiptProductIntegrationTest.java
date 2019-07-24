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
public class ReceiptProductIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private ReceiptProductDataOnDemand dod;

	@Test
    public void testCountReceiptProducts() {
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", dod.getRandomReceiptProduct());
        long count = ReceiptProduct.countReceiptProducts();
        Assert.assertTrue("Counter for 'ReceiptProduct' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindReceiptProduct() {
        ReceiptProduct obj = dod.getRandomReceiptProduct();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to provide an identifier", id);
        obj = ReceiptProduct.findReceiptProduct(id);
        Assert.assertNotNull("Find method for 'ReceiptProduct' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'ReceiptProduct' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllReceiptProducts() {
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", dod.getRandomReceiptProduct());
        long count = ReceiptProduct.countReceiptProducts();
        Assert.assertTrue("Too expensive to perform a find all test for 'ReceiptProduct', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<ReceiptProduct> result = ReceiptProduct.findAllReceiptProducts();
        Assert.assertNotNull("Find all method for 'ReceiptProduct' illegally returned null", result);
        Assert.assertTrue("Find all method for 'ReceiptProduct' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindReceiptProductEntries() {
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", dod.getRandomReceiptProduct());
        long count = ReceiptProduct.countReceiptProducts();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<ReceiptProduct> result = ReceiptProduct.findReceiptProductEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'ReceiptProduct' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'ReceiptProduct' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        ReceiptProduct obj = dod.getRandomReceiptProduct();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to provide an identifier", id);
        obj = ReceiptProduct.findReceiptProduct(id);
        Assert.assertNotNull("Find method for 'ReceiptProduct' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyReceiptProduct(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'ReceiptProduct' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        ReceiptProduct obj = dod.getRandomReceiptProduct();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to provide an identifier", id);
        obj = ReceiptProduct.findReceiptProduct(id);
        boolean modified =  dod.modifyReceiptProduct(obj);
        Integer currentVersion = obj.getVersion();
        ReceiptProduct merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'ReceiptProduct' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", dod.getRandomReceiptProduct());
        ReceiptProduct obj = dod.getNewTransientReceiptProduct(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'ReceiptProduct' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'ReceiptProduct' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        ReceiptProduct obj = dod.getRandomReceiptProduct();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'ReceiptProduct' failed to provide an identifier", id);
        obj = ReceiptProduct.findReceiptProduct(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'ReceiptProduct' with identifier '" + id + "'", ReceiptProduct.findReceiptProduct(id));
    }
}
