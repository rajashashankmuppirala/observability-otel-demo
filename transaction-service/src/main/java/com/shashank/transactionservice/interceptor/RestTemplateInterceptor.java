package com.shashank.transactionservice.interceptor;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final String TRACE_ID_HEADER = "X-B3-TraceId";
    private static final String SPAN_ID_HEADER = "X-B3-SpanId";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Get current span from OpenTelemetry context
        Span currentSpan = Span.current();
        
        if (currentSpan != null) {
            SpanContext spanContext = currentSpan.getSpanContext();
            
            // Add trace ID and span ID as headers to outgoing request
            if (spanContext.isValid()) {
                request.getHeaders().set(TRACE_ID_HEADER, spanContext.getTraceId());
                request.getHeaders().set(SPAN_ID_HEADER, spanContext.getSpanId());
            }
        }
        
        // Continue with the request execution
        return execution.execute(request, body);
    }
}
