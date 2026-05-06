package com.academia.user_service.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.academia.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
