package com.sina.sinavideo.sdk.dlna;

/**
 * 支持DLNA设备的实体类
 * 
 * @author sina
 *
 */
public class MRContent {

    private String uuid;
    private String name;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
