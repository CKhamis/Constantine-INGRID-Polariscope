package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.*;
import com.constantine.polariscope.Model.*;
import com.constantine.polariscope.Service.*;
import com.constantine.polariscope.Util.FileValidator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/interpersonal")
public class InterpersonalAPI {
    private final MemberService memberService;
    private final UserService userService;
    private final EvaluationService evaluationService;
    private final MemberGroupService groupService;
    private final ActivityLogService activityLogService;
    private final MemberGroupService memberGroupService;

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


    @PostMapping("/member/save")
    public ResponseEntity<ResponseMessage> saveMember(@Valid @ModelAttribute MemberForm formElements, Principal principal, @RequestParam(value = "image", required = false) MultipartFile file){
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

                        if(formElements.getGroup() != null){
                            // UUID provided
                            Optional<MemberGroup> optionalGroup = groupService.findGroupById(formElements.getGroup());
                            if(optionalGroup.isPresent()){
                                //UUID matches with an existing UUID
                                MemberGroup group = optionalGroup.get();
                                if(group.getAuthor().getId().equals(retrievedUser.getId())){
                                    // UUID is owned by logged in user
                                    retrievedMember.setGroup(group);
                                }
                            }
                        }else{
                            retrievedMember.setGroup(null);
                        }

                        // Profile image
                        if (file != null && !file.isEmpty()) {
                            String fileType = FileValidator.getImageFileType(file);
                            if(!fileType.isEmpty()){
                                retrievedMember.setProfileImageType(fileType);
                                retrievedMember.setProfileImageData(file.getBytes());
                                retrievedMember.setProfileImageTimestamp(LocalDateTime.now());
                            }else{
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.MEDIUM, "Profile image filetype was invalid. No changes were saved to the member. Please make sure to provide a valid image file."));
                            }
                        }

                        retrievedMember.setArchive(formElements.isArchive());
                        retrievedMember.setScoreHold(formElements.isScoreHold());

                        memberService.save(retrievedMember);

                        activityLogService.save(new ActivityLog(ActivityLog.ActivityType.MEMBER_MODIFIED, retrievedUser));
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

                if(formElements.getGroup() != null){
                    // UUID provided
                    Optional<MemberGroup> optionalGroup = groupService.findGroupById(formElements.getGroup());
                    if(optionalGroup.isPresent()){
                        //UUID matches with an existing UUID
                        MemberGroup group = optionalGroup.get();
                        if(group.getAuthor().getId().equals(retrievedUser.getId())){
                            // UUID is owned by logged in user
                            member.setGroup(group);
                        }
                    }
                }

                // Profile image
                if (file != null && !file.isEmpty()) {
                    String fileType = FileValidator.getImageFileType(file);
                    if(!fileType.isEmpty()){
                        member.setProfileImageType(fileType);
                        member.setProfileImageData(file.getBytes());
                        member.setProfileImageTimestamp(LocalDateTime.now());
                    }else{
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.MEDIUM, "Profile image filetype was invalid. No changes were saved to the member. Please make sure to provide a valid image file."));
                    }
                }

                memberService.save(member);
                activityLogService.save(new ActivityLog(ActivityLog.ActivityType.MEMBER_CREATED, retrievedUser));
                return ResponseEntity.ok(new ResponseMessage("Member Saved", ResponseMessage.Severity.INFORMATIONAL, "Member saved to Polariscope"));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Member", ResponseMessage.Severity.LOW, "Error saving member"));
        }
    }

    @PostMapping("/member/change-group")
    public ResponseEntity<ResponseMessage> deleteMember(@RequestBody GroupChangeForm form, Principal principal){
        try{
            User user = getCurrentUser(principal);

            UUID memberId = UUID.fromString(form.getMemberId());

            Member member = memberService.findMember(memberId);
            if(member.getAuthor().getId().equals(user.getId())){

                if(!form.getGroupId().isEmpty()){
                    // Add to group
                    UUID groupId = UUID.fromString(form.getGroupId());

                    Optional<MemberGroup> optionalMemberGroup = groupService.findGroupById(groupId);

                    if(optionalMemberGroup.isPresent()){
                        MemberGroup group = optionalMemberGroup.get();

                        if(group.getAuthor().getId().equals(user.getId())){
                            member.setGroup(group);
                            memberService.save(member);
                            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Member Group Changed", ResponseMessage.Severity.INFORMATIONAL, "Member group changed and saved."));
                        }else{
                            // user does not own group
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Changing Group", ResponseMessage.Severity.LOW, "Authentication error"));
                        }
                    }else{
                        // group not found
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Changing Group", ResponseMessage.Severity.LOW, "Authentication error"));
                    }
                }else{
                    // Remove current group
                    member.setGroup(null);
                    memberService.save(member);
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Member Group Changed", ResponseMessage.Severity.INFORMATIONAL, "Member group removed from member."));
                }
            }else{
                // user does not own member
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Changing Group", ResponseMessage.Severity.LOW, "Authentication error"));
            }
        }catch (Exception e){
            // No user/ not found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Changing Group", ResponseMessage.Severity.LOW, "Authentication error"));
        }
    }

    @PostMapping("/member/delete")
    public ResponseEntity<ResponseMessage> deleteMember(@RequestBody UUID id, Principal principal){
        try{
            User user = getCurrentUser(principal);

            // Get id of member
            Member member = memberService.findMember(id);

            if(member.getAuthor().getId().equals(user.getId())){
                memberService.deleteMember(member);
                activityLogService.save(new ActivityLog(ActivityLog.ActivityType.MEMBER_DELETED, user));
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Member Deleted", ResponseMessage.Severity.INFORMATIONAL, "Member and associated evaluations have been permanently deleted from member repository"));
            }else{
                // Member author does not match with logged-in user
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Member", ResponseMessage.Severity.LOW, "Please log in using correct id"));
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Member", ResponseMessage.Severity.LOW, "Error deleting evaluation"));
        }
    }

    @GetMapping("/member/profile-image/" + "{memberId}")
    public ResponseEntity<?> downloadMemberFile(@PathVariable UUID memberId, Principal principal) {
        try{
            Member retrievedMember = memberService.findMember(memberId);
            if(retrievedMember.getAuthor().equals(getCurrentUser(principal))){
                if(retrievedMember.getProfileImageData() == null){
                    return getDefaultImageResponse("static/icons/Default User.svg");
                }else{
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType("image/" + retrievedMember.getProfileImageType()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= \"" + retrievedMember.getFirstName() + "_" + retrievedMember.getLastName() + "\"")
                            .body(new ByteArrayResource(retrievedMember.getProfileImageData()));
                }
            }else{
                return getDefaultImageResponse("static/icons/Default User.svg");
            }
        }catch (Exception e){
            return getDefaultImageResponse("static/icons/Default User.svg");
        }
    }

    @GetMapping("/group/image/" + "{id}")
    public ResponseEntity<?> downloadGroupFile(@PathVariable UUID id, Principal principal) {
        try{
            Optional<MemberGroup> retrievedGroup = groupService.findGroupById(id);
            if(retrievedGroup.isPresent()){
                MemberGroup group = retrievedGroup.get();
                if(group.getAuthor().equals(getCurrentUser(principal))){
                    if(group.getProfileImageData() != null){
                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType("image/" + group.getProfileImageType()))
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= \"" + group.getName() + "\"")
                                .body(new ByteArrayResource(group.getProfileImageData()));

                    }else{
                        // Group does not have an image
                        return getDefaultImageResponse("static/icons/Default Group.svg");
                    }
                }else{
                    // Group is not owned by currently logged in user
                    return getDefaultImageResponse("static/icons/Default Group.svg");
                }
            }else{
                // Group not found
                return getDefaultImageResponse("static/icons/Default Group.svg");
            }
        }catch (Exception e){
            // Misc. issue
            return getDefaultImageResponse("static/icons/Default Group.svg");
        }
    }

    private ResponseEntity<?> getDefaultImageResponse(String filePath) {
        try {
            Resource resource = new ClassPathResource(filePath);
            byte[] defaultImageData = StreamUtils.copyToByteArray(resource.getInputStream());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/svg+xml"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"default_image.svg\"")
                    .body(new ByteArrayResource(defaultImageData));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Member Not Found", ResponseMessage.Severity.LOW, "Member could not be found in database"));
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

    @GetMapping("/member/group/{id}")
    public ResponseEntity<?> allMembers(Principal principal, @PathVariable UUID id){
        try{
            User retrievedUser = getCurrentUser(principal);

            Optional<MemberGroup> optionalGroup = groupService.findGroupById(id);
            if(optionalGroup.isPresent()){
                if(optionalGroup.get().getAuthor().equals(retrievedUser)){
                    MemberGroup group = optionalGroup.get();

                    // Get all users from repository
                    List<Member> members = memberGroupService.findMembersByGroup(group);
                    return ResponseEntity.ok(members);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Members", ResponseMessage.Severity.LOW, "Authentication error"));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Members", ResponseMessage.Severity.LOW, "Group not found"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Members", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @GetMapping("/enums")
    public ResponseEntity<?> enums(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            // Get all places from repository
            List<MemberGroup> groups = groupService.findAll(retrievedUser);
            return ResponseEntity.ok(new InterpersonalEnums(groups));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Enums", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }


    @GetMapping("/member/view/{id}")
    public ResponseEntity<?> viewMember(@PathVariable UUID id, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            try{
                Member member = memberService.findMemberWithEval(id);

                if(member.getAuthor().getId().equals(retrievedUser.getId())){

                    if(!member.getTimeline().isEmpty()){
                        member.setMostRecentEval(member.getTimeline().get(0));
                        member.setFullTimeline(member.getTimeline());
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


                    return ResponseEntity.ok(memberService.findEvaluationByMember(subject));
                }else{
                    // Invalid user account
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Error Retrieving Evaluations", ResponseMessage.Severity.LOW, "Member not found"));
                }
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Error Retrieving Evaluations", ResponseMessage.Severity.LOW, "Member not found"));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Evaluations", ResponseMessage.Severity.LOW, e.getMessage()));
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
                            activityLogService.save(new ActivityLog(ActivityLog.ActivityType.EVALUATION_MODIFIED, retrievedUser));
                            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Evaluation Edited", ResponseMessage.Severity.INFORMATIONAL, "Modified values of existing evaluation"));
                        }
                    }

                    if(form.getCscore() == null){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Evaluation", ResponseMessage.Severity.LOW, "Missing cScore"));
                    }
                    // Create new eval
                    Evaluation newEval = new Evaluation(null, form.getTimestamp(), form.getNote(), form.getCscore(), member);
                    evaluationService.save(newEval);
                    activityLogService.save(new ActivityLog(ActivityLog.ActivityType.EVALUATION_CREATED, retrievedUser));
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

    @PostMapping("/evaluation/delete")
    public ResponseEntity<ResponseMessage> deleteEvaluation(@RequestBody UUID id, Principal principal){
        try{
            User user = getCurrentUser(principal);

            // Get id of evaluation
            Optional<Evaluation> optionalEvaluation = evaluationService.findById(id);

            if(optionalEvaluation.isPresent()){
                Evaluation evaluation = optionalEvaluation.get();

                // Check if member is owned by logged in user
                Member member = evaluation.getMember();

                if(member.getAuthor().getId().equals(user.getId())){
                    evaluationService.delete(evaluation);
                    activityLogService.save(new ActivityLog(ActivityLog.ActivityType.EVALUATION_DELETED, user));
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Evaluation Saved", ResponseMessage.Severity.INFORMATIONAL, "Evaluation has been saved to member"));
                }else{
                    // Member author does not match with logged in user
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Evaluation", ResponseMessage.Severity.LOW, "Evaluation could not be deleted"));
                }
            }else{
                // Evaluation not found
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Evaluation", ResponseMessage.Severity.LOW, "Evaluation could not be deleted"));
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Evaluation", ResponseMessage.Severity.LOW, "Error deleting evaluation"));
        }
    }

    @PostMapping("/group/delete")
    public ResponseEntity<ResponseMessage> deleteGroup(@RequestBody UUID id, Principal principal){
        try{
            User user = getCurrentUser(principal);

            // Get id of evaluation
            Optional<MemberGroup> optionalGroup = groupService.findGroupById(id);

            if(optionalGroup.isPresent()){
                MemberGroup group = optionalGroup.get();

                // Check if group is owned by logged in user
                User author = group.getAuthor();

                if(user.getId().equals(author.getId())){
                    groupService.deleteGroup(group);
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Group Deleted", ResponseMessage.Severity.INFORMATIONAL, "Group and associations have been permanently deleted"));
                }else{
                    // Group author does not match with logged in user
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Group", ResponseMessage.Severity.LOW, "Authentication issue"));
                }
            }else{
                // Evaluation not found
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Group", ResponseMessage.Severity.LOW, "Group could not be found"));
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Group", ResponseMessage.Severity.LOW, "OOPS! There was an error :("));
        }
    }

    @PostMapping("/group/save")
    public ResponseEntity<ResponseMessage> saveGroup(@Valid @ModelAttribute GroupForm form, Principal principal, @RequestParam(value = "image", required = false) MultipartFile file){
        try{
            User user = getCurrentUser(principal);

            if(form.getId() == null){
                // Save brand new group
                MemberGroup newGroup = new MemberGroup(form, user);
                newGroup.setColor(new Color(form.getRed(), form.getGreen(), form.getBlue()));

                if (file != null && !file.isEmpty()) {
                    String fileType = FileValidator.getImageFileType(file);
                    if(!fileType.isEmpty()){
                        newGroup.setProfileImageType(fileType);
                        newGroup.setProfileImageData(file.getBytes());
                        newGroup.setProfileImageTimestamp(LocalDateTime.now());
                    }else{
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Group", ResponseMessage.Severity.MEDIUM, "Image filetype was invalid. No changes were saved to the member. Please make sure to provide a valid image file."));
                    }
                }

                groupService.saveGroup(newGroup);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Group Saved", ResponseMessage.Severity.INFORMATIONAL, "Group was created and saved to memory"));
            }

            // Get id of evaluation
            Optional<MemberGroup> optionalGroup = groupService.findGroupById(form.getId());

            if(optionalGroup.isPresent()){
                MemberGroup group = optionalGroup.get();

                // Check if group is owned by logged in user
                User author = group.getAuthor();

                if(user.getId().equals(author.getId())){
                    // Edit existing group
                    group.setName(form.getName());
                    group.setColor(new Color(form.getRed(), form.getGreen(), form.getBlue()));
                    group.setDescription(form.getDescription());

                    if (file != null && !file.isEmpty()) {
                        String fileType = FileValidator.getImageFileType(file);
                        if(!fileType.isEmpty()){
                            group.setProfileImageType(fileType);
                            group.setProfileImageData(file.getBytes());
                            group.setProfileImageTimestamp(LocalDateTime.now());
                        }else{
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Group", ResponseMessage.Severity.MEDIUM, "Image filetype was invalid. No changes were saved to the member. Please make sure to provide a valid image file."));
                        }
                    }

                    groupService.saveGroup(group);

                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Group Saved", ResponseMessage.Severity.INFORMATIONAL, "Group info has been modified"));
                }else{
                    // Group author does not match with logged in user
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Group", ResponseMessage.Severity.LOW, "Authentication issue"));
                }
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Group", ResponseMessage.Severity.LOW, "ID is invalid"));
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Group", ResponseMessage.Severity.LOW, "OOPS! There was an error :("));
        }
    }
}
