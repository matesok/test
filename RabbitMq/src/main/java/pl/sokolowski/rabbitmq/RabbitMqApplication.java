package pl.sokolowski.rabbitmq;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.sokolowski.rabbitmq.sender.RabbitMQSender;

@SpringBootApplication
public class RabbitMqApplication implements CommandLineRunner {

    private final RabbitMQSender rabbitMQSender;

    public RabbitMqApplication(RabbitMQSender rabbitMQSender) {
        this.rabbitMQSender = rabbitMQSender;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqApplication.class, args);
    }

    @Override

    public void run(String... args) {
        int[] messageCounts = {10, 100, 500, 1000};
        int[] messageSizes = {10, 100, 1024, 10240, 102400, 1048576};

        rabbitMQSender.warmUp();

        for (int count : messageCounts) {
            for (int size : messageSizes) {
                rabbitMQSender.sendMessage(size, count);
            }
        }
    }
}
