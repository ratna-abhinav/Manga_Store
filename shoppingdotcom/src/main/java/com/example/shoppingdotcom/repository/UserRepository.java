package com.example.shoppingdotcom.repository;

import com.example.shoppingdotcom.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {
}
