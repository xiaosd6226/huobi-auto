package io.yule.huobiauto.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by chensijiang on 2018/4/15 下午8:43.
 */
@Entity
public class EnumerationType implements Serializable {
    private static final long serialVersionUID = 8346219116077578855L;

    @Id
    private String id;

    private String enumTypeName;

    private String enumTypeDescription;

    private Timestamp createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnumTypeName() {
        return enumTypeName;
    }

    public void setEnumTypeName(String enumTypeName) {
        this.enumTypeName = enumTypeName;
    }

    public String getEnumTypeDescription() {
        return enumTypeDescription;
    }

    public void setEnumTypeDescription(String enumTypeDescription) {
        this.enumTypeDescription = enumTypeDescription;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}
