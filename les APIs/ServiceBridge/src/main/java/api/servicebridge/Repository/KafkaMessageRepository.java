package api.servicebridge.Repository;

import api.servicebridge.Model.KafkaMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KafkaMessageRepository extends JpaRepository<KafkaMessage, Long> {
}

