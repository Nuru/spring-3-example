package com.nuru.sq1.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;



import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
/**
 * Many-To-Many association map between <code>Receipt</code> and <code>Product</code> 
 * that also includes a download key specific to each association.
 * 
 * @author Jeremy Grodberg
 *
 */



@Table(name="receipt_products")
public class ReceiptProduct {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "download_key")
	private String downloadKey;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "receipt_id")
	private Receipt receipt;
	
	
	/**
	 * Creates a <code>ReceiptProduct</code> for the given objects, which must not be null.
	 * Automatically adds the created object to the correct collections and creates 
	 * the download key.
	 * @param receipt
	 * @param product
	 */
	public ReceiptProduct(Receipt receipt, Product product) {
		if (product == null)
			throw new NullPointerException("Product must not be null");
		this.product = product;
		receipt.addReceiptProduct(this);
		updateDownloadKey();
	}


	/**
	 * Updates the download key using the current <code>Receipt</code> and <code>Product</code>
	 */
	public void updateDownloadKey() {
		if ((product == null) || (receipt == null)) {
			setDownloadKey(null);
		}
		else {
			setDownloadKey(product.makeDownloadKey(receipt));
		}	
	}
	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getDownloadKey() {
		return this.downloadKey;
	}

	public void setDownloadKey(String downloadKey) {
		this.downloadKey = downloadKey;
	}

	public Receipt getReceipt() {
		return this.receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	public ReceiptProduct() {
		super();
	}

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

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@PersistenceContext
	transient EntityManager entityManager;

	public static final EntityManager entityManager() {
		EntityManager em = new ReceiptProduct().entityManager;
		if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public static long countReceiptProducts() {
		return entityManager().createQuery("SELECT COUNT(o) FROM ReceiptProduct o", Long.class).getSingleResult();
	}

	public static List<ReceiptProduct> findAllReceiptProducts() {
		return entityManager().createQuery("SELECT o FROM ReceiptProduct o", ReceiptProduct.class).getResultList();
	}

	public static ReceiptProduct findReceiptProduct(Long id) {
		if (id == null) return null;
		return entityManager().find(ReceiptProduct.class, id);
	}

	public static List<ReceiptProduct> findReceiptProductEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM ReceiptProduct o", ReceiptProduct.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
			ReceiptProduct attached = ReceiptProduct.findReceiptProduct(this.id);
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
	public ReceiptProduct merge() {
		if (this.entityManager == null) this.entityManager = entityManager();
		ReceiptProduct merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}
}
