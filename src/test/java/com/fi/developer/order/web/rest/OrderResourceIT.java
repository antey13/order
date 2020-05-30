package com.fi.developer.order.web.rest;

import com.fi.developer.order.OrderApp;
import com.fi.developer.order.config.SecurityBeanOverrideConfiguration;
import com.fi.developer.order.domain.Order;
import com.fi.developer.order.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link OrderResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, OrderApp.class })

@AutoConfigureMockMvc
@WithMockUser
public class OrderResourceIT {

    private static final String DEFAULT_DEVILIVERY_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_DEVILIVERY_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_TITLE = "BBBBBBBBBB";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity() {
        Order order = new Order()
            .deviliveryStatus(DEFAULT_DEVILIVERY_STATUS)
            .productTitle(DEFAULT_PRODUCT_TITLE);
        return order;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity() {
        Order order = new Order()
            .deviliveryStatus(UPDATED_DEVILIVERY_STATUS)
            .productTitle(UPDATED_PRODUCT_TITLE);
        return order;
    }

    @BeforeEach
    public void initTest() {
        orderRepository.deleteAll();
        order = createEntity();
    }

    @Test
    public void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // Create the Order
        restOrderMockMvc.perform(post("/api/orders").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getDeviliveryStatus()).isEqualTo(DEFAULT_DEVILIVERY_STATUS);
        assertThat(testOrder.getProductTitle()).isEqualTo(DEFAULT_PRODUCT_TITLE);
    }

    @Test
    public void createOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // Create the Order with an existing ID
        order.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc.perform(post("/api/orders").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.save(order);

        // Get all the orderList
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId())))
            .andExpect(jsonPath("$.[*].deviliveryStatus").value(hasItem(DEFAULT_DEVILIVERY_STATUS)))
            .andExpect(jsonPath("$.[*].productTitle").value(hasItem(DEFAULT_PRODUCT_TITLE)));
    }
    
    @Test
    public void getOrder() throws Exception {
        // Initialize the database
        orderRepository.save(order);

        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId()))
            .andExpect(jsonPath("$.deviliveryStatus").value(DEFAULT_DEVILIVERY_STATUS))
            .andExpect(jsonPath("$.productTitle").value(DEFAULT_PRODUCT_TITLE));
    }

    @Test
    public void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateOrder() throws Exception {
        // Initialize the database
        orderRepository.save(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        updatedOrder
            .deviliveryStatus(UPDATED_DEVILIVERY_STATUS)
            .productTitle(UPDATED_PRODUCT_TITLE);

        restOrderMockMvc.perform(put("/api/orders").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedOrder)))
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getDeviliveryStatus()).isEqualTo(UPDATED_DEVILIVERY_STATUS);
        assertThat(testOrder.getProductTitle()).isEqualTo(UPDATED_PRODUCT_TITLE);
    }

    @Test
    public void updateNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Create the Order

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc.perform(put("/api/orders").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.save(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc.perform(delete("/api/orders/{id}", order.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
