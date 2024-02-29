package com.lutech.notepad.model;

public class CategoriesModel {
    private int idCategory ;
    private String name ;
    private int idNotes;

    public CategoriesModel(String name) {
        this.name = name;
    }

    public CategoriesModel(int idCategory, String name) {
        this.idCategory = idCategory;
        this.name = name;
    }

    public CategoriesModel(int idCategory, String name, int idNotes) {
        this.idCategory = idCategory;
        this.name = name;
        this.idNotes = idNotes;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdNotes() {
        return idNotes;
    }

    public void setIdNotes(int idNotes) {
        this.idNotes = idNotes;
    }
}
