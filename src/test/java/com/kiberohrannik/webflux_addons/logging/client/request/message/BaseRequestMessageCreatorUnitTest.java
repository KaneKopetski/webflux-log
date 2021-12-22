package com.kiberohrannik.webflux_addons.logging.client.request.message;

import com.kiberohrannik.webflux_addons.logging.base.BaseTest;
import com.kiberohrannik.webflux_addons.logging.client.LoggingProperties;
import com.kiberohrannik.webflux_addons.logging.client.request.message.formatter.CookieMessageFormatter;
import com.kiberohrannik.webflux_addons.logging.client.request.message.formatter.HeaderMessageFormatter;
import com.kiberohrannik.webflux_addons.logging.client.request.message.formatter.ReqIdMessageFormatter;
import com.kiberohrannik.webflux_addons.logging.provider.CookieProvider;
import com.kiberohrannik.webflux_addons.logging.provider.HeaderProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BaseRequestMessageCreatorUnitTest extends BaseTest {

    private RequestMessageCreator requestMessageCreator;

    @Spy
    private ReqIdMessageFormatter reqIdFormatter;

    private final HeaderMessageFormatter headerFormatter =
            Mockito.spy(new HeaderMessageFormatter(new HeaderProvider()));

    private final CookieMessageFormatter cookieFormatter =
            Mockito.spy(new CookieMessageFormatter(new CookieProvider()));


    private final LoggingProperties loggingProperties = LoggingProperties.builder()
            .logHeaders(true)
            .logCookies(true)
            .logRequestId(true)
            .build();


    @BeforeEach
    void setUp() {
        requestMessageCreator = new BaseRequestMessageCreator(loggingProperties,
                List.of(reqIdFormatter, headerFormatter, cookieFormatter));
    }


    @Test
    void formatMessage_usingInjectedFormatters() {
        ClientRequest testRequest = ClientRequest.create(HttpMethod.GET, URI.create("/someUri"))
                .header(HttpHeaders.AUTHORIZATION, "Some Auth")
                .cookie("Session", "sid4567")
                .build();

        String result = requestMessageCreator.formatMessage(testRequest).block();

        assertNotNull(result);
        assertTrue(result.contains("REQUEST:"));
        assertTrue(result.contains(testRequest.method().name()));
        assertTrue(result.contains(testRequest.url().toString()));

        assertTrue(result.contains(testRequest.logPrefix().replaceAll("[\\[\\]\\s]", "")));
        assertTrue(result.contains(HttpHeaders.AUTHORIZATION + "=Some Auth"));
        assertTrue(result.contains("Session=sid4567"));

        verify(reqIdFormatter).addData(eq(testRequest), eq(loggingProperties), any());
        verify(headerFormatter).addData(eq(testRequest), eq(loggingProperties), any());
        verify(cookieFormatter).addData(eq(testRequest), eq(loggingProperties), any());
    }
}
