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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

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
        User user = (User) userService.loadUserByUsername(username);
        if(user.getRole().equals(User.Role.Owner)){
            return user;
        }
        throw new Exception("Invalid user");
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.LOW, "Error saving member"));
        }
    }

    @GetMapping("/member/all")
    public ResponseEntity<?> allMembers(Principal principal){
        //todo: Create a DTO that contains less information about the members
        try{
            User retrievedUser = getCurrentUser(principal);

            // Get all users from repository
            List<Member> members = memberService.allMembers(retrievedUser);
            return ResponseEntity.ok(members);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Members", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @GetMapping("/member/view/{id}")
    public ResponseEntity<?> viewMember(@PathVariable UUID id, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            try{
                Member member = memberService.findMember(id);

                if(member.getAuthor().getId().equals(retrievedUser.getId())){
                    return ResponseEntity.ok(member);
                }else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Member", ResponseMessage.Severity.LOW, "Invalid Account"));
                }
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Error Retrieving Member", ResponseMessage.Severity.LOW, "Member not found"));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Member", ResponseMessage.Severity.LOW, "Invalid Account"));
        }
    }
}
