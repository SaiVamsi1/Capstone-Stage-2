package com.vamsi.popularmoviesstage1final;

import java.io.Serializable;

class YoutubeData implements Serializable {
    private String name;
    private String key;

    public YoutubeData(String name, String key) {
        this.name=name;
        this.key=key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
