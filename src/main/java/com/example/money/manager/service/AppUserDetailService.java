package com.example.money.manager.service;

import com.example.money.manager.entity.ProfileEntity;
import com.example.money.manager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {
    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                    ProfileEntity existingProfile= profileRepository.findByEmail(email)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    return User.builder()            ///iska kam he Database se nikali hui user information ko Spring Security ke samajhne wale format (UserDetails) me convert karke wapas dena.
                            .username(existingProfile.getEmail())
                            .password(existingProfile.getPassword())
                            .authorities(Collections.emptyList())
                            .build();

    }
}
