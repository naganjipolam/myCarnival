package com.techplicit.mycarnival.data.model;

import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class CarnivalsPojo {

    private String id, name, image, startDate, endDate;
    private boolean activeFlag;

    public CarnivalsPojo(JSONObject jsonObject) {
        this.id = jsonObject.optString(JsonMap.ID);
        this.name = jsonObject.optString(JsonMap.NAME);
        this.image = jsonObject.optString(JsonMap.IMAGE);
        this.startDate = jsonObject.optString(JsonMap.START_DATE);
        this.endDate = jsonObject.optString(JsonMap.END_DATE);
        this.activeFlag = jsonObject.optBoolean(JsonMap.ACTIVE_FLAG);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    private interface JsonMap{
        String ID = "id";
        String NAME = "name";
        String IMAGE = "image";
        String START_DATE = "startDate";
        String END_DATE = "endDate";
        String ACTIVE_FLAG = "activeFlag";
    }
}
