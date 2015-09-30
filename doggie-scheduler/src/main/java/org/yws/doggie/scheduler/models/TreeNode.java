package org.yws.doggie.scheduler.models;

import java.io.Serializable;

/**
 * Created by wangshu.yang on 2015/7/28.
 */
public class TreeNode implements Serializable {
    private Long id;
    private String name;
    private boolean isParent;

    public TreeNode() {
    }

    public TreeNode(Long id, String name, boolean isParent) {
        this.id = id;
        this.name = name;
        this.isParent = isParent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }
}
