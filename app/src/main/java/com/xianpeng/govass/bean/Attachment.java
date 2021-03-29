/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: Attachment
 * Author: zhang
 * Date: 2021/3/24 20:36
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.xianpeng.govass.bean;

import java.io.Serializable;

/**
 * @ClassName: Attachment
 * @Description: java类作用描述
 * @Author: zhang
 * @Date: 2021/3/24 20:36
 */
public class Attachment implements Serializable {
    private String fileName;
    private String filePath;
    private String name;
    private String url;
    private int id;
    private int policyId;
    private int projectId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
