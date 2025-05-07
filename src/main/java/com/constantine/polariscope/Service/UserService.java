package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public void save(User user){
        userRepository.save(user);
    }

    // ONLY used for report generation; should not be used for anything else for security reasons
    public List<User> getAll(){
        return userRepository.findAll();
    }
}
