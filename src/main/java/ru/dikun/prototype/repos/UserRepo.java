package ru.dikun.prototype.repos;

import org.springframework.data.repository.CrudRepository;

import ru.dikun.prototype.domain.UserEntity;

public interface UserRepo extends CrudRepository<UserEntity, Long> {}
