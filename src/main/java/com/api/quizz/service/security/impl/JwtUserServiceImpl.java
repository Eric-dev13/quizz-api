package com.api.quizz.service.security.impl;



import com.api.quizz.configuration.security.Role;
import com.api.quizz.exceptions.AccountExistsException;
import com.api.quizz.repository.PlayerEntity;
import com.api.quizz.repository.PlayerRepository;
import com.api.quizz.service.security.JwtUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUserServiceImpl implements JwtUserService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final String signingKey;
    public JwtUserServiceImpl(@Value("${jwt.signing.key}") String signingKey) {
        this.signingKey = signingKey;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {
        PlayerEntity player = playerRepository.findByEmail(username);
        if (player == null) {
            throw new UsernameNotFoundException("The owner could not be found");
        }
        return player;
    }

    // USED FOR REGISTRATION
    @Override
    public Authentication authenticate(String username, String password) throws Exception {
        Authentication authentication = new
                UsernamePasswordAuthenticationToken(username, password);
        return authenticationConfiguration.getAuthenticationManager().authenticate(authentication);
    }


    @Override
    public UserDetails save(String username, String password) throws AccountExistsException {
        UserDetails existingUser = playerRepository.findByEmail(username);
        if (existingUser != null) {
            throw new AccountExistsException();
        }
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setEmail(username);
        playerEntity.setPassword(passwordEncoder.encode(password));
        playerEntity.setRole(Role.USER);
        PlayerEntity savedPlayer = playerRepository.save(playerEntity);
        return playerEntity;
    }

    @Override
    public UserDetails getUserFromJwt(String jwt) {
        String username = getUsernameFromToken(jwt);
        return loadUserByUsername(username);
    }

    private String getUsernameFromToken(String token) {
        System.out.println(signingKey);
        Claims claims =
                Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


    @Override
    public String generateJwtForUser(UserDetails user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600 * 1000);
        return
                Jwts.builder().setSubject(user.getUsername()).setIssuedAt(now).setExpiration(expiryDate)
                        .signWith(SignatureAlgorithm.HS512, signingKey)
                        .compact();
    }

}
