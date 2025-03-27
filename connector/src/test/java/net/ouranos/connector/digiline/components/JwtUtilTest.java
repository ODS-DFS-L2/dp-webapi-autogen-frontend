package net.ouranos.connector.digiline.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * {@code JwtUtil} クラスの単体テストを行うためのテストクラス。
 */
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    /**
     * テストのセットアップを行います。
     * モックオブジェクトの初期化を行います。
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();
    }

    /**
     * Bearer Tokenが正しい場合の {@code getJwtFromRequest} メソッドのテストを行います。
     * 正しいトークンが返されることを検証します。
     */
    @Test
    public void testGetJwtFromRequest_withValidBearerToken() {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");

        String jwt = jwtUtil.getJwtFromRequest(request);

        assertEquals("valid.jwt.token", jwt);
    }

    /**
     * Bearer Tokenが不正な場合の {@code getJwtFromRequest} メソッドのテストを行います。
     */
    @Test
    public void testGetJwtFromRequest_withInvalidBearerToken() {
        when(request.getHeader("Authorization")).thenReturn("InvalidBearerToken");

        String jwt = jwtUtil.getJwtFromRequest(request);

        assertNull(jwt);
    }

    /**
     * Bearer Tokenが含まれていない場合の {@code getJwtFromRequest} メソッドのテストを行います。
     */
    @Test
    public void testGetJwtFromRequest_withNoBearerToken() {
        when(request.getHeader("Authorization")).thenReturn(null);

        String jwt = jwtUtil.getJwtFromRequest(request);

        assertNull(jwt);
    }

    /**
     * JWTが正しい場合の {@code decodeJwtPayload} メソッドのテストを行います。
     * ペイロード部分が正しくデコードされることを検証します。
     */
    @Test
    public void testDecodeJwtPayload_withValidJwt() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        JSONObject decodedPayload = jwtUtil.decodeJwtPayload(jwt);

        assertEquals("1234567890", decodedPayload.optString("sub"));
        assertEquals("John Doe", decodedPayload.optString("name"));
        assertEquals(1516239022, decodedPayload.optInt("iat"));
    }

    /**
     * JWTがnullの場合の {@code decodeJwtPayload} メソッドのテストを行います。
     * nullが返されることを検証します。
     */
    @Test
    public void testDecodeJwtPayload_withNullJwt() {
        String jwt = null;
        
        JSONObject decodedPayload = jwtUtil.decodeJwtPayload(jwt);

        assertEquals(null, decodedPayload);
    }

    /**
     * JWTが不正な場合の {@code decodeJwtPayload} メソッドのテストを行います。
     * nullが返されることを検証します。
     */
    @Test
    public void testDecodeJwtPayload_withInvalidJwtToken() {
        String jwt = "invalidToken";

        JSONObject decodedPayload = jwtUtil.decodeJwtPayload(jwt);

        assertEquals(null, decodedPayload);
    
    }
        
}