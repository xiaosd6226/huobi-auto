package io.yule.huobiauto.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by chensijiang on 2018/4/15 下午5:42.
 */
@Entity
public class TradeRecord implements Serializable {
    private static final long serialVersionUID = -7268972093503672321L;

    @Id
    private String id;

    private String taskId;

    private Timestamp createdDate;

    private String orderId;

    private String orderState;

    private String orderType;

    private BigDecimal delegateAmount;

    private BigDecimal dealAmount;

    private Timestamp delegateCreatedTime;

    private Timestamp delegateFinishedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getDelegateAmount() {
        return delegateAmount;
    }

    public void setDelegateAmount(BigDecimal delegateAmount) {
        this.delegateAmount = delegateAmount;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    public Timestamp getDelegateCreatedTime() {
        return delegateCreatedTime;
    }

    public void setDelegateCreatedTime(Timestamp delegateCreatedTime) {
        this.delegateCreatedTime = delegateCreatedTime;
    }

    public Timestamp getDelegateFinishedTime() {
        return delegateFinishedTime;
    }

    public void setDelegateFinishedTime(Timestamp delegateFinishedTime) {
        this.delegateFinishedTime = delegateFinishedTime;
    }
}
