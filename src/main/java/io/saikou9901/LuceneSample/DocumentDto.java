package io.saikou9901.LuceneSample;

public class DocumentDto {
    private int documentId;

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

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

    public DocumentDto(int documentId, String name, String value) {
        this.documentId = documentId;
        this.name = name;
        this.value = value;
    }
}
