package pl.sokolowski.rabbitmq.model;

import jakarta.persistence.*;
import lombok.*;
import pl.sokolowski.rabbitmq.model.enums.QueueType;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class Throughput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private QueueType queueType;

    private Double throughputValue;
    private Long messageSize;
    private Long totalTime;

}
