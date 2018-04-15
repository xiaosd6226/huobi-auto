package io.yule.huobiauto.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by chensijiang on 2018/4/15 下午5:38.
 */
@Entity
public class TradeTask implements Serializable {
    private static final long serialVersionUID = -7812957286352275581L;

    @Id
    private String id;

    private Timestamp createdDate;

    private String taskName;

    private String symbol;

    private BigDecimal initAmount;

    private Integer tickSeconds;

    private BigDecimal priceChangeThresholdPeriodPercent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getInitAmount() {
        return initAmount;
    }

    public void setInitAmount(BigDecimal initAmount) {
        this.initAmount = initAmount;
    }

    public Integer getTickSeconds() {
        return tickSeconds;
    }

    public void setTickSeconds(Integer tickSeconds) {
        this.tickSeconds = tickSeconds;
    }

    public BigDecimal getPriceChangeThresholdPeriodPercent() {
        return priceChangeThresholdPeriodPercent;
    }

    public void setPriceChangeThresholdPeriodPercent(BigDecimal priceChangeThresholdPeriodPercent) {
        this.priceChangeThresholdPeriodPercent = priceChangeThresholdPeriodPercent;
    }
}
