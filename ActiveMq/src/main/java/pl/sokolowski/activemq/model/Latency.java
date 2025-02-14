package pl.sokolowski.activemq.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import pl.sokolowski.activemq.model.enums.QueueType;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
public class Latency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long batchId;
    private Long startTime;
    @Transient
    private Long endTime;
    private Long producerLatency;
    @Transient
    private Long endToEndLatency;

    @Enumerated(value = EnumType.STRING)
    private QueueType queueType;
    private Long messageSize;
    private Long messageCount;

}
