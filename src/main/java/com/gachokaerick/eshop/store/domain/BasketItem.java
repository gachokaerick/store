package com.gachokaerick.eshop.store.domain;

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
@Table("basket_item")
public class BasketItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("product_id")
    private Long productId;

    @NotNull(message = "must not be null")
    @Column("product_name")
    private String productName;

    @NotNull(message = "must not be null")
    @Column("unit_price")
    private BigDecimal unitPrice;

    @NotNull(message = "must not be null")
    @Column("old_unit_price")
    private BigDecimal oldUnitPrice;

    @NotNull(message = "must not be null")
    @Column("quantity")
    private Integer quantity;

    @NotNull(message = "must not be null")
    @Column("picture_url")
    private String pictureUrl;

    @NotNull(message = "must not be null")
    @Column("user_login")
    private String userLogin;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BasketItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return this.productId;
    }

    public BasketItem productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public BasketItem productName(String productName) {
        this.setProductName(productName);
        return this;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public BasketItem unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice.stripTrailingZeros() : null;
    }

    public BigDecimal getOldUnitPrice() {
        return this.oldUnitPrice;
    }

    public BasketItem oldUnitPrice(BigDecimal oldUnitPrice) {
        this.setOldUnitPrice(oldUnitPrice);
        return this;
    }

    public void setOldUnitPrice(BigDecimal oldUnitPrice) {
        this.oldUnitPrice = oldUnitPrice != null ? oldUnitPrice.stripTrailingZeros() : null;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public BasketItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPictureUrl() {
        return this.pictureUrl;
    }

    public BasketItem pictureUrl(String pictureUrl) {
        this.setPictureUrl(pictureUrl);
        return this;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getUserLogin() {
        return this.userLogin;
    }

    public BasketItem userLogin(String userLogin) {
        this.setUserLogin(userLogin);
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasketItem)) {
            return false;
        }
        return id != null && id.equals(((BasketItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BasketItem{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", productName='" + getProductName() + "'" +
            ", unitPrice=" + getUnitPrice() +
            ", oldUnitPrice=" + getOldUnitPrice() +
            ", quantity=" + getQuantity() +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", userLogin='" + getUserLogin() + "'" +
            "}";
    }
}
