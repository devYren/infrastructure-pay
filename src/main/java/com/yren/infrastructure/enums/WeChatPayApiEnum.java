package com.yren.infrastructure.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信支付API接口枚举类
 * @author ChenYu ren
 * @date 2023/8/7
 */

@AllArgsConstructor
@Getter
public enum WeChatPayApiEnum {

    /**
     * JSAPI下单
     * doc:商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易会话标识后
     *     再按Native、JSAPI、APP等不同场景生成交易串调起支付。
     * @link <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_1.shtml">JSAPI下单</a>
     */
    JSAPI_PAY("/v3/pay/transactions/jsapi","POST"),

    /**
     * Native下单API
     * 商户Native支付下单接口，微信后台系统返回链接参数code_url，
     * 商户后台系统将code_url值生成二维码图片，用户使用微信客户端扫码后发起支付。
     * @link <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_1.shtml">Native下单API</a>
     */
    NATIVE_PAY("/v3/pay/transactions/native","POST"),

    /**
     * 查询订单
     * 商户可以通过查询订单接口主动查询订单状态，完成下一步的业务逻辑。
     * @link <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_2.shtml">查询订单</a>
     */
    QUERY_ORDER_BY_NO("/v3/pay/transactions/out-trade-no/%s","GET"),

    /**
     * 关闭订单
     * @link  <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_3.shtml">关闭订单API</a>
     */
    CLOSE_ORDER_BY_NO("/v3/pay/transactions/out-trade-no/%s/close","POST");


    /**
     * 接口地址
     */
    private final String apiUrl;

    /**
     * 请求方式
     */
    private final String method;
}
