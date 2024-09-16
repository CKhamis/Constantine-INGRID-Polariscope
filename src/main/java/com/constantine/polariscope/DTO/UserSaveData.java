package com.constantine.polariscope.DTO;

import com.constantine.polariscope.Model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserSaveData implements Serializable {
    public static final String VERSION = "1.0.0";

    private User author;
    private ArrayList<Member> members;
    private ArrayList<ActivityLog> activity;
    private ArrayList<Evaluation> evaluations;
    private ArrayList<MemberGroup> groups;
}
