package org.theleakycauldron.thesortinghat.services.implementations;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.theleakycauldron.thesortinghat.dtos.SortingHatLoginResponseDTO;
import org.theleakycauldron.thesortinghat.entities.LoginToken;
import org.theleakycauldron.thesortinghat.entities.User;
import org.theleakycauldron.thesortinghat.repositories.SortingHatTokenRepository;
import org.theleakycauldron.thesortinghat.repositories.SortingHatUserRepository;
import org.theleakycauldron.thesortinghat.services.SortingHatService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.Timestamp.valueOf;


/**
 * @author: Vijaysurya Mandala
 * @github: github/mandalavijaysurya (<a href="https://www.github.com/mandalavijaysurya"> Github</a>)
 */

@Service
public class SortingHatServiceImpl implements SortingHatService {

    private final SortingHatTokenRepository sortingHatTokenRepository;
    private final SortingHatUserRepository sortingHatUserRepository;
    @Value("${jwt.secret}")
    private String secret;
    private SecretKey key;


    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public SortingHatServiceImpl(
            SortingHatTokenRepository sortingHatTokenRepository,
            SortingHatUserRepository sortingHatUserRepository
    ){
        this.sortingHatTokenRepository = sortingHatTokenRepository;
        this.sortingHatUserRepository = sortingHatUserRepository;
    }

    @Override
    public SortingHatLoginResponseDTO login(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
        LoginToken token = createToken(username);
        User user = sortingHatUserRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        sortingHatTokenRepository.save(token);
        return SortingHatLoginResponseDTO.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(username)
                .token(token.getToken())
                .build();
    }

    private LoginToken createToken(String username){
        Map<String, String> claims = new HashMap<>();
        claims.put("email", username);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusDays(10);
        String token = Jwts.builder()
                .claims(claims)
                .expiration(valueOf(now))
                .issuedAt(valueOf(now))
                .signWith(key)
                .compact();
        return LoginToken.builder()
                .createdAt(now)
                .updatedAt(now)
                .isDeleted(false)
                .expiresAt(expiryTime)
                .token(token)
                .build();
    }
}
