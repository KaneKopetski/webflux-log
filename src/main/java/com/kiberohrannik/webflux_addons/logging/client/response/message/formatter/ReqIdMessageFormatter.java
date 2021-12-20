package com.kiberohrannik.webflux_addons.logging.client.response.message.formatter;

import com.kiberohrannik.webflux_addons.logging.client.LoggingProperties;
import com.kiberohrannik.webflux_addons.logging.client.LoggingUtils;
import com.kiberohrannik.webflux_addons.logging.client.response.message.ResponseData;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class ReqIdMessageFormatter implements ResponseDataMessageFormatter {

    @Override
    public Mono<ResponseData> addData(LoggingProperties loggingProperties,
                                      Mono<ResponseData> sourceMessage) {

        if (loggingProperties.isLogRequestId()) {
            return sourceMessage.map(source -> {
                String reqIdMessage = extractReqId(source.getResponse(), loggingProperties);
                return source.addToLogs(reqIdMessage);
            });
        }

        return sourceMessage;
    }


    private String extractReqId(ClientResponse response, LoggingProperties loggingProperties) {
        String reqId = LoggingUtils.extractReqId(response.logPrefix());

        if (loggingProperties.getRequestIdPrefix() != null) {
            reqId = loggingProperties.getRequestIdPrefix().concat("_").concat(reqId);
        }

        return "\nREQ-ID: [ ".concat(reqId).concat(" ]");
    }
}