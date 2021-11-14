package com.gachokaerick.eshop.store.domain;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Erick Gachoka
 */
@ApiModel(description = "@author Erick Gachoka")
@Table("basket_checkout")
public class BasketCheckout implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("street")
    private String street;

    @NotNull(message = "must not be null")
    @Column("city")
    private String city;

    @NotNull(message = "must not be null")
    @Column("town")
    private String town;

    @NotNull(message = "must not be null")
    @Column("country")
    private String country;

    @Column("zipcode")
    private String zipcode;

    @NotNull(message = "must not be null")
    @Column("create_time")
    private ZonedDateTime createTime;

    @NotNull(message = "must not be null")
    @Column("update_time")
    private ZonedDateTime updateTime;

    @Column("payment_status")
    private String paymentStatus;

    @Column("payer_country_code")
    private String payerCountryCode;

    @Column("payer_email")
    private String payerEmail;

    @NotNull(message = "must not be null")
    @Column("payer_name")
    private String payerName;

    @Column("payer_surname")
    private String payerSurname;

    @NotNull(message = "must not be null")
    @Column("payer_id")
    private String payerId;

    @NotNull(message = "must not be null")
    @Column("currency")
    private String currency;

    @NotNull(message = "must not be null")
    @Column("amount")
    private BigDecimal amount;

    @Column("payment_id")
    private String paymentId;

    @NotNull(message = "must not be null")
    @Column("user_login")
    private String userLogin;

    @Column("description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BasketCheckout id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return this.street;
    }

    public BasketCheckout street(String street) {
        this.setStreet(street);
        return this;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return this.city;
    }

    public BasketCheckout city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return this.town;
    }

    public BasketCheckout town(String town) {
        this.setTown(town);
        return this;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return this.country;
    }

    public BasketCheckout country(String country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public BasketCheckout zipcode(String zipcode) {
        this.setZipcode(zipcode);
        return this;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public ZonedDateTime getCreateTime() {
        return this.createTime;
    }

    public BasketCheckout createTime(ZonedDateTime createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public ZonedDateTime getUpdateTime() {
        return this.updateTime;
    }

    public BasketCheckout updateTime(ZonedDateTime updateTime) {
        this.setUpdateTime(updateTime);
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public BasketCheckout paymentStatus(String paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPayerCountryCode() {
        return this.payerCountryCode;
    }

    public BasketCheckout payerCountryCode(String payerCountryCode) {
        this.setPayerCountryCode(payerCountryCode);
        return this;
    }

    public void setPayerCountryCode(String payerCountryCode) {
        this.payerCountryCode = payerCountryCode;
    }

    public String getPayerEmail() {
        return this.payerEmail;
    }

    public BasketCheckout payerEmail(String payerEmail) {
        this.setPayerEmail(payerEmail);
        return this;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getPayerName() {
        return this.payerName;
    }

    public BasketCheckout payerName(String payerName) {
        this.setPayerName(payerName);
        return this;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerSurname() {
        return this.payerSurname;
    }

    public BasketCheckout payerSurname(String payerSurname) {
        this.setPayerSurname(payerSurname);
        return this;
    }

    public void setPayerSurname(String payerSurname) {
        this.payerSurname = payerSurname;
    }

    public String getPayerId() {
        return this.payerId;
    }

    public BasketCheckout payerId(String payerId) {
        this.setPayerId(payerId);
        return this;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BasketCheckout currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public BasketCheckout amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount.stripTrailingZeros() : null;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public BasketCheckout paymentId(String paymentId) {
        this.setPaymentId(paymentId);
        return this;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserLogin() {
        return this.userLogin;
    }

    public BasketCheckout userLogin(String userLogin) {
        this.setUserLogin(userLogin);
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getDescription() {
        return this.description;
    }

    public BasketCheckout description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasketCheckout)) {
            return false;
        }
        return id != null && id.equals(((BasketCheckout) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BasketCheckout{" +
            "id=" + getId() +
            ", street='" + getStreet() + "'" +
            ", city='" + getCity() + "'" +
            ", town='" + getTown() + "'" +
            ", country='" + getCountry() + "'" +
            ", zipcode='" + getZipcode() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", payerCountryCode='" + getPayerCountryCode() + "'" +
            ", payerEmail='" + getPayerEmail() + "'" +
            ", payerName='" + getPayerName() + "'" +
            ", payerSurname='" + getPayerSurname() + "'" +
            ", payerId='" + getPayerId() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", amount=" + getAmount() +
            ", paymentId='" + getPaymentId() + "'" +
            ", userLogin='" + getUserLogin() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
