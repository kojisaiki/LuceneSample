package io.saikou9901.LuceneSample;

public class DocumentDto {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DocumentDto(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
