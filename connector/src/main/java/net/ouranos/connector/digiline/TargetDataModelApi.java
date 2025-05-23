/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package net.ouranos.connector.digiline;

import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;


import jakarta.validation.Valid;

import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-08-07T16:11:47.528558400+09:00[Asia/Tokyo]", comments = "Generator version: 7.6.0")
@Validated
@Tag(name = "データ流通システム", description = "the データ流通システム API")
public interface TargetDataModelApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /{targetDataModel}
     * XXXデータモデルのデータを削除します。
     *
     * @param targetDataModel 対象データモデル (required)
     * @param id データID (required)
     * @param xTracking トラッキングID (optional)
     * @return OK (status code 200)
     */
    @Operation(
        operationId = "targetDataModelDelete",
        description = "XXXデータモデルのデータを削除します。",
        tags = { "データ流通システム" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{targetDataModel}"
    )
    
    ResponseEntity<Void> targetDataModelDelete(
        @Parameter(name = "targetDataModel", description = "対象データモデル", required = true, in = ParameterIn.PATH) @PathVariable("targetDataModel") String targetDataModel,
        @Parameter(name = "Object", description = "Request Body", required = false) @Valid @RequestBody(required = false) Object dataModelTemplate,
        @Parameter(name = "X-Tracking", description = "トラッキングID", in = ParameterIn.HEADER) @RequestHeader(value = "X-Tracking", required = false) UUID xTracking,
        @Parameter(name = "Headers", description = "Request Headers", in = ParameterIn.HEADER, required = false) @RequestHeader HttpHeaders headers
    );


    /**
     * GET /{targetDataModel}
     * XXXデータモデルのデータを取得します。
     *
     * @param targetDataModel 対象データモデル (required)
     * @param xTracking トラッキングID (optional)
     * @return OK (status code 200)
     */
    @Operation(
        operationId = "targetDataModelGet",
        description = "XXXデータモデルのデータを取得します。",
        tags = { "データ流通システム" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/{targetDataModel}",
        produces = { "application/json" }
    )
    
    ResponseEntity<Object> targetDataModelGet(
        @Parameter(name = "targetDataModel", description = "対象データモデル", required = true, in = ParameterIn.PATH) @PathVariable("targetDataModel") String targetDataModel,
        @Parameter(name = "X-Tracking", description = "トラッキングID", in = ParameterIn.HEADER) @RequestHeader(value = "X-Tracking", required = false) UUID xTracking,
        @Parameter(name = "Headers", description = "Request Headers", in = ParameterIn.HEADER, required = false) @RequestHeader HttpHeaders headers
    );


    /**
     * PUT /{targetDataModel}
     * XXXデータモデルのデータを新規作成or更新します。
     *
     * @param targetDataModel 対象データモデル (required)
     * @param xTracking トラッキングID (optional)
     * @return OK (status code 200)
     *         or Created (status code 201)
     */
    @Operation(
        operationId = "targetDataModelPut",
        description = "XXXデータモデルのデータを新規作成or更新します。",
        tags = { "データ流通システム" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))
            }),
            @ApiResponse(responseCode = "201", description = "Created")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/{targetDataModel}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    ResponseEntity<Object> targetDataModelPut(
        @Parameter(name = "targetDataModel", description = "対象データモデル", required = true, in = ParameterIn.PATH) @PathVariable("targetDataModel") String targetDataModel,
        @Parameter(name = "Object", description = "Request Body", required = true) @Valid @RequestBody Object dataModelTemplate,
        @Parameter(name = "X-Tracking", description = "トラッキングID", in = ParameterIn.HEADER) @RequestHeader(value = "X-Tracking", required = false) UUID xTracking,
        @Parameter(name = "Headers", description = "Request Headers", in = ParameterIn.HEADER, required = false) @RequestHeader HttpHeaders headers
    );

    /**
     * POST /{targetDataModel}
     * XXXデータモデルのデータの処理を行います。
     *
     * @param targetDataModel 対象データモデル (required)
     * @param xTracking トラッキングID (optional)
     * @return OK (status code 200)
     *         or Created (status code 201)
     *         or No Content (status code 204)
     */
    @Operation(
        operationId = "targetDataModelPost",
        description = "XXXデータモデルのデータを処理を行います。",
        tags = { "データ流通システム" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))
            }),
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "204", description = "No Content")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/{targetDataModel}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    ResponseEntity<Object> targetDataModelPost(
        @Parameter(name = "targetDataModel", description = "対象データモデル", required = true, in = ParameterIn.PATH) @PathVariable("targetDataModel") String targetDataModel,
        @Parameter(name = "Object", description = "Request Body", required = true) @Valid @RequestBody Object dataModelTemplate,
        @Parameter(name = "X-Tracking", description = "トラッキングID", in = ParameterIn.HEADER) @RequestHeader(value = "X-Tracking", required = false) UUID xTracking,
        @Parameter(name = "Headers", description = "Request Headers", in = ParameterIn.HEADER, required = false) @RequestHeader HttpHeaders headers
    );

}
