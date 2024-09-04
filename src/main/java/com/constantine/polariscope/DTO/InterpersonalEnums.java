package com.constantine.polariscope.DTO;

import com.constantine.polariscope.Model.MemberGroup;
import com.constantine.polariscope.Model.Member;
import lombok.Getter;

import java.util.List;

@Getter
public class InterpersonalEnums {
    Member.Sex[] sex;
    Member.Sexuality[] sexuality;
    Member.RelationshipType[] relationship;
    List<MemberGroup> groups;

    public InterpersonalEnums(List<MemberGroup> p){
        groups = p;
        sex = Member.Sex.values();
        sexuality = Member.Sexuality.values();
        relationship = Member.RelationshipType.values();
    }
}
