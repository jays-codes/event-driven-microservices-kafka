package jayslabs.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jayslabs.kafka.section11.integrationtest.${app}")
public class ReactiveKafkaSandboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveKafkaSandboxApplication.class, args);
	}

}
