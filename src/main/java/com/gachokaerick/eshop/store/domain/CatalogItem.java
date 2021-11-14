package com.gachokaerick.eshop.store.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@Table("catalog_item")
public class CatalogItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("price")
    private BigDecimal price;

    @Column("picture_file_name")
    private String pictureFileName;

    @Column("picture_url")
    private String pictureUrl;

    @NotNull(message = "must not be null")
    @Column("available_stock")
    private Integer availableStock;

    /**
     * Available stock at which we should reorder
     */
    @ApiModelProperty(value = "Available stock at which we should reorder", required = true)
    @NotNull(message = "must not be null")
    @Column("restock_threshold")
    private Integer restockThreshold;

    /**
     * Maximum number of units that can be in-stock at any time (due to physicial/logistical constraints in warehouses)
     */
    @ApiModelProperty(
        value = "Maximum number of units that can be in-stock at any time (due to physicial/logistical constraints in warehouses)",
        required = true
    )
    @NotNull(message = "must not be null")
    @Column("max_stock_threshold")
    private Integer maxStockThreshold;

    @Column("on_reorder")
    private Boolean onReorder;

    @Transient
    private CatalogBrand catalogBrand;

    @Transient
    private CatalogType catalogType;

    @Column("catalog_brand_id")
    private Long catalogBrandId;

    @Column("catalog_type_id")
    private Long catalogTypeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CatalogItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public CatalogItem name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public CatalogItem description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public CatalogItem price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public String getPictureFileName() {
        return this.pictureFileName;
    }

    public CatalogItem pictureFileName(String pictureFileName) {
        this.setPictureFileName(pictureFileName);
        return this;
    }

    public void setPictureFileName(String pictureFileName) {
        this.pictureFileName = pictureFileName;
    }

    public String getPictureUrl() {
        return this.pictureUrl;
    }

    public CatalogItem pictureUrl(String pictureUrl) {
        this.setPictureUrl(pictureUrl);
        return this;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getAvailableStock() {
        return this.availableStock;
    }

    public CatalogItem availableStock(Integer availableStock) {
        this.setAvailableStock(availableStock);
        return this;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getRestockThreshold() {
        return this.restockThreshold;
    }

    public CatalogItem restockThreshold(Integer restockThreshold) {
        this.setRestockThreshold(restockThreshold);
        return this;
    }

    public void setRestockThreshold(Integer restockThreshold) {
        this.restockThreshold = restockThreshold;
    }

    public Integer getMaxStockThreshold() {
        return this.maxStockThreshold;
    }

    public CatalogItem maxStockThreshold(Integer maxStockThreshold) {
        this.setMaxStockThreshold(maxStockThreshold);
        return this;
    }

    public void setMaxStockThreshold(Integer maxStockThreshold) {
        this.maxStockThreshold = maxStockThreshold;
    }

    public Boolean getOnReorder() {
        return this.onReorder;
    }

    public CatalogItem onReorder(Boolean onReorder) {
        this.setOnReorder(onReorder);
        return this;
    }

    public void setOnReorder(Boolean onReorder) {
        this.onReorder = onReorder;
    }

    public CatalogBrand getCatalogBrand() {
        return this.catalogBrand;
    }

    public void setCatalogBrand(CatalogBrand catalogBrand) {
        this.catalogBrand = catalogBrand;
        this.catalogBrandId = catalogBrand != null ? catalogBrand.getId() : null;
    }

    public CatalogItem catalogBrand(CatalogBrand catalogBrand) {
        this.setCatalogBrand(catalogBrand);
        return this;
    }

    public CatalogType getCatalogType() {
        return this.catalogType;
    }

    public void setCatalogType(CatalogType catalogType) {
        this.catalogType = catalogType;
        this.catalogTypeId = catalogType != null ? catalogType.getId() : null;
    }

    public CatalogItem catalogType(CatalogType catalogType) {
        this.setCatalogType(catalogType);
        return this;
    }

    public Long getCatalogBrandId() {
        return this.catalogBrandId;
    }

    public void setCatalogBrandId(Long catalogBrand) {
        this.catalogBrandId = catalogBrand;
    }

    public Long getCatalogTypeId() {
        return this.catalogTypeId;
    }

    public void setCatalogTypeId(Long catalogType) {
        this.catalogTypeId = catalogType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CatalogItem)) {
            return false;
        }
        return id != null && id.equals(((CatalogItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CatalogItem{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", pictureFileName='" + getPictureFileName() + "'" +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", availableStock=" + getAvailableStock() +
            ", restockThreshold=" + getRestockThreshold() +
            ", maxStockThreshold=" + getMaxStockThreshold() +
            ", onReorder='" + getOnReorder() + "'" +
            "}";
    }
}
