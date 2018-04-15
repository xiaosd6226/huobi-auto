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

    private String tradeType;

    private String externalId;

    private Timestamp delegateTime;

    private BigDecimal currentAmount;

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

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Timestamp getDelegateTime() {
        return delegateTime;
    }

    public void setDelegateTime(Timestamp delegateTime) {
        this.delegateTime = delegateTime;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }
}
