package com.constantine.polariscope.DataImport;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConvertedMember {
    private Member member;
    private List<Evaluation> timeline;
}
