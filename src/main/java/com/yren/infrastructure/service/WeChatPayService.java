package com.yren.infrastructure.service;

/**
 * 微信支付公用方法
 * @author ChenYu ren
 * @date 2023/8/7
 */
public interface WeChatPayService  {

    /**
     * 商户订单号查询
     * @param orderNo 商户订单号
     * @return 订单信息
     * @link <a><a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_2.shtml">查询订单</a></a>
     */
   String queryOrderInfo(String orderNo) throws Exception;


    /**
     * 关闭订单
     * @param orderNo 商户订单号
     * @link <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_3.shtml">关闭订单</a>
     */
   void closeOrder(String orderNo) throws Exception;

}
