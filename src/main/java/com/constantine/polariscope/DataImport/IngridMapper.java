package com.constantine.polariscope.DataImport;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class IngridMapper {
    private final int TABLE_HEADER_ROW = 4;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
    public List<ImportedMember> extractMembers(MultipartFile file) throws Exception{
        List<ImportedMember> members = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream()); CSVReader csvReader = new CSVReader(reader)) {

            // Skip decoration rows
            for (int i = 0; i < TABLE_HEADER_ROW - 1; i++) {
                csvReader.readNext();
            }

            String[] tableHeader = csvReader.readNext();

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null && !nextLine[0].equals("Average")) {
                // Create a new member
                ImportedMember memberPacket = new ImportedMember();


                // Map basic member attributes
                memberPacket.member.setFirstName(nextLine[0]);
                memberPacket.member.setMiddleName(nextLine[1]);
                memberPacket.member.setLastName(nextLine[2]);
                // Account and logo not stored
                try{
                    memberPacket.member.setRelationship(Member.RelationshipType.valueOf(nextLine[5].toUpperCase()));
                }catch(Exception e){
                    memberPacket.importErrorList.add(new ImportError("Error parsing relationship", e.getMessage()));
                }


                try{
                    memberPacket.member.setSexuality(Member.Sexuality.valueOf(nextLine[6].toUpperCase()));
                }catch(Exception e){
                    memberPacket.importErrorList.add(new ImportError("Error parsing sexuality", e.getMessage()));
                }


                memberPacket.member.setPersonality(nextLine[7] + nextLine[8] + nextLine[9] + nextLine[10]);
                memberPacket.member.setFavoriteColor(nextLine[11]);
                memberPacket.member.setDescription(nextLine[12]);


                try{
                    memberPacket.member.setSex(Member.Sex.valueOf(nextLine[13].toUpperCase()));
                }catch(Exception e){
                    memberPacket.importErrorList.add(new ImportError("Error parsing sex", e.getMessage()));
                }


                try{
                    if(!nextLine[14].isEmpty()){
                        memberPacket.member.setAgeMet(Integer.parseInt(nextLine[14]));
                    }
                }catch(Exception e){
                    memberPacket.importErrorList.add(new ImportError("Error parsing age met", e.getMessage()));
                }

                // todo: place met

                try{
                    if(!nextLine[16].isEmpty()){
                        LocalDate birthday = LocalDate.parse(nextLine[16], DATE_FORMAT);
                        memberPacket.member.setBirthday(birthday);
                    }
                }catch (Exception e){
                    memberPacket.importErrorList.add(new ImportError("Error importing birthday", e.getMessage()));
                }

                //todo: add notes
                List<Evaluation> timeline = new ArrayList<>();
                for(int index = 18; !tableHeader[index].equals("News & Updates"); index++){
                    if(!nextLine[index].isEmpty()){
                        LocalDate date = LocalDate.parse(tableHeader[index], DATE_FORMAT);
                        LocalDateTime timestamp = date.atTime(LocalTime.MIDNIGHT);
                        timeline.add(new Evaluation(null, timestamp, "test", Integer.parseInt(nextLine[index]), memberPacket.member));
                    }
                }
                memberPacket.member.setTimeline(timeline);

                members.add(memberPacket);
            }

            return members;
        }catch (Exception e){
            throw e;
        }
    }
}
