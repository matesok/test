package pl.sokolowski.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sokolowski.rabbitmq.model.Throughput;

public interface ThroughputRepository extends JpaRepository<Throughput, Long>{
}
