package com.fi.developer.order.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Order.
 */
@Document(collection = "order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("devilivery_status")
    private String deviliveryStatus;

    @Field("product_title")
    private String productTitle;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviliveryStatus() {
        return deviliveryStatus;
    }

    public Order deviliveryStatus(String deviliveryStatus) {
        this.deviliveryStatus = deviliveryStatus;
        return this;
    }

    public void setDeviliveryStatus(String deviliveryStatus) {
        this.deviliveryStatus = deviliveryStatus;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public Order productTitle(String productTitle) {
        this.productTitle = productTitle;
        return this;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", deviliveryStatus='" + getDeviliveryStatus() + "'" +
            ", productTitle='" + getProductTitle() + "'" +
            "}";
    }
}
