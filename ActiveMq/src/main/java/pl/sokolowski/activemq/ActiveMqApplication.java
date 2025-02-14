package pl.sokolowski.activemq;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import pl.sokolowski.activemq.sender.ActiveMqSender;

@SpringBootApplication
@RequiredArgsConstructor
@EnableJms
public class ActiveMqApplication implements CommandLineRunner {

    private final ActiveMqSender activeMqSender;

    public static void main(String[] args) {
        SpringApplication.run(ActiveMqApplication.class, args);
    }

    @Override
    public void run(String... args) {
        int[] messageCounts = {10, 100, 500, 1000};
        int[] messageSizes = {10, 100, 1024, 10240, 102400, 1048576};

        activeMqSender.warmUp();

        for (int count : messageCounts) {
            for (int size : messageSizes) {
                activeMqSender.sendMessage(size, count);
            }
        }
    }
}
