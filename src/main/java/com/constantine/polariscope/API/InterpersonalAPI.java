package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.EvaluationForm;
import com.constantine.polariscope.DTO.MemberListItem;
import com.constantine.polariscope.DTO.NewMemberForm;
import com.constantine.polariscope.DTO.ResponseMessage;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/api/interpersonal")
public class InterpersonalAPI {
    private final MemberService memberService;
    private final UserService userService;
    private final EvaluationService evaluationService;
    private final int MAX_EVALUATIONS = 300;

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
        try{
            User retrievedUser = getCurrentUser(principal);

            // Get all users from repository
            List<MemberListItem> members = memberService.allMembers(retrievedUser);
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
                Member member = memberService.findMemberWithEval(id);

                if(member.getAuthor().getId().equals(retrievedUser.getId())){

                    // Reduce the number of attached evals for speed purposes
                    if(member.getTimeline().size() > MAX_EVALUATIONS){
                        member.setShortTimeline(member.getTimeline().subList(0, MAX_EVALUATIONS));
                    }else{
                        member.setShortTimeline(member.getTimeline());
                    }

                    if(!member.getTimeline().isEmpty()){
                        member.setMostRecentScore(member.getTimeline().get(member.getTimeline().size()-1).getCScore());
                    }

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

    @GetMapping("/evaluation/all/{id}")
    public ResponseEntity<?> viewEvaluations(@PathVariable UUID id, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            //Find evaluations. Give same error if member is not found or wrong user account
            try{
                Member subject = memberService.findMember(id);
                if(subject.getAuthor().getId().equals(retrievedUser.getId())){
                    return ResponseEntity.ok(subject.getTimeline());
                }else{
                    // Invalid user account
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Error Retrieving Evaluations", ResponseMessage.Severity.LOW, "Member not found"));
                }
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Error Retrieving Evaluations", ResponseMessage.Severity.LOW, "Member not found"));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Members", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/evaluation/save")
    public ResponseEntity<ResponseMessage> saveEvaluation(@Valid @RequestBody EvaluationForm form, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            try{
                Member member = memberService.findMember(form.getMemberId());

                if(retrievedUser.getId().equals(member.getAuthor().getId())){
                    // Member author matches logged in user

                    // Check to see if editing or creating new eval
                    if(form.getId() != null){
                        Optional<Evaluation> optionalEvaluation = evaluationService.findById(form.getId());
                        if(optionalEvaluation.isPresent()){
                            Evaluation existing = optionalEvaluation.get();

                            // Change values if different
                            if(!form.getNote().isEmpty()){
                                existing.setNote(form.getNote());
                            }

                            if(form.getCscore() != null){
                                existing.setCScore(form.getCscore());
                            }

                            if(form.getTimestamp() != null){
                                existing.setTimestamp(form.getTimestamp());
                            }

                            existing.setModified(LocalDateTime.now());

                            evaluationService.save(existing);
                            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Evaluation Edited", ResponseMessage.Severity.INFORMATIONAL, "Modified values of existing evaluation"));
                        }
                    }

                    // Create new eval
                    Evaluation newEval = new Evaluation(null, form.getTimestamp(), form.getNote(), form.getCscore(), member);
                    evaluationService.save(newEval);
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Evaluation Saved", ResponseMessage.Severity.INFORMATIONAL, "Evaluation has been saved to member"));
                }else{
                    // Member author does not match logged in user
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Evaluation", ResponseMessage.Severity.LOW, "Evaluation could not be saved"));
                }
            }catch (Exception e){
                // Member not found or database issue
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Evaluation", ResponseMessage.Severity.LOW, "Evaluation could not be saved"));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.LOW, "Error saving member"));
        }
    }
}
