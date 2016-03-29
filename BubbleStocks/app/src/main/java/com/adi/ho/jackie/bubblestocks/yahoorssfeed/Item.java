
package com.adi.ho.jackie.bubblestocks.yahoorssfeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("guid")
    @Expose
    private Guid guid;
    @SerializedName("pubdate")
    @Expose
    private String pubdate;
    @SerializedName("content")
    @Expose
    private String content;

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The guid
     */
    public Guid getGuid() {
        return guid;
    }

    /**
     * 
     * @param guid
     *     The guid
     */
    public void setGuid(Guid guid) {
        this.guid = guid;
    }

    /**
     * 
     * @return
     *     The pubdate
     */
    public String getPubdate() {
        return pubdate;
    }

    /**
     * 
     * @param pubdate
     *     The pubdate
     */
    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    /**
     * 
     * @return
     *     The content
     */
    public String getTitle() {

        String[] contentArray = content.split("\n");
        return contentArray[1];

    }

    public String getLink(){

        String[] linkArray = content.split("\n");
        return  linkArray[2];
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
