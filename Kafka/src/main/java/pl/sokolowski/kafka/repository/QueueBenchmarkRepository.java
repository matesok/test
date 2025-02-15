package pl.sokolowski.kafka.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.sokolowski.kafka.model.Latency;

public interface QueueBenchmarkRepository extends JpaRepository<Latency, Long> {
}
