package tn.dymes.store.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.User;
import tn.dymes.store.repositories.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {

    private JwtEncoder jwtEncoder;
    private UserDetailsService userDetailsService;
    private IUserService userService;
    private UserRepository userRepository;
    private IMailService mailService;


    public AuthServiceImpl(JwtEncoder jwtEncoder, UserDetailsService userDetailsService, IMailService mailService ,IUserService userService, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    public Map<String, String> generateToken(String email, boolean withRefreshToken) {
        Map<String,String> idToken = new HashMap<>();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        User myuser = userService.findUserByEmail(email);
        Instant instant = Instant.now();
        String scope = userDetails.getAuthorities().stream().map(auth->auth.getAuthority())
                .collect(Collectors.joining(" "));
        // send profile photoURL with token
        if (myuser.getProfilePhoto() == null){
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(String.valueOf(myuser.getId()))
                    .issuer("Dymes")
                    // si il ya refreshToken le accessToken expire en 55 MIN sinon en 90 MIN
                    .expiresAt(instant.plus(withRefreshToken?55:90, ChronoUnit.MINUTES))
                    .issuedAt(instant)
                    .claim("scope",scope)
                    .claim("photoURL","null")
                    .claim("email",email.toLowerCase())
                    .build();
            String jwtAccessToken =  jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
            idToken.put("access-token",jwtAccessToken);
        }
        else{
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(String.valueOf(myuser.getId()))
                    .issuer("Dymes")
                    // si il ya refreshToken le accessToken expire en 55 MIN sinon en 90 MIN
                    .expiresAt(instant.plus(withRefreshToken?55:90, ChronoUnit.MINUTES))
                    .issuedAt(instant)
                    .claim("scope",scope)
                    .claim("photoURL",myuser.getProfilePhoto())
                    .claim("email",email.toLowerCase())
                    .build();
            String jwtAccessToken =  jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
            idToken.put("access-token",jwtAccessToken);
        }
        //end send photo url


        // with refreshToken
        if (withRefreshToken){
            JwtClaimsSet jwtRefreshClaimsSet = JwtClaimsSet.builder()
                    .subject(String.valueOf(myuser.getId()))
                    .issuer("Dymes")
                    .claim("email",email.toLowerCase())
                    .expiresAt(instant.plus(55, ChronoUnit.MINUTES))
                    .issuedAt(instant)
                    .build();

            String jwtRefreshToken =  jwtEncoder.encode(JwtEncoderParameters.from(jwtRefreshClaimsSet)).getTokenValue();
            idToken.put("refresh-token",jwtRefreshToken);
        }
        return idToken;
    }

    @Override
    public void sendActivationCode(String email) {
        User user = userService.findUserByEmail(email);
        Random random=new Random();
        String activationCode="";
        for (int i = 0; i <4 ; i++) {
            activationCode+=random.nextInt(9);
        }
        user.setTemporaryActivationCode(activationCode);
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        user.setTemporaryActivationCodeTimeStamp(now);
        userRepository.save(user);
        mailService.sendEmail(email,"Initialisation du mot de passe","Votre code secret est : "+activationCode);
    }

    @Override
    public String authorizePasswordInitialization(String authorizationCode, String email) {
        User user=userService.findUserByEmail(email);
        if(!user.getTemporaryActivationCode().equals(authorizationCode))
            return "Code d'autorisation incorrect";
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant lastInstant=user.getTemporaryActivationCodeTimeStamp();
        Instant lastInstantPlus5=lastInstant.plus(5,ChronoUnit.MINUTES);
        if(!now.isBefore(lastInstantPlus5))
            return "Ce code d'autorisation a expiré";
        user.setTemporaryActivationCodeTimeStamp(now);
        userRepository.save(user);
        return "Merci!";
    }

    @Override
    public void resetPassword(String pass, String email) {
        User user = userService.findUserByEmail(email);
        user.setPassword(this.passwordEncoder().encode(pass));
        userRepository.save(user);
    }
}
