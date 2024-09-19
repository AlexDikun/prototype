package ru.dikun.prototype.repos;

import org.springframework.data.repository.CrudRepository;

import ru.dikun.prototype.domain.PrivilegeEntity;

public interface PrivilegeRepo extends CrudRepository<PrivilegeEntity, Long> {}