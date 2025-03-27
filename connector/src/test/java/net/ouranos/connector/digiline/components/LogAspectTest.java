package net.ouranos.connector.digiline.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


import java.util.UUID;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

import org.aspectj.lang.JoinPoint;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;

/**
 * {@code LogAspect} クラスの単体テストを行うためのテストクラス。
 */
class LogAspectTest {

    @InjectMocks
    private LogAspect logAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;
      // 引数検証の為のArgumentCaptor、LoggingEventを検証する
    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;  

    @Mock
    private TrackingHeaderBean trackingHeaderBean;

      // モック化されたAppender
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    /**
     * テストのセットアップを行います。
     * モックオブジェクトの初期化を行います。
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * {@code startLog} メソッドのテストを行います。
     * ログが正しく記録されることを検証します。
     */
    @Test
    void testStartLog() {
        Logger logger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        // JoinPointのメソッドの戻り値を指定
        Mockito.when(joinPoint.getSignature()).thenReturn(methodSignature);

        // MethodSignatureのメソッドの戻り値を指定
        Mockito.when(methodSignature.getName()).thenReturn("testMethod");

        // loggerのログ出力先をモック化する
        logger.addAppender(mockAppender);

        // JwtUtilのモックメソッドの戻り値を指定
        String mockJwt = "mockJwt";
        JSONObject mockPayload = new JSONObject();
        when(jwtUtil.getJwtFromRequest(request)).thenReturn(mockJwt);
        when(jwtUtil.decodeJwtPayload(mockJwt)).thenReturn(mockPayload);

        // TrackingHeaderBeanのモックの戻り値を指定
        UUID mockUuid = UUID.randomUUID();
        when(trackingHeaderBean.getUUID()).thenReturn(mockUuid);

        // メソッドを実行
        logAspect.startLog(joinPoint);
        
        verify(jwtUtil).getJwtFromRequest(request);
        verify(jwtUtil).decodeJwtPayload(mockJwt);
        verify(joinPoint.getSignature(), times(1)).getName();
        verify(trackingHeaderBean, times(1)).getUUID();
 
        verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
        String logMessage = captorLoggingEvent.getAllValues().get(0).getFormattedMessage();
        assertTrue(logMessage.contains("API name: testMethod"));
        assertTrue(logMessage.contains("operator_id: "));
        assertTrue(logMessage.contains("open_system_id: "));
        assertTrue(logMessage.contains("sent to: "));
        assertTrue(logMessage.contains("Request receive time: "));
        assertTrue(logMessage.contains("X-Tracking: " + mockUuid));
        assertTrue(logMessage.contains("URI path: "));

    }


    /**
     * JWTがnullの場合の {@code startLog} メソッドのテストを行います。
     * ログに警告が記録されることを検証します。
     */
    @Test
    void testStartLog_withwarn() {
        Logger logger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        // JoinPointのメソッドの戻り値を指定
        Mockito.when(joinPoint.getSignature()).thenReturn(methodSignature);

        // MethodSignatureのメソッドの戻り値を指定
        Mockito.when(methodSignature.getName()).thenReturn("testMethod");

        // loggerのログ出力先をモック化する
        logger.addAppender(mockAppender);

        // JwtUtilのモックメソッドの戻り値を指定
        String mockJwt = null;
        JSONObject mockPayload = null;
        when(jwtUtil.getJwtFromRequest(request)).thenReturn(mockJwt);
        when(jwtUtil.decodeJwtPayload(mockJwt)).thenReturn(mockPayload);

        // メソッドを実行
        logAspect.startLog(joinPoint);

        // 処理内で呼ばれるメソッドの確認
        verify(jwtUtil).getJwtFromRequest(request);
        verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
        assertEquals(captorLoggingEvent.getAllValues().get(0).toString(), "[WARN] accessToken is null");
    }

    /**
     * {@code endLog} メソッドのテストを行います。
     * ログが正しく記録されることを検証します。
     */
    @Test
    void testEndLog() {
        Logger logger = (Logger) LoggerFactory.getLogger(LogAspect.class);
        // loggerのログ出力先をモック化する
        logger.addAppender(mockAppender);

        // メソッドを実行
        logAspect.endLog();

        verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
        assertTrue(captorLoggingEvent.getAllValues().get(0).toString().contains("[INFO] Request sent time: "));
    }

}
