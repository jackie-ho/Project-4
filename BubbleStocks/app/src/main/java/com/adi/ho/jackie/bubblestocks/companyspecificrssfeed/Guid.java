
package com.adi.ho.jackie.bubblestocks.companyspecificrssfeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Guid {

    @SerializedName("isPermaLink")
    @Expose
    private String isPermaLink;
    @SerializedName("content")
    @Expose
    private String content;

    /**
     * 
     * @return
     *     The isPermaLink
     */
    public String getIsPermaLink() {
        return isPermaLink;
    }

    /**
     * 
     * @param isPermaLink
     *     The isPermaLink
     */
    public void setIsPermaLink(String isPermaLink) {
        this.isPermaLink = isPermaLink;
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
