package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.MemberForm;
import com.constantine.polariscope.DTO.ResponseMessage;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("/api/account")
public class AccountAPI {
    private final UserService userService;

    private User getCurrentUser(Principal principal) throws Exception{
        if(principal == null){
            throw new Exception("No user found");
        }
        String username = principal.getName();;
        User user = (User) userService.loadUserByUsername(username);
        if(user.getRole().equals(User.Role.Owner)){
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
}
