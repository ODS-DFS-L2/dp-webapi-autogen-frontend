package net.ouranos.domain.digiline.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * トークン検証APIのOUTPUT
 */

@Schema(name = "authentication.TokenOutputModel", description = "トークン検証APIのOUTPUT")
@JsonTypeName("authentication.TokenOutputModel")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-10-10T12:24:30.505501300+09:00[Asia/Tokyo]", comments = "Generator version: 7.9.0")
public class AuthenticationTokenOutputModel {

  private UUID operatorId;

  private String openSystemId;

  private Boolean active;

  public AuthenticationTokenOutputModel operatorId(UUID operatorId) {
    this.operatorId = operatorId;
    return this;
  }

  /**
   * 事業者識別子（内部）
   * @return operatorId
   */
  @Valid 
  @Schema(name = "operatorId", example = "db00afa9-8e8c-8887-5a98-d2c768bf6205", description = "事業者識別子（内部）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operatorId")
  public UUID getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(UUID operatorId) {
    this.operatorId = operatorId;
  }

  public AuthenticationTokenOutputModel openSystemId(String openSystemId) {
    this.openSystemId = openSystemId;
    return this;
  }

  /**
   * 公開システム識別子
   * @return openSystemId
   */
  @Size(min = 21, max = 36) 
  @Schema(name = "openSystemId", example = "cooperationSystemC202", description = "公開システム識別子", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("openSystemId")
  public String getOpenSystemId() {
    return openSystemId;
  }

  public void setOpenSystemId(String openSystemId) {
    this.openSystemId = openSystemId;
  }

  public AuthenticationTokenOutputModel active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * トークン有効
   * @return active
   */
  
  @Schema(name = "active", example = "true", description = "トークン有効", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthenticationTokenOutputModel authenticationTokenOutputModel = (AuthenticationTokenOutputModel) o;
    return Objects.equals(this.operatorId, authenticationTokenOutputModel.operatorId) &&
        Objects.equals(this.openSystemId, authenticationTokenOutputModel.openSystemId) &&
        Objects.equals(this.active, authenticationTokenOutputModel.active);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operatorId, openSystemId, active);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationTokenOutputModel {\n");
    sb.append("    operatorId: ").append(toIndentedString(operatorId)).append("\n");
    sb.append("    openSystemId: ").append(toIndentedString(openSystemId)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

