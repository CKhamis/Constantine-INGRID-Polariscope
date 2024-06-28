package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.EvaluationForm;
import com.constantine.polariscope.DTO.MemberForm;
import com.constantine.polariscope.DTO.MemberListItem;
import com.constantine.polariscope.DTO.ResponseMessage;
import com.constantine.polariscope.DataImport.ImportedMember;
import com.constantine.polariscope.DataImport.IngridMapper;
import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.EvaluationService;
import com.constantine.polariscope.Service.MemberService;
import com.constantine.polariscope.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/api/ingrid")
public class IngridAPI {
    private final UserService userService;
    private final IngridMapper mapper;

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

    @PostMapping("/upload")
    public ResponseEntity<?> uploadData(@RequestParam("file")MultipartFile file, Principal principal){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body(new ResponseMessage("Invalid File", ResponseMessage.Severity.MEDIUM, "The file is empty"));
        }

        try{
            List<ImportedMember> members = mapper.extractMembers(file, getCurrentUser(principal));
            return ResponseEntity.ok().body(members);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ResponseMessage("Invalid File", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }
}
