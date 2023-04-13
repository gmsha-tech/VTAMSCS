package com.toralabs.apkextractor.helperclasses;

public class RecentAppsHelper {
    String fileName, ApkName, url, packageName, versionName, TimeStamp, pdfUrl, md5;

    public RecentAppsHelper(String fileName, String apkName, String url, String packageName, String versionName, String timeStamp, String pdfUrl, String md5) {
        this.fileName = fileName;
        ApkName = apkName;
        this.url = url;
        this.packageName = packageName;
        this.versionName = versionName;
        TimeStamp = timeStamp;
        this.pdfUrl = pdfUrl;
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getApkName() {
        return ApkName;
    }

    public void setApkName(String apkName) {
        ApkName = apkName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
