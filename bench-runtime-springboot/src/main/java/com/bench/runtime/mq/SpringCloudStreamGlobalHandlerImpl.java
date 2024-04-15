package com.bench.runtime.mq;

import com.bench.runtime.mq.SpringCloudStreamGlobalHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>
 * 默认实现
 * </p>
 *
 * @author Karl
 * @since 2024/4/15 11:37
 */
@Slf4j
@ConditionalOnProperty(prefix = "spring.cloud.stream", value = "default-binder", havingValue = "kafka")
@Component
public class SpringCloudStreamGlobalHandlerImpl implements SpringCloudStreamGlobalHandler {

    @Override
    public void handle(ErrorMessage errorMessage) {
        System.out.println("********************************************************************");

        Message<?> originalMessage = errorMessage.getOriginalMessage();
        try {
            ConsumerRecord sourceData = (ConsumerRecord) errorMessage.getHeaders().get("sourceData");
            String topic = sourceData.topic();
            String payload = new String((byte[]) originalMessage.getPayload());
            ActiveSpan.tag("mq.topic", topic);
            ActiveSpan.tag("mq.payload", payload);
            log.info("GlobalHandleError: {}, {}, {}", topic, payload, errorMessage.getPayload().getMessage());
        } catch (Exception e) {
            log.error("GlobalHandleError: {}, {}", originalMessage, errorMessage.getPayload().getMessage(), e);
        }
        Optional<String> error = TraceContext.getCorrelation("error");
        error.ifPresent(ActiveSpan::error);
        ActiveSpan.error(errorMessage.getPayload().getCause());
    }
}
