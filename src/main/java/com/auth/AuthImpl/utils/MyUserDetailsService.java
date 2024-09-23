package com.auth.AuthImpl.utils;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;
    private final JWTService jwtUtil;

    public MyUserDetailsService(UserRepository userRepo, JWTService jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public UserDetails loadUserByJwtToken(String token) throws UsernameNotFoundException {
        Claims claims = jwtUtil.extractAllClaims(token);
        String username = claims.getSubject();
        return loadUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User name not found");
        }
        return new UserPrincipal(user);
    }
}
