package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleDto implements Serializable {

    private static final long serialVersionUID = 5187893160878198443L;
    private long id;

    private String name;

    private String description;

}
