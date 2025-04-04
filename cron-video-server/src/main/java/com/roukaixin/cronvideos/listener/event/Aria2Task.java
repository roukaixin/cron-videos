package com.roukaixin.cronvideos.listener.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aria2Task {

    private Long id;

    private String gid;

    public String toString() {
        return "Aria2Task [id=" + id + ", gid=" + gid + "]";
    }
}
