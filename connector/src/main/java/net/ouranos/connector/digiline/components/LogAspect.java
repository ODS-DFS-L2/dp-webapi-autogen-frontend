package net.ouranos.connector.digiline.components;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * ログを記録するためのアスペクトクラス。
 * メソッドの実行前後にログを記録します。
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TrackingHeaderBean trackingHeaderBean;

	@Autowired
    private JwtUtil jwtUtil;

    @Value("${restclient.base-url}")
    private String restClientBaseUrl;

    /**
     * メソッドの実行前にログを記録します。
     *
     * @param joinPoint アスペクトの結合点
     */
	@Before("execution(* *..*.TargetDataModelApiController.*(..))")
	public void startLog(JoinPoint jp) {
        String jwt = jwtUtil.getJwtFromRequest(request);
        JSONObject payload = jwtUtil.decodeJwtPayload(jwt);
        if (payload != null) {
            // 送信元情報をペイロードから取得
            String operator_id = payload.optString("operator_id", null);
            String open_system_id = payload.optString("open_system_id", null);

            // リクエスト受信時間を取得
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String str = sdf.format(timestamp);

            // X-Trackingの取得
            UUID trackingUuid = this.trackingHeaderBean.getUUID();
            
            String logMessage = String.format(
        "API name: %s operator_id: %s open_system_id: %s sent to: %s Request receive time: %s X-Tracking: %s URI path: %s",
                jp.getSignature().getName(),
                operator_id,
                open_system_id,
                restClientBaseUrl,
                str,
                trackingUuid,
                request.getRequestURI()
            );
            
            log.info(logMessage);

        } else {
            log.warn("accessToken is null");
        }
    }

    /**
     * メソッドの実行後にログを記録します。
     *
     * @param joinPoint アスペクトの結合点
     */
    @After("execution(* *..*.TargetDataModelApiController.*(..))")
    public void endLog() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = sdf.format(timestamp);
        log.info("Request sent time: " + str);
    }

}
