package io.yule.huobiauto.entity;

/**
 * Created by chensijiang on 2018/4/16 上午1:00.
 */
public interface EnumerationConstants {

    /**
     * 交易Tick日志类型：等待买入。
     */
    String tttlt_wait_buy = "tttlt_wait_buy";

    /**
     * 交易Tick日志类型：等待卖出。
     */
    String tttlt_wait_sell = "tttlt_wait_sell";

    /**
     * 交易类型：创建买入委托。
     */
    String trt_create_buy_delegate = "trt_create_buy_delegate";

    /**
     * 交易类型：创建卖出委托。
     */
    String trt_create_sell_delegate = "trt_create_sell_delegate";

    /**
     * 交易类型：买入委托完成。
     */
    String trt_buy_delegate_complete = "trt_buy_delegate_complete";

    /**
     * 交易类型：卖出委托完成。
     */
    String trt_sell_delegate_complete = "trt_sell_delegate_complete";
}
