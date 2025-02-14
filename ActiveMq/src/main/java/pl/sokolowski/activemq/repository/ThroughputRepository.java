package pl.sokolowski.activemq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sokolowski.activemq.model.Throughput;

public interface ThroughputRepository extends JpaRepository<Throughput, Long>{
}
