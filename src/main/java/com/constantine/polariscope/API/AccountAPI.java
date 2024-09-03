package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.MemberForm;
import com.constantine.polariscope.DTO.NewUserForm;
import com.constantine.polariscope.DTO.ResponseMessage;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.ActivityLogService;
import com.constantine.polariscope.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping("/api/account")
public class AccountAPI {
    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private User getCurrentUser(Principal principal) throws Exception{
        if(principal == null){
            throw new Exception("No user found");
        }
        String username = principal.getName();;
        User user = (User) userService.loadUserByUsername(username);
        if(user.getRole().equals(User.Role.Owner) || user.getRole().equals(User.Role.ADMIN)){
            return user;
        }
        throw new Exception("Invalid user");
    }

    @GetMapping("/info/current")
    public ResponseEntity<?> getUserInfo(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);
            return ResponseEntity.ok(retrievedUser);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving User", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @GetMapping("/activity/all")
    public ResponseEntity<?> getAllActivity(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);
            return ResponseEntity.ok(activityLogService.findAll(retrievedUser));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving User", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @GetMapping("/activity/after/{date}")
    public ResponseEntity<?> getAllActivity(@PathVariable LocalDate date, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);
            return ResponseEntity.ok(activityLogService.findAllAfter(retrievedUser, date.atStartOfDay()));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving User", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/dismiss-tutorial")
    public ResponseEntity<?> dismissTutorial(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);
            retrievedUser.setTutorialNeeded(false);
            userService.save(retrievedUser);
            return ResponseEntity.ok().body(new ResponseMessage("User Data saved", ResponseMessage.Severity.INFORMATIONAL, "Tutorial is now dismissed"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Unable to dismiss tutorial", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> newMember(@RequestBody NewUserForm form, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            if(retrievedUser.getRole().equals(User.Role.Owner)){
                User newUser = new User();
                newUser.setUsername(form.getUsername());
                newUser.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
                newUser.setRole(User.Role.ADMIN);
                newUser.setEnabled(true);
                userService.save(newUser);
                return ResponseEntity.ok().body(new ResponseMessage("New User Created", ResponseMessage.Severity.INFORMATIONAL, "New user is created"));
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("New User Not Created", ResponseMessage.Severity.MEDIUM, "User could not be created due to invalid credentials"));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Unable to dismiss tutorial", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }
}
