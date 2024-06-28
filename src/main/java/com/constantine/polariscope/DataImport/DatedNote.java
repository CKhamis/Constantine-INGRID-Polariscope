package com.constantine.polariscope.DataImport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class DatedNote implements Comparable<DatedNote>{
    private String note;
    private LocalDateTime timestamp;

    @Override
    public int compareTo(DatedNote other) {
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != getClass()){
            return false;
        }

        DatedNote other = (DatedNote) obj;
        return (note.equals(other.note) && timestamp.equals(other.timestamp));
    }
}
