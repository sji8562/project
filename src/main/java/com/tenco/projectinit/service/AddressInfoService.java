package com.tenco.projectinit.service;

import com.tenco.projectinit.dto.AddressInfoResponseDTO;
import com.tenco.projectinit.repository.entity.AddressInfo;
import com.tenco.projectinit.repository.entity.User;
import com.tenco.projectinit.repository.inteface.AddressInfoJPARepository;
import com.tenco.projectinit.repository.inteface.UserJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressInfoService {

    @Autowired
    private AddressInfoJPARepository addressInfoJPARepository;
    @Autowired
    private UserJPARepository userJPARepository;

    // 주소 목록을 보여주는 메서드
    public List<AddressInfo> get(int userId) {
        Optional<User> userOptional = userJPARepository.findById(userId);
        return addressInfoJPARepository.findByUserId(userId);
    }

    // 주소를 추가하는 메서드
    @Transactional
    public AddressInfo add(AddressInfoResponseDTO dto, int userId) {
        // 이미 존재하는 유저 가져오기
        Optional<User> userOptional = userJPARepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            AddressInfo addressInfo = dto.convertToAddressInfo(user);
            setMainAddress(user.getId(), addressInfo);
            // 주소 정보 저장
            return addressInfoJPARepository.save(addressInfo);
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }

    // 대표 주소 설정 메서드
    @Transactional
    public void setMainAddress(int userId, AddressInfo addressInfo) {
        List<AddressInfo> addressList = addressInfoJPARepository.findByUserId(userId);

        if (!addressList.isEmpty()) {
            // 대표 주소 초기화
            for (AddressInfo address : addressList) {
                address.setChoice(false);
            }
        }
        addressInfo.setChoice(true);
        addressList.add(addressInfo);

        // 대표 주소 정보 저장
        addressInfoJPARepository.saveAll(addressList);
    }
}