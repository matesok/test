package pl.sokolowski.activemq.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import pl.sokolowski.activemq.model.Latency;
import pl.sokolowski.activemq.model.Throughput;
import pl.sokolowski.activemq.model.enums.QueueType;
import pl.sokolowski.activemq.repository.QueueBenchmarkRepository;
import pl.sokolowski.activemq.repository.ThroughputRepository;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ActiveMqSender {

    private final JmsTemplate jmsTemplate;
    private final QueueBenchmarkRepository queueBenchmarkRepository;
    private final ThroughputRepository throughputRepository;

    private final AtomicLong batchId = new AtomicLong(1);

    public void warmUp() {
        for (int i = 0; i < 10; i++) {
            jmsTemplate.convertAndSend("benchmark-queue", "warm-up");
        }
    }

    public void sendMessage(int messageSize, int count) {
        long currentBatchId = batchId.getAndIncrement();

        String message = generateMessage(currentBatchId, messageSize);

        Latency latency = Latency.builder()
                .batchId(currentBatchId)
                .queueType(QueueType.ACTIVE_MQ)
                .messageCount((long) count)
                .messageSize((long) messageSize)
                .startTime(System.nanoTime())
                .build();

        queueBenchmarkRepository.saveAndFlush(latency);

        for (int i = 0; i < count; i++) {
            jmsTemplate.convertAndSend("benchmark-queue", message);
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - latency.getStartTime();

        latency.setProducerLatency(totalTime / 1_000_000);
        queueBenchmarkRepository.save(latency);

        double throughput = (double) count / (totalTime / 1_000_000_000.0);
        throughputRepository.save(Throughput.builder()
                .totalTime(totalTime)
                .messageSize((long) messageSize)
                .messageCount((long) count)
                .queueType(QueueType.ACTIVE_MQ)
                .throughputValue(throughput)
                .build());
    }

    public String generateMessage(long batchId, int targetSize) {
        byte[] idBytes = ByteBuffer.allocate(Long.BYTES).putLong(batchId).array();
        int paddingSize = targetSize - idBytes.length;

        byte[] padding = new byte[paddingSize];
        Arrays.fill(padding, (byte) ' ');

        byte[] messageBytes = new byte[idBytes.length + padding.length];
        System.arraycopy(idBytes, 0, messageBytes, 0, idBytes.length);
        System.arraycopy(padding, 0, messageBytes, idBytes.length, padding.length);

        return new String(messageBytes, StandardCharsets.UTF_8);
    }

}
