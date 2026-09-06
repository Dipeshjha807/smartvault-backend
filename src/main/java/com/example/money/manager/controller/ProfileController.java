package com.example.money.manager.controller;

import com.example.money.manager.dto.AuthDTO;
import com.example.money.manager.dto.ProfileDTO;
import com.example.money.manager.service.ProfileService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService Profileservice;



@PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO)throws MessagingException {

        ProfileDTO registerProfile=Profileservice.registerProfile(profileDTO);         ///"Service bhai, ye ProfileDTO le aur registration ka kaam kar."

        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activeProfile(@RequestParam String token){

        boolean isActivated = Profileservice.activateProfile(token);

        if(isActivated){
            return ResponseEntity.ok("Profile activated successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Profile activation failed");
        }
    }
    @PostMapping("login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
    try{
        // check if the user is active or not if yes then ok or else forbidden
        if(!Profileservice.isAccountActive(authDTO.getEmail())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Account is not active⬇️. activate your account first"));
        }

       Map<String,Object>response= Profileservice.authenticateAndGenerateToken(authDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e){
        e.printStackTrace();
        throw new BadCredentialsException(e.getMessage());
    }
    }


}
