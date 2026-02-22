package Repository;

import Model.todoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface todoRepository extends JpaRepository<todoEntity, UUID>{
}
