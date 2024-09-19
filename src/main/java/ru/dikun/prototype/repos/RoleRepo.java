package ru.dikun.prototype.repos;

import org.springframework.data.repository.CrudRepository;

import ru.dikun.prototype.domain.RoleEntity;

public interface RoleRepo extends CrudRepository<RoleEntity, Long> {}