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
public class FileDto implements Serializable {
    private static final long serialVersionUID = -1615675106168870645L;
    private Long id;
    private String name;
    private String type;
    private String userId;
    private byte[] fileByte;
    private Long eventId;
}
