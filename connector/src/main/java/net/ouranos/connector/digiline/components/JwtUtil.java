package net.ouranos.connector.digiline.components;

import org.springframework.stereotype.Component;
import java.util.Base64;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT（JSON Web Token）のpayload部分を抽出するためのユーティリティクラス。
 */
@Component
@Slf4j
public class JwtUtil {

    /**
     * リクエストヘッダーからBearer Tokenを取得します。
     *
     * @param request HTTPリクエストオブジェクト
     * @return Bearer Tokenが含まれている場合はトークン文字列、含まれていない場合はnull
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "を取り除く
        }
        return null;
    }

    /**
     * JWTをデコードし、ペイロード部分を取得します。
     *
     * @param jwt JWT文字列
     * @return デコードされたペイロード部分を含むJSONObject、JWTがnullの場合はnull
     */
    public JSONObject decodeJwtPayload(String jwt) {
        if (jwt == null) {
            return null;
        }
        String[] parts = jwt.split("\\.");
        // JWTが3つの部分に分かれていない場合
        if (parts.length != 3) {
            log.warn("Invalid JWT token");
            return null;
        }else{
        String payload = parts[1];
        byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
        String decodedPayload = new String(decodedBytes);
        return new JSONObject(decodedPayload);
        }
    }
}
