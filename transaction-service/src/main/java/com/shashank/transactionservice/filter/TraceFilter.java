package com.shashank.transactionservice.filter;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Log4j2
public class TraceFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-B3-TraceId";
    private static final String SPAN_ID_HEADER = "X-B3-SpanId";

    private final Tracer tracer;

    @Autowired
    public TraceFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
        String spanId = httpRequest.getHeader(SPAN_ID_HEADER);
        
        Span span;
        
        // If no trace ID is provided, create a new one
        if (traceId == null || traceId.isEmpty()) {
            span = tracer.spanBuilder(httpRequest.getRequestURI())
                    .setSpanKind(io.opentelemetry.api.trace.SpanKind.SERVER)
                    .startSpan();
        } else {
            // Use the provided trace ID
            span = Span.current();
        }
        
        try (Scope scope = span.makeCurrent()) {
            // Extract current span context
            SpanContext spanContext = span.getSpanContext();
            
            // Add trace and span IDs to Thread Context for logging
            ThreadContext.put("traceId", spanContext.getTraceId());
            ThreadContext.put("spanId", spanContext.getSpanId());
            
            // Add trace ID to response headers
            httpResponse.setHeader(TRACE_ID_HEADER, spanContext.getTraceId());
            httpResponse.setHeader(SPAN_ID_HEADER, spanContext.getSpanId());
            
            log.info("Processing request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            
            // Continue the filter chain
            chain.doFilter(request, response);
            
            log.info("Completed request: {} {} with status {}", 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(), 
                    httpResponse.getStatus());
        } finally {
            // Clear the thread context
            ThreadContext.remove("traceId");
            ThreadContext.remove("spanId");
            
            // End the span if we created a new one
            if (traceId == null || traceId.isEmpty()) {
                span.end();
            }
        }
    }
}
