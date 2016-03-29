
package com.adi.ho.jackie.bubblestocks.yahoorssfeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Body {

    @SerializedName("rss")
    @Expose
    private Rss rss;

    /**
     * 
     * @return
     *     The rss
     */
    public Rss getRss() {
        return rss;
    }

    /**
     * 
     * @param rss
     *     The rss
     */
    public void setRss(Rss rss) {
        this.rss = rss;
    }

}
