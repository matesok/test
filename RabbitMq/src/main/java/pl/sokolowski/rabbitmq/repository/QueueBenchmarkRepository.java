package pl.sokolowski.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sokolowski.rabbitmq.model.Latency;

public interface QueueBenchmarkRepository extends JpaRepository<Latency, Long> {
}
