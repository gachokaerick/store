package com.gachokaerick.eshop.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Erick Gachoka
 */
@ApiModel(description = "@author Erick Gachoka")
@Table("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("product_name")
    private String productName;

    @NotNull(message = "must not be null")
    @Column("picture_url")
    private String pictureUrl;

    @NotNull(message = "must not be null")
    @Column("unit_price")
    private BigDecimal unitPrice;

    @NotNull(message = "must not be null")
    @Column("discount")
    private BigDecimal discount;

    @NotNull(message = "must not be null")
    @Column("units")
    private Integer units;

    @NotNull(message = "must not be null")
    @Column("product_id")
    private Long productId;

    @Transient
    @JsonIgnoreProperties(value = { "address", "orderItems", "payments", "buyer" }, allowSetters = true)
    private Order order;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return this.productName;
    }

    public OrderItem productName(String productName) {
        this.setProductName(productName);
        return this;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPictureUrl() {
        return this.pictureUrl;
    }

    public OrderItem pictureUrl(String pictureUrl) {
        this.setPictureUrl(pictureUrl);
        return this;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public OrderItem unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice.stripTrailingZeros() : null;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public OrderItem discount(BigDecimal discount) {
        this.setDiscount(discount);
        return this;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount != null ? discount.stripTrailingZeros() : null;
    }

    public Integer getUnits() {
        return this.units;
    }

    public OrderItem units(Integer units) {
        this.setUnits(units);
        return this;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Long getProductId() {
        return this.productId;
    }

    public OrderItem productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public OrderItem order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long order) {
        this.orderId = order;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        return id != null && id.equals(((OrderItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + getId() +
            ", productName='" + getProductName() + "'" +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", unitPrice=" + getUnitPrice() +
            ", discount=" + getDiscount() +
            ", units=" + getUnits() +
            ", productId=" + getProductId() +
            "}";
    }
}
