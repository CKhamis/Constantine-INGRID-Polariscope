package com.constantine.polariscope.API;

import com.constantine.polariscope.Comprehension.ReportGenerator;
import com.constantine.polariscope.DTO.*;
import com.constantine.polariscope.Model.*;
import com.constantine.polariscope.Model.Event;
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

import javax.management.relation.RelationService;
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
    private final EventService eventService;
    private final RelationshipService relationshipService;
    private final ReportGenerator reportGenerator;

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

    @PostMapping("/member/edit-counter/reset")
    public ResponseEntity<ResponseMessage> deleteMemberEditCounts(Principal principal){
        try{
            User user = getCurrentUser(principal);

            // run function
            memberService.resetEditCounters(user);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Member Edit Counts Reset", ResponseMessage.Severity.INFORMATIONAL, "All member edit counts have been set to 0 and saved."));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Resetting Edit Counts", ResponseMessage.Severity.LOW, "Error with modifying edit counts"));
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

            // Find evaluations. Give same error if member is not found or wrong user account
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
                if(group.getAuthor().getId().equals(id)){
                    groupService.deleteGroup(group);
                    activityLogService.save(new ActivityLog(ActivityLog.ActivityType.GROUP_DELETED, user));
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
                activityLogService.save(new ActivityLog(ActivityLog.ActivityType.GROUP_CREATED, user));
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Group Saved", ResponseMessage.Severity.INFORMATIONAL, "Group was created and saved to memory"));
            }

            // Get id of evaluation
            Optional<MemberGroup> optionalGroup = groupService.findGroupById(form.getId());

            if(optionalGroup.isPresent()){
                MemberGroup group = optionalGroup.get();

                // Check if group is owned by logged in user
                if(group.getAuthor().getId().equals(user.getId())){
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
                    activityLogService.save(new ActivityLog(ActivityLog.ActivityType.GROUP_MODIFIED, user));
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

    @PostMapping("/event/save")
    public ResponseEntity<ResponseMessage> saveEvent(@Valid @RequestBody EventForm formElements, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            if(formElements.getId() != null){
                // Save to existing event

                Optional<Event> optionalEvent = eventService.findById(formElements.getId());
                if(optionalEvent.isPresent()){
                    // Check if event author matches logged in user
                    Event retrievedEvent = optionalEvent.get();

                    if(retrievedEvent.getAuthor().getId().equals(retrievedUser.getId())){

                        try{
                            if(!formElements.getLabel().isEmpty()){
                                retrievedEvent.setLabel(formElements.getLabel());
                            }

                            if(!formElements.getDescription().isEmpty()){
                                retrievedEvent.setDescription(formElements.getDescription());
                            }

                            retrievedEvent.setDate(formElements.getDate());

                            retrievedEvent.setColor(new Color(formElements.getRed(), formElements.getGreen(), formElements.getBlue()));

                            eventService.save(retrievedEvent);
                            activityLogService.save(new ActivityLog(ActivityLog.ActivityType.EVENT_MODIFIED, retrievedUser));

                            return ResponseEntity.ok(new ResponseMessage("Member Saved", ResponseMessage.Severity.INFORMATIONAL, "Member details saved"));

                        }catch (Exception e){
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving Event", ResponseMessage.Severity.MEDIUM, e.getMessage()));
                        }
                    }else{
                        // Author did not write the event
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Error Saving Event", ResponseMessage.Severity.LOW, "ID not found"));
                    }
                }else{
                    // Event not found. Throw an error
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Error Saving Event", ResponseMessage.Severity.LOW, "ID not found"));
                }
            }else{
                try{
                    // Save to new user
                    Event event = new Event(retrievedUser, formElements);

                    eventService.save(event);
                    activityLogService.save(new ActivityLog(ActivityLog.ActivityType.EVENT_CREATED, retrievedUser));
                    return ResponseEntity.ok(new ResponseMessage("Event Saved", ResponseMessage.Severity.INFORMATIONAL, "Event saved to Polariscope"));

                }catch (Exception e){
                    // Error with saving event
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Saving Event", ResponseMessage.Severity.MEDIUM, e.getMessage()));
                }
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Event", ResponseMessage.Severity.LOW, "OOOPS :)"));
        }
    }

    @GetMapping("/event/all")
    public ResponseEntity<?> allEvents(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            // Get all users from repository
            List<Event> events = eventService.findAllByAuthor(retrievedUser);

            return ResponseEntity.ok(events);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Events", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/event/delete")
    public ResponseEntity<ResponseMessage> deleteEvent(@RequestBody UUID id, Principal principal){
        try{
            User user = getCurrentUser(principal);

            // Get id of evaluation
            Optional<Event> optionalEvent = eventService.findById(id);

            if(optionalEvent.isPresent()){
                Event event = optionalEvent.get();

                // Check if event is owned by logged in user
                if(event.getAuthor().getId().equals(user.getId())){
                    eventService.delete(event);
                    activityLogService.save(new ActivityLog(ActivityLog.ActivityType.GROUP_DELETED, user));
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Event Deleted", ResponseMessage.Severity.INFORMATIONAL, "Event was deleted."));
                }else{
                    // event author does not match with logged in user
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Group", ResponseMessage.Severity.LOW, "Authentication issue"));
                }
            }else{
                // Event not found
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Group", ResponseMessage.Severity.LOW, "Group could not be found"));
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Deleting Group", ResponseMessage.Severity.LOW, "OOPS! There was an error :("));
        }
    }

    @PostMapping("/event/change-member")
    public ResponseEntity<ResponseMessage> addMemberToEvent(@RequestBody EventConnectionForm form, Principal principal){
        try{
            User user = getCurrentUser(principal);

            // Get id of evaluation
            Optional<Event> optionalEvent = eventService.findById(form.getEventId());

            if(optionalEvent.isPresent()){
                Event event = optionalEvent.get();

                // Check if event is owned by logged in user
                if(event.getAuthor().getId().equals(user.getId())){

                    // get member
                    try{
                        Member member = memberService.findMember(form.getMemberId());

                        // Check if member is owned by logged in user
                        if(member.getAuthor().getId().equals(user.getId())){

                            // Actually add or remove the connection

                            Set<Event> events = member.getEvents();
                            if(events.contains(event)){
                                // disconnect
                                events.remove(event);
                            }else{
                                // add in connection
                                events.add(event);
                            }
                            member.setEvents(events);

                            event.getMembers().add(member);

                            memberService.save(member);
                            eventService.save(event);
                            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Event Added to Member", ResponseMessage.Severity.INFORMATIONAL, "Event was added to member."));
                        }else{
                            // Event is owned by logged in user, but member is not
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Adding Event to Member", ResponseMessage.Severity.LOW, "Authentication issue"));
                        }
                    }catch (Exception e){
                        // Member not found
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Adding Event to Member", ResponseMessage.Severity.LOW, "Member was not found"));
                    }
                }else{
                    // event author does not match with logged in user
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Adding Event to Member", ResponseMessage.Severity.LOW, "Authentication issue"));
                }
            }else{
                // Event not found
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Adding Event to Member", ResponseMessage.Severity.LOW, "Event was not found"));
            }
        }catch (Exception e){
            // Auth issue or database issue
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Adding Event to Member", ResponseMessage.Severity.LOW, "OOPS! There was an error :("));
        }
    }

    @PostMapping("/relationship/save")
    public ResponseEntity<ResponseMessage> saveRelationship(@Valid @RequestBody MemberRelationshipForm formElements, Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            // Check if members are owned by existing user
            Member self = memberService.findMember(formElements.getSelf());
            Member other = memberService.findMember(formElements.getOther());

            if(self.getAuthor().getId().equals(retrievedUser.getId()) && other.getAuthor().getId().equals(retrievedUser.getId())){
                // Members are both created by logged in user. Check if there is a relationship that already exists
                Optional<Relationship> optionalRelationship = relationshipService.findById(formElements.getSelf().toString() + formElements.getOther().toString());

                if(optionalRelationship.isPresent()){
                    // Check if deleting
                    if(formElements.getHealth() == null){
                        relationshipService.delete(formElements.getSelf().toString() + formElements.getOther().toString());
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Relationship Deleted", ResponseMessage.Severity.LOW, "Details have been updated"));
                    }


                    // Relationship exists
                    Relationship relationship = optionalRelationship.get();
                    relationship.setHealth(formElements.getHealth());
                    relationship.setType(formElements.getType());
                    relationship.setLastModified(LocalDateTime.now());
                    relationship.setDescription(formElements.getDescription());
                    relationshipService.save(relationship);
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Relationship Saved", ResponseMessage.Severity.LOW, "Details have been updated"));

                }else{
                    // Relationship does not exist. Create one
                    relationshipService.save(new Relationship(retrievedUser, self, other, formElements.getHealth(), formElements.getDescription(), formElements.getType()));
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Relationship Saved", ResponseMessage.Severity.LOW, "New relationship added"));
                }
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Error Saving Relationship", ResponseMessage.Severity.LOW, "Authentication error"));

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Relationship", ResponseMessage.Severity.LOW, "OOOPS :)"));
        }
    }

    @PostMapping("/relationship/delete")
    public ResponseEntity<ResponseMessage> deleteRelationship(@RequestBody String id, Principal principal){
        try{
            User user = getCurrentUser(principal);

            // Get id of evaluation
            Optional<Relationship> optionalRelationship = relationshipService.findById(id);
            if(optionalRelationship.isPresent()){
                Relationship relationship = optionalRelationship.get();
                if(relationship.getAuthor().getId().equals(user.getId())){
                    relationshipService.delete(id);
                }
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Error Deleting Relationship", ResponseMessage.Severity.LOW, "Authentication error"));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Saving Evaluation", ResponseMessage.Severity.LOW, "Error deleting evaluation"));
        }
    }

    @GetMapping("/relationship/all")
    public ResponseEntity<?> allRelationships(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            // Get all users from repository
            List<Relationship> relationships = relationshipService.findAll(retrievedUser);

            return ResponseEntity.ok(relationships);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving Relationships", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @GetMapping("/reports/quarterly")
    public ResponseEntity<?> quarterly(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);

            if(reportGenerator.isQuarterlyReportFinished()){
                return ResponseEntity.ok(reportGenerator.getQuarterlyReportList());
            }

            return ResponseEntity.status(HttpStatus.LOCKED).body(new ResponseMessage("Error Getting Report", ResponseMessage.Severity.LOW, "Quarterly Reports have not yet been generated. Please check back soon."));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Getting Report", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }
}
