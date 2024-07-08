package com.constantine.polariscope.DTO;

import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.Place;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InterpersonalEnums {
    Member.Sex[] sex;
    Member.Sexuality[] sexuality;
    Member.RelationshipType[] relationship;
    List<Place> places;

    public InterpersonalEnums(List<Place> p){
        places = p;
        sex = Member.Sex.values();
        sexuality = Member.Sexuality.values();
        relationship = Member.RelationshipType.values();
    }
}
