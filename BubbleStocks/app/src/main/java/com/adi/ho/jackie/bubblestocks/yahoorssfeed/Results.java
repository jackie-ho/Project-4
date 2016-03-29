
package com.adi.ho.jackie.bubblestocks.yahoorssfeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("body")
    @Expose
    private Body body;

    /**
     * 
     * @return
     *     The body
     */
    public Body getBody() {
        return body;
    }

    /**
     * 
     * @param body
     *     The body
     */
    public void setBody(Body body) {
        this.body = body;
    }

}
