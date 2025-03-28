package com.yren.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ChenYu ren
 * @date 2023/8/9
 */

@ConfigurationProperties(prefix = "we-chat.pay")
@Data
public class WeChatV3PayConfig {

    /**
     * 私钥存储路径
     * 私钥在商户证书文件中(apiclient_key.pem)
     */
    private String privateKeyPath = "privatekey/apiclient_key.pem";

    /**
     * 商户Id
     */
    private String merchantId = "woshijiade";

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber = "woshijiade";

    /**
     * ApiV3密钥
     */
    private String apiV3Key = "woshijiade";


    /**
     * 微信支付服务器地址
     */
    private String wxBaseUrl = "https://api.mch.weixin.qq.com";

    /**
     * 巧智慧微信支付通知服务地址
     */
    private String payNotifyBaseUrl = "https://www.demo.com/api/";

    /**
     * 支付通知回调接口地址
     */
    private String payNotifyUrl = "/open/payNotify";

}
