package com.yren.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yren.infrastructure.enums.WeChatPayApiEnum;
import com.yren.infrastructure.service.WeChatPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author ChenYu ren
 * @date 2023/8/7
 */

@Slf4j(topic = "WeChatNativePayService")
public class WeChatNativePayServiceImpl extends WeChatPayServiceImpl implements WeChatPayService {


    /**
     * 商户Native支付下单接口
     * @param description 商品描述
     * @param orderNo 商户订单号
     * @param priceFen 订单总金额，单位为分。
     * @param clientAppId 客户端AppId
     * @return 二维码链接
     */
    public String nativeCreateAdvancedOrder(String description,
                                           String orderNo,
                                           Long priceFen,
                                           String clientAppId) throws Exception {
        String url = weChatV3PayConfig.getWxBaseUrl()
                    .concat(WeChatPayApiEnum.NATIVE_PAY.getApiUrl());
        //请求URL
        HttpPost httpPost = new HttpPost(url);
        JSONObject reqData = new JSONObject();
        reqData.put("appid",clientAppId);
        reqData.put("mchid",weChatV3PayConfig.getMerchantId());
        reqData.put("description",description);
        //商户订单号
        reqData.put("out_trade_no",orderNo);
        //支付回调通知地址
        reqData.put("notify_url",weChatV3PayConfig.getPayNotifyBaseUrl().concat(weChatV3PayConfig.getPayNotifyUrl()));
        //订单金额
        JSONObject amount = new JSONObject();
        amount.put("total",priceFen);
        amount.put("currency","CNY");
        reqData.put("amount",amount);
        //请求体JsonStr
        String requestBodyStr = reqData.toJSONString();
        log.info("WeChatNativePayService nativeCreateAdvancedOrder request before orderNo -> {} url -> {}, params -> {} ",orderNo,url,requestBodyStr);
        StringEntity entity = new StringEntity(requestBodyStr,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        //完成签名并执行请求
        try (CloseableHttpResponse response = wxPayClient.execute(httpPost)) {
            //响应体
            String respBody = EntityUtils.toString(response.getEntity());
            //响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            //TODO 响应体头: Wechatpay-Timestamp
            log.info("WeChatNativePayService nativeCreateAdvancedOrder execute result orderNo ->{} code ->{} ,body ->{}",orderNo ,statusCode, respBody);
            //响应失败
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_NO_CONTENT) {
                throw new RuntimeException("预付单创建失败");
            }
            JSONObject jsonObject = JSONObject.parseObject(respBody);
            //返回支付二维码
            return jsonObject.getString("code_url");
        } catch (Exception e) {
            log.error("WeChatNativePayService nativeCreateAdvancedOrder execute errorMsg -> {},orderNo ->{}", e.getMessage(),orderNo);
            throw new RuntimeException(e);
        }
    }



}
