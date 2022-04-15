package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoriesDto {

    private Long id;
    private String categoryName;
    private Long displayOrder;
}
