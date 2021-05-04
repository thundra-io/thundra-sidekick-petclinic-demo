package io.thundra.petclinicnotificationapp.consumer;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.thundra.agent.trace.instrument.config.Traceable;
import io.thundra.petclinicnotificationapp.configuration.ThundraConfig;
import io.thundra.petclinicnotificationapp.configuration.TraceConfiguration;
import io.thundra.petclinicnotificationapp.model.NotificationEvent;
import io.thundra.petclinicnotificationapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Traceable(
        justMarker = true
)
@RequiredArgsConstructor
public class NotificationConsumer {
    protected static final int DISPATCH_DELAY_MILLISECONDS = 1 * 20 * 1000;
    protected static final int POLLING_DELAY_SECONDS = 20;

    private final AmazonSQSAsync sqsClient;

    private final NotificationService notificationService;

    private final ThundraConfig config;

    private final ObjectMapper objectMapper;

    @Traceable(
            entryPoint = true,
            traceLineByLine = true,
            traceLinesWithSource = true,
            traceLocalVariables = true,
            traceArguments = TraceConfiguration.TRACE_ARGS,
            traceArgumentNames = TraceConfiguration.TRACE_ARG_NAMES,
            serializeArgumentsAsJson = TraceConfiguration.SERIALIZE_ARGUMENTS_AS_JSON,
            traceReturnValue = TraceConfiguration.TRACE_RETURN_VALUE,
            serializeReturnValueAsJson = TraceConfiguration.SERIALIZE_RETURN_VALUE_AS_JSON,
            traceError = TraceConfiguration.TRACE_ERROR
    )
    @Scheduled(fixedRate = DISPATCH_DELAY_MILLISECONDS)
    private void doPoll() {
        try {
            ReceiveMessageResult receiveMessageResult =
                    sqsClient.receiveMessage(
                            new ReceiveMessageRequest().
                                    withQueueUrl(config.getQueueUrl()).
                                    withWaitTimeSeconds(POLLING_DELAY_SECONDS).
                                    withMaxNumberOfMessages(10));
            List<Message> messages = receiveMessageResult.getMessages();
            if (messages.isEmpty()) {
                return;
            }
            for (Message message : messages) {
                log.info("Received message: " + message.getBody());
                try {
                    NotificationEvent event = objectMapper.readValue(message.getBody(), NotificationEvent.class);
                    notificationService.sendNotification(event);
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                } finally {
                    sqsClient.deleteMessage(config.getQueueUrl(), message.getReceiptHandle());
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }
}
