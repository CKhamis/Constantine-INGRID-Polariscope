package com.constantine.polariscope.DataImport;

import com.constantine.polariscope.Model.Member;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
public class IngridMapper {
    private final int TABLE_HEADER_ROW = 4;
    public List<Member> extractMembers(MultipartFile file) throws Exception{
        List<Member> members = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream()); CSVReader csvReader = new CSVReader(reader)) {

            // Skip decoration rows
            for (int i = 0; i < TABLE_HEADER_ROW; i++) {
                csvReader.readNext();
            }

            String[] tableHeader = csvReader.readNext();

            return null;
        }catch (Exception e){
            throw e;
        }
    }
}
