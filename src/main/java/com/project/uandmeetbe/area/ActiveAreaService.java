package com.project.uandmeetbe.area;

import com.project.uandmeetbe.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActiveAreaService {
    private final ActiveAreaRepository activeAreaRepository;

    @Transactional
    public void modifyActiveArea(List<ActiveArea> activeAreaList, User user){

        for (ActiveArea activeArea : user.getActiveAreas()){
            activeAreaRepository.delete(activeArea);
        }
        user.deleteActiveArea();

        for (ActiveArea activeArea : activeAreaList){
            activeArea.updateUser(user);
            activeAreaRepository.save(activeArea);
            user.updateActiveArea(activeArea);

        }
    }
}
