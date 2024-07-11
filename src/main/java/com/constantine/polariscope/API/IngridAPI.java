package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.EvaluationForm;
import com.constantine.polariscope.DTO.MemberForm;
import com.constantine.polariscope.DTO.MemberListItem;
import com.constantine.polariscope.DTO.ResponseMessage;
import com.constantine.polariscope.DataImport.ImportedMember;
import com.constantine.polariscope.DataImport.IngridMapper;
import com.constantine.polariscope.DataImport.IngridMember;
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
    private final MemberService memberService;
    private final EvaluationService evaluationService;

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

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage> saveMember(@Valid @RequestBody IngridMember formElements, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            if(formElements.getId() != null){
                // Save to existing user

                try{
                    Member retrievedMember = memberService.findMember(UUID.fromString(formElements.getId()));
                    // Check if member author matches logged in user

                    if(retrievedMember.getAuthor().getId().equals(retrievedUser.getId())){
                        if(!formElements.getFirstName().isEmpty()){
                            retrievedMember.setFirstName(formElements.getFirstName());
                        }
                        if(!formElements.getMiddleName().isEmpty()){
                            retrievedMember.setMiddleName(formElements.getMiddleName());
                        }
                        if(!formElements.getLastName().isEmpty()){
                            retrievedMember.setLastName(formElements.getLastName());
                        }
                        if(formElements.getAgeMet() != null){
                            retrievedMember.setAgeMet(formElements.getAgeMet());
                        }
                        if(formElements.getBirthday() != null){
                            retrievedMember.setBirthday(formElements.getBirthday());
                        }
                        if(!formElements.getFavoriteColor().isEmpty()){
                            retrievedMember.setFavoriteColor(formElements.getFavoriteColor());
                        }
                        if(!formElements.getPersonality().isEmpty()){
                            retrievedMember.setPersonality(formElements.getPersonality());
                        }
                        if(!formElements.getDescription().isEmpty()){
                            retrievedMember.setDescription(formElements.getDescription());
                        }
                        if(!formElements.getRelationship().isEmpty()){
                            retrievedMember.setRelationship(Member.RelationshipType.valueOf(formElements.getRelationship()));
                        }
                        if(!formElements.getSex().isEmpty()){
                            retrievedMember.setSex(Member.Sex.valueOf(formElements.getSex()));
                        }
                        if(!formElements.getSexuality().isEmpty()){
                            retrievedMember.setSexuality(Member.Sexuality.valueOf(formElements.getSexuality()));
                        }

                        Member saved = memberService.save(retrievedMember);

                        for (Evaluation evaluation : formElements.getTimeline()) {
                            evaluationService.save(new Evaluation(null, evaluation.getTimestamp(), evaluation.getNote(), evaluation.getCScore(), saved));
                        }
                        return ResponseEntity.ok(new ResponseMessage("Member Saved", ResponseMessage.Severity.INFORMATIONAL, "Member details saved"));
                    }else{
                        // Throw the same error
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.MEDIUM, "ID not found"));
                    }
                }catch (Exception e){
                    // Member not found. Throw an error
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.MEDIUM, "ID not found"));
                }
            }else{
                // Save to new user
                Member member = new Member(formElements, retrievedUser);
                Member saved = memberService.save(member);

                for (Evaluation evaluation : formElements.getTimeline()) {
                    evaluationService.save(new Evaluation(null, evaluation.getTimestamp(), evaluation.getNote(), evaluation.getCScore(), saved));
                }
                return ResponseEntity.ok(new ResponseMessage("Member Saved", ResponseMessage.Severity.INFORMATIONAL, "Member imported Polariscope"));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.LOW, "Error saving member"));
        }
    }
}
