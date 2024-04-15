package com.bench.runtime.mq;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.messaging.support.ErrorMessage;

/**
 * <p>
 * SpringCloudStream 全局异常处理
 * </p>
 *
 * @author Karl
 * @since 2024/4/15 11:35
 */
public interface SpringCloudStreamGlobalHandler {

    @StreamListener(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
    void handle(ErrorMessage errorMessage);
}
