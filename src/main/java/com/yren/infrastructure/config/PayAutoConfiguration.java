package com.yren.infrastructure.config;

import com.yren.infrastructure.service.impl.WeChatMiniProgramPayServiceImpl;
import com.yren.infrastructure.service.impl.WeChatNativePayServiceImpl;
import com.yren.infrastructure.service.impl.WeChatPayServiceImpl;
import com.yren.infrastructure.utils.WeChatPayUtils;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * @author ChenYu ren
 * @date 2023/8/14
 */

@EnableConfigurationProperties({WeChatV3PayConfig.class})
@Configuration(proxyBeanMethods = false)
public class PayAutoConfiguration {

    /**
     * 获取签名验证器
     * @return
     */
    @Bean("verifier")
    public Verifier getVerifier(WeChatV3PayConfig config) throws Exception{
        PrivateKey privateKey = getPrivateKey(config.getPrivateKeyPath());
        // 获取证书管理器实例
        CertificatesManager certificatesManager = CertificatesManager.getInstance();
        // 向证书管理器增加需要自动更新平台证书的商户信息
        certificatesManager.putMerchant(config.getMerchantId(),
                new WechatPay2Credentials(config.getMerchantId(),new PrivateKeySigner(config.getMerchantSerialNumber(), privateKey)),
                config.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        // 若有多个商户号，可继续调用putMerchant添加商户信息
        // 从证书管理器中获取verifier
        Verifier verifier = certificatesManager.getVerifier(config.getMerchantId());
        return verifier;
    }

    @Bean(name = "weChatPayUtils")
    public WeChatPayUtils weChatPayUtils(WeChatV3PayConfig weChatV3PayConfig,
                                         @Qualifier("verifier") Verifier verifier){
        WeChatPayUtils weChatPayUtils = new WeChatPayUtils();
        weChatPayUtils.setWechatPayConfig(weChatV3PayConfig);
        weChatPayUtils.setVerifier(verifier);
        return weChatPayUtils;
    }

    /**
     * 获取http请求对象
     * @param verifier
     * @return
     */
    @Bean(name = "wxPayClient")
    public CloseableHttpClient getWxPayClient(@Qualifier("verifier") Verifier verifier,
                                              @Autowired WeChatV3PayConfig config){
        //获取商户私钥
        PrivateKey privateKey = getPrivateKey(config.getPrivateKeyPath());
        WechatPayHttpClientBuilder httpClientBuilder = WechatPayHttpClientBuilder.create()
                .withMerchant(config.getMerchantId(), config.getMerchantSerialNumber(), privateKey)
                .withValidator(new WechatPay2Validator(verifier));
        // ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
        return httpClientBuilder.build();
    }

//    /**
//     * 获取HttpClient，无需进行应答签名验证，跳过验签的流程
//     */
//    @Bean(name = "wxPayNoSignClient")
//    public CloseableHttpClient getWxPayNoSignClient(@Autowired WeChatV3PayConfig config){
//        //获取商户私钥
//        PrivateKey privateKey = getPrivateKey(config.getPrivateKeyPath());
//        //用于构造HttpClient
//        WechatPayHttpClientBuilder httpNoSignClientBuilder = WechatPayHttpClientBuilder.create()
//                //设置商户信息
//                .withMerchant(config.getMerchantId(), config.getMerchantSerialNumber(), privateKey)
//                //无需进行签名验证、通过withValidator((response) -> true)实现
//                .withValidator((response) -> true);
//        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
//        return httpNoSignClientBuilder.build();
//    }


    @Bean(name = "weChatMiniProgramPayServiceImpl")
    public WeChatMiniProgramPayServiceImpl weChatMiniProgramPayServiceImpl(@Qualifier("wxPayClient") CloseableHttpClient client,
                                                                           WeChatV3PayConfig weChatV3PayConfig){
        WeChatMiniProgramPayServiceImpl weChatMiniProgramPayService = new WeChatMiniProgramPayServiceImpl();
        weChatMiniProgramPayService.setWeChatV3PayConfig(weChatV3PayConfig);
        weChatMiniProgramPayService.setWxPayClient(client);
        return weChatMiniProgramPayService;
    }

    @Bean(name = "weChatNativePayService")
    public WeChatNativePayServiceImpl weChatNativePayService(@Qualifier("wxPayClient") CloseableHttpClient client,
                                                             WeChatV3PayConfig weChatV3PayConfig){
        WeChatNativePayServiceImpl weChatNativePayService = new WeChatNativePayServiceImpl();
        weChatNativePayService.setWeChatV3PayConfig(weChatV3PayConfig);
        weChatNativePayService.setWxPayClient(client);
        return weChatNativePayService;
    }

    @Bean(name = "weChatPayServiceImpl")
    public WeChatPayServiceImpl weChatPayService(@Qualifier("wxPayClient") CloseableHttpClient client,
                                                 WeChatV3PayConfig weChatV3PayConfig){
        WeChatPayServiceImpl weChatPayService = new WeChatPayServiceImpl();
        weChatPayService.setWxPayClient(client);
        weChatPayService.setWeChatV3PayConfig(weChatV3PayConfig);
        return weChatPayService;
    }

    /**
     * 获取商户的私钥文件
     * @param filename 商户证书路径
     * @return 私钥
     */
    public static PrivateKey getPrivateKey(String filename){

        /*
            ResourceUtils.getFile(filename) 来获取文件的。这是 Spring 的一个工具类，它主要用于获取 classpath 中的资源文件。
            在应用打成jar或war包之后，classpath中的资源文件会被打包进jar或war中，这时候你使用
            ResourceUtils.getFile(filename) 是无法获取到文件的，因为它不是一个独立的文件系统中的文件了，而是被打包在jar或war包内部。

            解决方案

            1. 将私钥文件放在应用的外部，然后通过文件路径来读取。例如：
            privateKeyPath = "/etc/myapp/apiclient_key.pem";
            File file = new File(privateKeyPath);
            return PemUtil.loadPrivateKey(new FileInputStream(file));
            这样你就可以将 privateKeyPath 设置为任意位置的文件路径。


            2.如果你确实需要将私钥文件打包到应用中，你可以使用 ResourceUtils.getURL(filename).openStream() 来获取文件内容，例如：
            privateKeyPath = "classpath:cert/apiclient_key.pem";
            return PemUtil.loadPrivateKey(ResourceUtils.getURL(privateKeyPath).openStream());
            这样你就可以从应用的 classpath 中读取文件内容。

            ResourceUtils.getURL(filename).openStream() 来获取资源，这是与平台无关的，所以在Windows、Linux以及其他任何支持Java的平台上都能正常使用。只要你的资源文件（在这个例子中是私钥文件）被正确的包含在了你的应用的classpath中，那么你就可以在任何平台上使用这个方法来读取资源文件的内容。
            这里需要注意的是，classpath: 是一个特殊的协议前缀，它表示资源是从classpath中获取的。当你的资源文件被打包进jar或war时，它们就位于应用的classpath中，因此你可以使用 classpath: 前缀来获取这些文件。
            因此，无论你的应用运行在Windows还是Linux，或者是其他任何操作系统上，使用方法2都不会有问题。只要你的资源文件被正确地打包进了应用，那么就可以使用这种方法来读取文件内容。
             */

        // 原始取秘钥的方法
        // File file = ResourceUtils.getFile(filename);
        // return PemUtil.loadPrivateKey(new FileInputStream(file));

        // 修改后的方法
        try {
            return PemUtil.loadPrivateKey(ResourceUtils.getURL(filename).openStream());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("私钥文件不存在", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
