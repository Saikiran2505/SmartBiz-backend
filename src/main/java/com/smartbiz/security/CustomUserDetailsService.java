package com.smartbiz.security;

import com.smartbiz.entity.Business;
import com.smartbiz.entity.User;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public CustomUserDetailsService(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Business business = businessRepository.findByOwnerId(user.getId()).orElse(null);
        Long businessId = (business != null) ? business.getId() : null;

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                businessId,
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
