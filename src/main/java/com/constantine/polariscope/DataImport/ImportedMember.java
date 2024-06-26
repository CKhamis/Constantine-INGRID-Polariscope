package com.constantine.polariscope.DataImport;

import com.constantine.polariscope.Model.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ImportedMember{
    Member member;
    List<ImportError> importErrorList;

    public ImportedMember(){
        member = new Member();
        importErrorList = new ArrayList<>();
    }
}
