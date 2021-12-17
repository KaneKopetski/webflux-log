package com.kiberohrannik.webflux_addons.logging.request.filter;

import com.kiberohrannik.webflux_addons.logging.request.message.RequestMessageCreator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LogRequestFilter implements ExchangeFilterFunction {

    private static final Log log = LogFactory.getLog(LogRequestFilter.class);
    private final RequestMessageCreator messageCreator;


    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return messageCreator.formatMessage(request)
                .doOnNext(log::info)
                .flatMap(val -> next.exchange(request));
    }
}