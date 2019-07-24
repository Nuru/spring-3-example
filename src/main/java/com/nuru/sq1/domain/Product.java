package com.nuru.sq1.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
/**
 * Represents a product (SKU/product ID) available for sale in the iTunes store
 * 
 * @author Jeremy Grodberg
 *
 */
@Table(name="products", uniqueConstraints = {@UniqueConstraint(columnNames = { "remote_product_id" })})
public class Product {

    @Column(name = "remote_product_id")   
    private String remoteProductId;
    
    /**
     * Generates a download key for this product, tied to a specific receipt (purchase)
     * @param receipt The receipt for the purchase of this product
     * @return	A download key for this product unique to this receipt
     */
    public String makeDownloadKey(Receipt receipt) {
    	return getId().toString() + "-" + receipt.getExpirationTimestamp().getTime();
    }
    
    /**
     * @param remoteProductId
     * @return The product with that remote product ID
     * @throws EmptyResultDataAccessException if product not found
     */
    public static Product lookupProduct(String remoteProductId) {
    	return findProductsByRemoteProductIdEquals(remoteProductId).getSingleResult();
    }

	public String getRemoteProductId() {
        return this.remoteProductId;
    }

	public void setRemoteProductId(String remoteProductId) {
        this.remoteProductId = remoteProductId;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new Product().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countProducts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Product o", Long.class).getSingleResult();
    }

	public static List<Product> findAllProducts() {
        return entityManager().createQuery("SELECT o FROM Product o", Product.class).getResultList();
    }

	public static Product findProduct(Long id) {
        if (id == null) return null;
        return entityManager().find(Product.class, id);
    }

	public static List<Product> findProductEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Product o", Product.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Product attached = Product.findProduct(this.id);
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
    public Product merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Product merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static TypedQuery<Product> findProductsByRemoteProductIdEquals(String remoteProductId) {
        if (remoteProductId == null || remoteProductId.length() == 0) throw new IllegalArgumentException("The remoteProductId argument is required");
        EntityManager em = Product.entityManager();
        TypedQuery<Product> q = em.createQuery("SELECT o FROM Product AS o WHERE o.remoteProductId = :remoteProductId", Product.class);
        q.setParameter("remoteProductId", remoteProductId);
        return q;
    }
}
