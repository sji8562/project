package com.tenco.projectinit.service;

import com.tenco.projectinit.dto.responsedto.CategoryResponseDTO;
import com.tenco.projectinit.repository.entity.FirstCategory;
import com.tenco.projectinit.repository.inteface.FirstCategoryJPARepository;
import com.tenco.projectinit.repository.inteface.OptionJPARepository;
import com.tenco.projectinit.repository.inteface.SecondCategoryJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private FirstCategoryJPARepository firstCategoryJPARepository;

    @Autowired
    private SecondCategoryJPARepository secondCategoryJPARepository;

    @Autowired
    private OptionJPARepository optionJPARepository;

    public CategoryResponseDTO.FirstCategoryListDTO getFirstCategoryList() {
        List<FirstCategory> firstCategories = firstCategoryJPARepository.findAll();
        List<CategoryResponseDTO.FirstCategoryDTO> firstCategoryDTOS = new ArrayList<>();
        for (FirstCategory firstCategory : firstCategories) {
            CategoryResponseDTO.FirstCategoryDTO firstCategoryDTO = new CategoryResponseDTO.FirstCategoryDTO(firstCategory.getId(), firstCategory.getName());
            firstCategoryDTOS.add(firstCategoryDTO);
        }
        return new CategoryResponseDTO.FirstCategoryListDTO(firstCategoryDTOS);
    }
}
