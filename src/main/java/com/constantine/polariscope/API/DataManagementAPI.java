package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.GroupForm;
import com.constantine.polariscope.DTO.ResponseMessage;
import com.constantine.polariscope.DTO.UserSaveData;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.SaveDataService;
import com.constantine.polariscope.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("/api/data")
public class DataManagementAPI {
    private SaveDataService saveDataService;
    private UserService userService;

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

    @GetMapping("/export")
    public ResponseEntity<?> getAllData(Principal principal){
        try{
            User retrievedUser = getCurrentUser(principal);
            byte[] data = saveDataService.retrieveUserData(retrievedUser);

            return ResponseEntity.ok(data);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error Retrieving User", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> setData(@RequestParam(value = "content") String info, Principal principal){ // todo: something is wrong with how file is handled or interpreted
        try{
            //User retrievedUser = getCurrentUser(principal);
            saveDataService.importUserData(info.getBytes());

            return ResponseEntity.ok().body(new ResponseMessage("User Data saved", ResponseMessage.Severity.INFORMATIONAL, "Tutorial is now dismissed"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Unable to dismiss tutorial", ResponseMessage.Severity.LOW, e.getMessage()));
        }
    }
}
