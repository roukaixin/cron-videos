package com.roukaixin.cronvideos.listener.event;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Aria2TaskEvent {

    private Long id;

    private String gid;

    public String toString() {
        return "Aria2Task [id=" + id + ", gid=" + gid + "]";
    }
}
