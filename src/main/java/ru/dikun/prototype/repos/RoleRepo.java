package ru.dikun.prototype.repos;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import ru.dikun.prototype.domain.RoleEntity;

public interface RoleRepo extends CrudRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

}