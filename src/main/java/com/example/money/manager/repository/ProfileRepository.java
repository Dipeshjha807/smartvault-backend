package com.example.money.manager.repository;

import com.example.money.manager.entity.ProfileEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity,Long> {
     Optional<ProfileEntity> findByEmail(String email);

     //selectv * form table where activation token=?
     Optional<ProfileEntity> findByActivationToken(String token);
}
