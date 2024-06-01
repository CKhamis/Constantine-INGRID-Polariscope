package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Place;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public List<Place> findAll(User author){
        return placeRepository.findAllByAuthorOrderByLastModifiedDesc(author);
    }
}
