package io.yule.huobiauto.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by chensijiang on 2018/4/15 下午8:45.
 */
@Entity
public class Enumeration implements Serializable {
    private static final long serialVersionUID = 572839730644264686L;

    @Id
    private String id;

    private String enumTypeId;

    private String enumName;

    private String enumDescription;

    private Timestamp createdDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnumTypeId() {
        return enumTypeId;
    }

    public void setEnumTypeId(String enumTypeId) {
        this.enumTypeId = enumTypeId;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public String getEnumDescription() {
        return enumDescription;
    }

    public void setEnumDescription(String enumDescription) {
        this.enumDescription = enumDescription;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}
