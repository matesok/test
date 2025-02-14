package pl.sokolowski.activemq.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.sokolowski.activemq.model.Latency;

public interface QueueBenchmarkRepository extends JpaRepository<Latency, Long> {
}
