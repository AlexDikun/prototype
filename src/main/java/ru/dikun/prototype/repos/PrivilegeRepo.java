package ru.dikun.prototype.repos;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import ru.dikun.prototype.domain.PrivilegeEntity;

public interface PrivilegeRepo extends CrudRepository<PrivilegeEntity, Long> {

    Optional<PrivilegeEntity> findByName(String name);

}