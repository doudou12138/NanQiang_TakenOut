package edu.doudou.NanqiangTakenout.utils.mediaUtil;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import edu.doudou.NanqiangTakenout.common.CustomException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AliyunSmsUtil {

    //替换成你的阿里云Accesskey,请确保对应的用户已经开启了短信权限
    private static final String ACCESS_KEY_ID = "your id";
    private static final String ACCESS_KEY_SECRET = "your secret";

    //替换成你的签名名,和你的模版号
    private static final String LOGIN_SMS_SIGHNAME = "Sturdy";
    private static final String LOGIN_SMS_TEMPLATE_CODE = "SMS_463614939";

    private static final String END_POINT = "dysmsapi.aliyuncs.com";

    /**
     *
     * @param type
     * @param phoneNumbers
     * @param param
     */
    public static void sendMessage(MediaMsgType type,String phoneNumbers,String param)  {
        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                //将阿里云accesskey放到环境变量的方式
                //.accessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                //.accessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
                .accessKeyId(ACCESS_KEY_ID)
                .accessKeySecret(ACCESS_KEY_SECRET)
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
                                .setEndpointOverride(END_POINT)
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phoneNumbers)
                .signName(LOGIN_SMS_SIGHNAME)
                .templateCode(LOGIN_SMS_TEMPLATE_CODE)
                .templateParam("{\"code\":\""+ param +"\"}" )
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        System.out.println("验证码是: "+ sendSmsRequest.getTemplateParam());
        // Synchronously get the return value of the API request
        try {
            SendSmsResponse resp = response.get();
            System.out.println(new Gson().toJson(resp));
        }catch (ExecutionException | InterruptedException exception){
            System.err.println(exception);
            throw new CustomException("发送验证码错误");
        } finally{
            // Finally, close the client
            client.close();
        }
        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/
    }

    private static String getTemplateCode(MediaMsgType mediaMsgType){
        switch (mediaMsgType){
            case LOG_IN:
                return LOGIN_SMS_TEMPLATE_CODE;
            default:
                throw new CustomException("发送短信失败");
        }
    }

}
