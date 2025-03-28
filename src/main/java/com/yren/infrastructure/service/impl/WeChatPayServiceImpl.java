package com.yren.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yren.infrastructure.config.WeChatV3PayConfig;
import com.yren.infrastructure.service.WeChatPayService;
import com.yren.infrastructure.enums.WeChatPayApiEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * 小程序支付通用方法
 * @author ChenYu ren
 * @date 2023/8/8
 */

@Data
@Slf4j(topic = "WeChatPayServiceImpl")
public class WeChatPayServiceImpl implements WeChatPayService {

    protected CloseableHttpClient wxPayClient;


    protected WeChatV3PayConfig weChatV3PayConfig;

    @Override
    public String queryOrderInfo(String orderNo) throws Exception {
        log.info("WeChatPayService queryOrderInfo orderNo -> {}",orderNo);
        String url = weChatV3PayConfig.getWxBaseUrl()
                    .concat(String.format(WeChatPayApiEnum.QUERY_ORDER_BY_NO.getApiUrl(), orderNo))
                    .concat("?mchid=")
                    .concat(weChatV3PayConfig.getMerchantId());
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        log.info("WeChatPayService queryOrderInfo request before orderNo -> {} , url ->{}",orderNo,url);
        //完成签名并执行请求
        try (CloseableHttpResponse response = wxPayClient.execute(httpGet)) {
            //响应体
            String respBody = EntityUtils.toString(response.getEntity());
            //响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("WeChatPayService queryOrderInfo execute result orderNo ->{} code ->{} ,body ->{}",orderNo, statusCode, respBody);
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_NO_CONTENT) {
                throw new RuntimeException("订单信息查询失败");
            }
            return respBody;
        } catch (Exception e) {
            log.error("WeChatPayService queryOrderInfo execute errorMsg ->{} orderNo -> {}", e.getMessage(),orderNo);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeOrder(String orderNo) throws Exception {
        String url = weChatV3PayConfig.getWxBaseUrl()
                .concat(String.format(WeChatPayApiEnum.CLOSE_ORDER_BY_NO.getApiUrl(), orderNo));
        //创建远程请求对象
        HttpPost httpPost = new HttpPost(url);
        JSONObject requestBody = new JSONObject();
        requestBody.put("mchid", weChatV3PayConfig.getMerchantId());
        String requestBodyJsonStr = requestBody.toJSONString();
        log.info("WeChatPayService closeOrder request before orderNo ->{} url ->{}, params-> {}",orderNo,url,requestBodyJsonStr);
        //将请求参数设置到请求对象中
        StringEntity entity = new StringEntity(requestBodyJsonStr,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        //完成签名并执行请求
        try(CloseableHttpResponse response = wxPayClient.execute(httpPost)) {
            //响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200 && statusCode != 204) {
                throw new RuntimeException("订单关闭异常");
            } else {
                log.info("WeChatPayService closeOrder execute result orderNo ->{},code ->{}", orderNo, statusCode);
            }
        } catch (Exception e){
            log.error("WeChatPayService closeOrder execute errorMsg ->{} orderNo -> {}",e.getMessage(),orderNo);
            throw new RuntimeException("订单关闭异常");
        }
    }

}
