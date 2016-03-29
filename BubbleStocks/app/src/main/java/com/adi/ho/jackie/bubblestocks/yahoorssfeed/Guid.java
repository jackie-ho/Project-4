
package com.adi.ho.jackie.bubblestocks.yahoorssfeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Guid {

    @SerializedName("ispermalink")
    @Expose
    private String ispermalink;
    @SerializedName("content")
    @Expose
    private String content;

    /**
     * 
     * @return
     *     The ispermalink
     */
    public String getIspermalink() {
        return ispermalink;
    }

    /**
     * 
     * @param ispermalink
     *     The ispermalink
     */
    public void setIspermalink(String ispermalink) {
        this.ispermalink = ispermalink;
    }

    /**
     * 
     * @return
     *     The content
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param content
     *     The content
     */
    public void setContent(String content) {
        this.content = content;
    }

}
