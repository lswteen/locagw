package com.lottecard.loca.core.handler;

import com.lottecard.loca.HttpUtils;
import com.lottecard.loca.meta.ErrorType;
import com.lottecard.loca.meta.ResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Order(-1)
@Component
public class GatewayExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final String ERROR_PAGE = "static/error.html";
    private static final String DEFAULT_MESSAGE = "GatewayExceptionHandler response body";
    private static final String DEFAULT_DATA = "-";

    private static final MediaType TEXT_HTML_UTF8 = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

    private final Resource errorPage;
    private final ErrorAttributes errorAttributes;


    public GatewayExceptionHandler(ErrorAttributes errorAttributes,
                                   WebProperties webProperties,
                                   ApplicationContext applicationContext,
                                   ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        this.errorAttributes = errorAttributes;
        this.errorPage = new ClassPathResource(ERROR_PAGE);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::response);
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
        if (HttpStatus.resolve(response.statusCode().value()) != null
                && response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            log.error("{} 500 Server Error for {}", request.exchange().getLogPrefix(), formatRequest(request), throwable);
        }
    }

    private String formatRequest(ServerRequest request) {
        String rawQuery = request.uri().getRawQuery();
        String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
        return "HTTP " + request.method() + " \"" + request.path() + query + "\"";
    }

    private Mono<ServerResponse> response(ServerRequest request) {
        Throwable throwable = errorAttributes.getError(request);

        if (throwable instanceof ResponseStatusException responseStatusException) {
            HttpStatus httpStatus = HttpUtils.getHttpStatus(responseStatusException.getStatusCode());
            log.error("ResponseStatusException. cause : {}", responseStatusException.getMessage());
            return this.response(request, httpStatus, httpStatus.getReasonPhrase());
        }

        return this.response(request, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    private Mono<ServerResponse> response(ServerRequest request, HttpStatus httpStatus, String message) {
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.status(httpStatus);
        return HttpUtils.isApi(request.uri()) ? responseBody(bodyBuilder, message) : responsePage(bodyBuilder);
    }

    private Mono<ServerResponse> responsePage(ServerResponse.BodyBuilder bodyBuilder) {
        return bodyBuilder.contentType(TEXT_HTML_UTF8)
                .body(BodyInserters.fromResource(errorPage));
    }

    private Mono<ServerResponse> responseBody(ServerResponse.BodyBuilder bodyBuilder, String message) {
        return bodyBuilder.contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(ResponseObject.errorWithMessage(ErrorType.INTERNAL_SERVER_ERROR.getCode()
                        , ErrorType.INTERNAL_SERVER_ERROR.getMessage()
                        ,DEFAULT_MESSAGE
                        ,DEFAULT_DATA)));
    }
}
