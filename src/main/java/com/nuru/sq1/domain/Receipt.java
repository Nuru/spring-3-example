package com.nuru.sq1.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Index;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
/**
 * Represents a purchase at the iTunes store
 * 
 * @author Jeremy Grodberg
 *
 */
@Table(name="receipts")
public class Receipt {

    @NotNull
    @Size(max = 4000)
    @Index(name = "receipt_idx")
    private String receipt;

    @Column(name = "expiration_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date expirationTimestamp;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receipt")
    private Set<ReceiptProduct> receiptProducts = new HashSet<ReceiptProduct>();
    
    /**
     * @param appleReceipt The Apple-provided receipt string
     * @return The Receipts object corresponding to the Apple receipt, or null if none exists
     */
    public static Receipt lookupReceipt(String appleReceipt) {
    	List<Receipt> results = findReceiptsByReceiptEquals(appleReceipt).getResultList();
    	return results.size() == 0 ? null : results.get(0);
    }
    
    public void addReceiptProduct(ReceiptProduct receiptProduct) {
    	if (this.receiptProducts == null)
    		this.receiptProducts = new HashSet<ReceiptProduct>();
    	this.receiptProducts.add(receiptProduct);
    	receiptProduct.setReceipt(this);
    }
    
    public void updateDownloadKeyForRemoteProductId(String remoteProductId) {
    	for (ReceiptProduct receiptProduct : receiptProducts) {
    		if (remoteProductId.equals(receiptProduct.getProduct().getRemoteProductId())) {
    			receiptProduct.updateDownloadKey();
    		}
    	}
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public String getReceipt() {
        return this.receipt;
    }

	public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

	public Date getExpirationTimestamp() {
        return this.expirationTimestamp;
    }

	public void setExpirationTimestamp(Date expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

	public Set<ReceiptProduct> getReceiptProducts() {
        return this.receiptProducts;
    }

	public void setReceiptProducts(Set<ReceiptProduct> receiptProducts) {
        this.receiptProducts = receiptProducts;
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new Receipt().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countReceipts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Receipt o", Long.class).getSingleResult();
    }

	public static List<Receipt> findAllReceipts() {
        return entityManager().createQuery("SELECT o FROM Receipt o", Receipt.class).getResultList();
    }

	public static Receipt findReceipt(Long id) {
        if (id == null) return null;
        return entityManager().find(Receipt.class, id);
    }

	public static List<Receipt> findReceiptEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Receipt o", Receipt.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Receipt attached = Receipt.findReceipt(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public Receipt merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Receipt merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	public static TypedQuery<Receipt> findReceiptsByReceiptEquals(String receipt) {
        if (receipt == null || receipt.length() == 0) throw new IllegalArgumentException("The receipt argument is required");
        EntityManager em = Receipt.entityManager();
        TypedQuery<Receipt> q = em.createQuery("SELECT o FROM Receipt AS o WHERE o.receipt = :receipt", Receipt.class);
        q.setParameter("receipt", receipt);
        return q;
    }
}
