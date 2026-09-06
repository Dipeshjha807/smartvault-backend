package com.example.money.manager.service;

import com.example.money.manager.dto.AuthDTO;
import com.example.money.manager.dto.ProfileDTO;
import com.example.money.manager.entity.ProfileEntity;
import com.example.money.manager.repository.ProfileRepository;
import com.example.money.manager.util.JwtUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    private final Emailservice emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.backend.url}")
    private String activationURL;

    public ProfileService(Emailservice emailService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    public ProfileDTO registerProfile(ProfileDTO profileDTO) throws MessagingException {  ///  3 kam he ye method ka  1-> frontend se jo data aa rha he usko bto se entity me convert krna mean toentity() method ko call krwa rha he
                                                                         ///2->Security & Account Link Setup (setActivationToken): /// User ka account verify karne ke liye ek unique verification token (UUID) generate karke profile me attach karta hai (taaki user ke email par activation link bheja ja sake).
            ProfileEntity newProfile = toEntity(profileDTO);  /// ye lineka mtlb he user ne jo form bahara tha rofileDTO), usko Database ke format (ProfileEntity) me badal kar newProfile me daala.
            newProfile.setActivationToken(UUID.randomUUID().toString());   //"Activation Token ek unique security code hota hai jo naye user ke email address ko verify karne aur fake/spam accounts ko rokne ke liye generate kiya jata hai."
       newProfile.setPassword(passwordEncoder.encode(newProfile.getPassword())); /// encoding the password
        newProfile=profileRepository.save(newProfile);
       //send activation mail
        String activationLink=   activationURL+"/activate?token="+newProfile.getActivationToken();
        String subject = "Profile Activation";
        String body ="click on the link to activate your account: " + activationLink;
       emailService.sendEmail(newProfile.getEmail(), subject,body );   //get mail mean reciver mail

        return toDTO(newProfile);


    }
    //profile dto to entity
    public ProfileEntity toEntity(ProfileDTO profileDTO) {              /// "Mujhe ek ProfileDTO do, main tumhe ek ProfileEntity dunga."
        return ProfileEntity.builder()               ///  here we convert DTO to enity conversion because we have  tosave in DB so covert in entity
                .id(profileDTO.getId())                 /// DTO ka id pick kro and entity ke id me rskho
                .FullName(profileDTO.getFullName())  /// User ne DTO me jo Name bheja, usko utha ke Entity ke Name field me dala
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())  //assword utha ke Entity me dala
                .profieImageUrl(profileDTO.getProfieImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();                       ///Aur poori Entity tayar karke return kar di!
    }
    /// this is
    /// totally opposite of totntity method mean to entity me hum dto ke vaues ko entity ke field me dal kr save kr rhew the lkin yaha pe entity ke data ko dto medsl rhe he
    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .FullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profieImageUrl(profileEntity.getProfieImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
    public boolean activateProfile(String activationToken) {

        return profileRepository.findByActivationToken(activationToken)  /// Token se user find kar raha hai
                .map(profile -> {

                    profile.setIsactive(true);    ///agar user mill gy TO TRUEW

                    profileRepository.save(profile);
//                    Email Link
//     ↓
//                    Controller
//     ↓
//                    ProfileService
//     ↓
//                    Find User By Token
//     ↓
//                    isActive = true
//     ↓
//                    Save
                    return true;

                })
                .orElse(false);
    }
//user ka account active hai ya nahi check karne ke liye
    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(profile -> profile.getIsactive())
                .orElse(false);
    }

    /// getCurrentProfile() → jo user already login hai uska data nikalne ke liye
    public ProfileEntity getCurrentProfile() { //"Abhi login kaun hai?"
      Authentication  authentication=SecurityContextHolder.getContext().getAuthentication();
      return profileRepository.findByEmail(authentication.getName())
              .orElseThrow(() -> new UsernameNotFoundException(
                      "profile is not found with email: " + authentication.getName()
              ));
    }
    public ProfileDTO getPublicProfile(String email) {
       ProfileEntity currentUser=null;
        if(email==null){
         currentUser=   getCurrentProfile();
        }
        else{
           currentUser= profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("profile is not found with email: " + email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .FullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profieImageUrl(currentUser.getProfieImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())

                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())); ///Mujhe email/password mila Check karo sahi hai ya nahi.
           ///  generate jwt token
        String token=jwtUtil.generateToken(authDTO.getEmail());   /// token bann rha he
            return Map.of("token",token,"user",getPublicProfile(authDTO.getEmail()));
        }

        catch (Exception e){
            throw new BadCredentialsException("Invalid username and password");
        }
       // Frontend
//     ↓
//        Login Controller
//     ↓
//        ProfileService
//     ↓
//        AuthenticationManager
//     ↓
//        AppUserDetailService
//     ↓
//        Database
//     ↓
//        UserDetails
//     ↓
//        Password Check
//     ↓
//        JWT Generate
//     ↓
//        Return Token
    }
}
