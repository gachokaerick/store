package com.gachokaerick.eshop.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gachokaerick.eshop.store.domain.enumeration.Gender;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Erick Gachoka
 */
@ApiModel(description = "@author Erick Gachoka")
@Table("buyer")
public class Buyer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Column("last_name")
    private String lastName;

    @NotNull(message = "must not be null")
    @Column("gender")
    private Gender gender;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Column("phone")
    private String phone;

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "buyer" }, allowSetters = true)
    private Set<Address> addresses = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "address", "orderItems", "payments", "buyer" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    @Column("user_id")
    private String userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Buyer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Buyer firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Buyer lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Buyer gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return this.email;
    }

    public Buyer email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Buyer phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Buyer user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        if (this.addresses != null) {
            this.addresses.forEach(i -> i.setBuyer(null));
        }
        if (addresses != null) {
            addresses.forEach(i -> i.setBuyer(this));
        }
        this.addresses = addresses;
    }

    public Buyer addresses(Set<Address> addresses) {
        this.setAddresses(addresses);
        return this;
    }

    public Buyer addAddresses(Address address) {
        this.addresses.add(address);
        address.setBuyer(this);
        return this;
    }

    public Buyer removeAddresses(Address address) {
        this.addresses.remove(address);
        address.setBuyer(null);
        return this;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setBuyer(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setBuyer(this));
        }
        this.orders = orders;
    }

    public Buyer orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public Buyer addOrders(Order order) {
        this.orders.add(order);
        order.setBuyer(this);
        return this;
    }

    public Buyer removeOrders(Order order) {
        this.orders.remove(order);
        order.setBuyer(null);
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String user) {
        this.userId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Buyer)) {
            return false;
        }
        return id != null && id.equals(((Buyer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Buyer{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", gender='" + getGender() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
