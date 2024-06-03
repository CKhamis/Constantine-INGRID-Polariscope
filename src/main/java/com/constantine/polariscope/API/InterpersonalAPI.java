package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.NewMemberForm;
import com.constantine.polariscope.DTO.ResponseMessage;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.MemberService;
import com.constantine.polariscope.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("/api/interpersonal")
public class InterpersonalAPI {
    private final MemberService memberService;
    private final UserService userService;

    private User getCurrentUser(Principal principal) throws Exception{
        if(principal == null){
            throw new Exception("No user found");
        }
        String username = principal.getName();;
        return (User) userService.loadUserByUsername(username);
    }

    @PostMapping("/member/new")
    public ResponseEntity<ResponseMessage> newMember(@Valid @RequestBody NewMemberForm formElements, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            // Construct new user
            Member member = new Member(formElements, retrievedUser);
            memberService.save(member);
            return ResponseEntity.ok(new ResponseMessage("Member Saved", ResponseMessage.Severity.INFORMATIONAL, "Member saved to Polariscope"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }
}
