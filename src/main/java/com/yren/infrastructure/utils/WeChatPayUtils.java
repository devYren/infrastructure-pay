package com.yren.infrastructure.utils;

import com.alibaba.fastjson.JSONObject;
import com.yren.infrastructure.config.PayAutoConfiguration;
import com.yren.infrastructure.config.WeChatV3PayConfig;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * 微信支付工具类
 * @author ChenYu ren
 * @date 2023/8/9
 */

@Slf4j(topic = "WeChatPayUtils")
@Data
public class WeChatPayUtils {

    /**
     * 签名方式
     */
    public static final String SIGN_TYPE = "RSA";

    public static final String WX_NONCE_HEAD = "Wechatpay-Nonce";
    public static final String WX_TIMESTAMP_HEAD = "Wechatpay-Timestamp";
    public static final String WX_SIGNATURE_HEAD = "Wechatpay-Signature";
    public static final String WX_SERIAL_HEAD = "Wechatpay-Serial";

    private WeChatV3PayConfig wechatPayConfig;

    private Verifier verifier;


    /**
     * 获取小程序调起支付API所需请求参数
     * @param appId 小程序应用appId
     * @param prepayId 预支付交易会话标识
     * @return 小程序调起支付API所需请求参数
     */
    public JSONObject getWxMiniProgramRequestPayment(String appId,
                                                     String prepayId,
                                                     String orderNo) throws Exception {
        log.info("WeChatPayUtils getWxMiniProgramRequestPayment requestParam appId -> {} , prepayId -> {} ,orderNo -> {}",
                                                                                                            appId,
                                                                                                            prepayId,
                                                                                                            orderNo);
        //时间戳(秒级)
        long epochSecond = Instant.now().getEpochSecond();
        //32位随机字符串
        String nonceStr = UUID.randomUUID().toString().replaceAll("-","");
        //订单详情扩展字符串
        String packageStr ="prepay_id=".concat(prepayId);
        //调起小程序调起支付API所需的参数
        JSONObject requestBodyParam = new JSONObject();
        requestBodyParam.put("appId",appId);
        requestBodyParam.put("timeStamp",epochSecond);
        requestBodyParam.put("nonceStr",nonceStr);
        requestBodyParam.put("package",packageStr);
        requestBodyParam.put("signType",SIGN_TYPE);
        requestBodyParam.put("orderNo",orderNo);
        requestBodyParam.put("paySign",buildWxMiniProgramPaySignature(appId,
                epochSecond,
                nonceStr,
                packageStr));
        log.info("WeChatPayUtils getWxMiniProgramRequestPayment requestResult  -> {}",requestBodyParam.toJSONString());
        return requestBodyParam;
    }

    /**
     * 回调通知的验签与解密
     * @param nonce 请求头Wechatpay-Nonce
     * @param timestamp 请求头Wechatpay-Timestamp
     * @param signature 请求头Wechatpay-Signature
     * @param body 请求体
     * @return 明文请求报文
     */
    public String payCallBackSignatureAndDecrypt(String nonce,
                                                 String timestamp,
                                                 String signature,
                                                 String serial,
                                                 String body){
        log.info("WeChatPayUtils payCallBackSignatureAndDecrypt nonce -> {} , timestamp -> {},signature-> {} ,serial ->{} , encryptionBody->{}",
                                                                                                        nonce,
                                                                                                        timestamp,
                                                                                                        signature,
                                                                                                        serial,
                                                                                                        body);
        try {
            // 构建request，传入必要参数
            NotificationRequest request = new NotificationRequest.Builder()
                    .withSerialNumber(serial)
                    .withNonce(nonce)
                    .withTimestamp(timestamp)
                    .withSignature(signature)
                    .withBody(body)
                    .build();
            NotificationHandler handler = new NotificationHandler(verifier, wechatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
            // 验签和解析请求体
            Notification notification = handler.parse(request);
            log.info("WeChatPayUtils payCallBackSignatureAndDecrypt body plaintext -> {}", notification);
            return notification.toString();
        }catch (Exception e){
            log.error("WeChatPayUtils payCallBackSignatureAndDecrypt errorMsg -> {}",e.getMessage());
            return null;
        }
    }

    /**
     * 构建微信小程序支付签名
     * @param appId 小程序应用appId
     * @param timestamp 秒级时间戳
     * @param nonceStr 随机字符串
     * @param packageStr 预支付交易会话标识
     * @return 微信小程序支付签名
     */
    private String buildWxMiniProgramPaySignature(String appId,
                                                  long timestamp,
                                                  String nonceStr,
                                                  String packageStr) throws Exception {
        try {
            StringBuilder paySignStr = new StringBuilder();
            // 应用id
            paySignStr.append(appId)
                    .append("\n");
            // 支付签名时间戳
            paySignStr.append(timestamp)
                    .append("\n");
            // 随机字符串
            paySignStr.append(nonceStr)
                    .append("\n");
            // 预支付交易会话ID
            paySignStr.append(packageStr)
                    .append("\n");
            // 签名
            Signature sign = Signature.getInstance("SHA256withRSA");
            // 获取商户私钥并进行签名
            PrivateKey privateKey = PayAutoConfiguration.getPrivateKey(wechatPayConfig.getPrivateKeyPath());
            sign.initSign(privateKey);
            sign.update(paySignStr.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sign.sign());
        }catch (Exception e){
            log.error("WeChatPayUtils buildWxMiniProgramPaySignature buildWxMiniProgramPaySignature ex -> {} , failMessage ->{}",e,e.getMessage());
            throw new RuntimeException("构建微信小程序支付签名失败");
        }
    }




}
