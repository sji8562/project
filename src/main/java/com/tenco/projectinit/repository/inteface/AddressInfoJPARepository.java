package com.tenco.projectinit.repository.inteface;

import com.tenco.projectinit.repository.entity.AddressInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AddressInfoJPARepository extends JpaRepository<AddressInfo, Integer> {
    List<AddressInfo> findByUserId(int userId);
}