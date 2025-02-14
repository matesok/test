package pl.sokolowski.rabbitmq.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.sokolowski.rabbitmq.model.Latency;
import pl.sokolowski.rabbitmq.model.Throughput;
import pl.sokolowski.rabbitmq.model.enums.QueueType;
import pl.sokolowski.rabbitmq.repository.QueueBenchmarkRepository;
import pl.sokolowski.rabbitmq.repository.ThroughputRepository;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;
    private final QueueBenchmarkRepository queueBenchmarkRepository;
    private final ThroughputRepository throughputRepository;

    private final AtomicLong batchId = new AtomicLong(1);

    public void warmUp() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("benchmark-exchange", "routing-key", "warm-up");
        }
    }

    public void sendMessage(int messageSize, int count) {
        long currentBatchId = batchId.getAndIncrement();

        String message = generateMessage(currentBatchId, messageSize);

        Latency latency = Latency.builder()
                .batchId(currentBatchId)
                .queueType(QueueType.RABBIT_MQ)
                .messageCount((long) count)
                .messageSize((long) messageSize)
                .startTime(System.nanoTime())
                .build();
        queueBenchmarkRepository.save(latency);

        for (int i = 0; i < count; i++) {
            rabbitTemplate.convertAndSend("benchmark-exchange", "routing-key", message);
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - latency.getStartTime();

        latency.setProducerLatency(totalTime / 1_000_000);
        queueBenchmarkRepository.save(latency);

        double throughput = count / ((totalTime) / 1_000_000_000.0);
        throughputRepository.save(Throughput.builder()
                .totalTime(totalTime)
                .messageSize((long) messageSize)
                .queueType(QueueType.RABBIT_MQ)
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
