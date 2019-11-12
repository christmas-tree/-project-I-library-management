/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package model;

public class Language {
    private String langId;
    private String language;

    public Language(String langId, String language) {
        this.langId = langId;
        this.language = language;
    }

    public String getLangId() {
        return langId;
    }

    public void setLangId(String langId) {
        this.langId = langId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return language;
    }
}
