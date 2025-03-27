package net.ouranos.connector.digiline.common.exception;

import lombok.extern.slf4j.Slf4j;
import net.ouranos.connector.digiline.components.TrackingHeaderBean;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 例外ハンドラ
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private enum SystemName{
        AUTH("auth"),
        DATASPACE("dataspace");

        String systemName;
        SystemName(String systemName) {
            this.systemName = systemName;
        }
      };

    @Autowired
    private TrackingHeaderBean trackingHeaderBean;

    public GlobalExceptionHandler(TrackingHeaderBean trackingHeaderBean) {
        this.trackingHeaderBean = trackingHeaderBean;
    }

    /**
     * ResourceAccessException を処理します。
     * バックエンドサーバ利用不可例外が発生した場合に発生します。
     * 
     * @param ex バックエンドサーバ利用不可例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceAccessException.class)
    public  ResponseEntity<ErrorResponse> handleInternalServerErrorException(final ResourceAccessException ex) {
        UUID trackingUuid = trackingHeaderBean.getUUID();
        log.error("ResourceAccessException occured. {}, Tracking = {}", ex.getMessage(), trackingUuid);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = sdf.format(timestamp);
        ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] InternalServerError", "Unexpected error occurred", "timeStamp: "+str);
        return ResponseEntity.internalServerError().header("X-Tracking", trackingUuid.toString()).body(errorResponse);
    }

    /**
     * MissingRequestValueException を処理します。
     * リクエストパラメータが不足している場合に発生します。
     * 
     * @param ex MissingRequestValueException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestMissingRequestValueException(final MissingRequestValueException ex) {
      HttpServletRequest request = ((ServletRequestAttributes) Objects
      .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
      String trackingUuid = request.getHeader("x-Tracking");
      if(trackingUuid == null) {
        trackingUuid = trackingHeaderBean.getUUID().toString();
      }
      log.error("MissingRequestValueException occured. {}, Tracking = {}", ex.getMessage(), trackingUuid);
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] BadRequest", ex.getMessage(), "timeStamp: "+str);
      return ResponseEntity.badRequest().header("X-Tracking", trackingUuid.toString()).body(errorResponse);
    }

    /**
     * TypeMismatchException を処理します。
     * リクエストパラメータの型が不正な場合に発生します。
     * 
     * @param ex TypeMismatchException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestTypeMismatchException(final TypeMismatchException ex) {
      HttpServletRequest request = ((ServletRequestAttributes) Objects
      .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
      String trackingUuid = request.getHeader("x-Tracking");
      if(trackingUuid == null) {
        trackingUuid = trackingHeaderBean.getUUID().toString();
      }
      log.error("TypeMismatchException occured. {}, Tracking = {}", ex.getMessage(), trackingUuid);
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] BadRequest", ex.getMessage(), "timeStamp: "+str);
      return ResponseEntity.badRequest().header("X-Tracking", trackingUuid.toString()).body(errorResponse);
    }

    /**
     * NoResourceFoundException を処理します。
     * 存在しないResourceにアクセスした場合に発生します。
     * 
     * @param ex NoResourceFoundException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NoResourceFoundException ex) {
      HttpServletRequest request = ((ServletRequestAttributes) Objects
      .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
      String trackingUuid = request.getHeader("x-Tracking");
      if(trackingUuid == null) {
        trackingUuid = trackingHeaderBean.getUUID().toString();
      }
      log.error("NoResourceFoundException occured. {}, Tracking = {}", ex.getMessage(), trackingUuid);
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] NotFound", "Endpoint Not Found", "timeStamp: "+str);
      return ResponseEntity.status(404).header("X-Tracking", trackingUuid.toString()).body(errorResponse);
    }
    
    /**
     * HttpServerErrorException を処理します。
     * RestTemplateで5xx系のエラーが発生した場合に発生します。
     * 
     * @param ex HttpServerErrorException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Object> handleServiceUnavailableException(final  HttpServerErrorException ex) {
      UUID trackingUuid = trackingHeaderBean.getUUID();
      log.error("HttpServerErrorException occured. {}, Tracking = {}", ex.getMessage(), trackingUuid);
      String responseBody = ex.getResponseBodyAsString();
      return ResponseEntity.status(ex.getStatusCode()).header("X-Tracking", trackingUuid.toString()).body(responseBody);
    }

    /**
     * HttpClientErrorException を処理します。
     * RestTemplateで4xx系のエラーが発生した場合に発生します。
     * 404の場合はNotFoundエラーを作成し返します。
     * それ以外の場合は、エラーレスポンスをそのまま返します。
     *
     * @param ex HttpClientErrorException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleClientErrorException(final  HttpClientErrorException ex) throws JsonMappingException, JsonProcessingException {
      UUID trackingUuid = trackingHeaderBean.getUUID();
      log.error("HttpClientErrorException occured. {}, Tracking = {}", ex.getMessage(), trackingUuid);
      String responseBody = ex.getResponseBodyAsString();
      return ResponseEntity.status(ex.getStatusCode()).header("X-Tracking", trackingUuid.toString()).body(responseBody);
    }

    /**
     * VerifyTokenIntrospectionFalseException を処理します。
     *
     * @param ex VerifyTokenIntrospectionFalseException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(VerifyTokenIntrospectionFalseException.class)
    public ResponseEntity<ErrorResponse> handleVerifyTokenIntrospectionFalseException(final  VerifyTokenIntrospectionFalseException ex) {
      HttpServletRequest request = ((ServletRequestAttributes) Objects
      .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
      String trackingUuid = request.getHeader("x-Tracking");
      if(trackingUuid == null) {
        trackingUuid = trackingHeaderBean.getUUID().toString();
      }
      log.error("VerifyTokenIntrospectionFalseException occured. {}, Tracking = {}", ex.getMessage(), trackingUuid);
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.AUTH.systemName+"] Unauthorized", "Invalid or expired token","timeStamp: "+str);
      return ResponseEntity.status(401).header("X-Tracking", trackingUuid.toString()).body(errorResponse);
    }
}
