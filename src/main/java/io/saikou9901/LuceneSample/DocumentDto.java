package io.saikou9901.LuceneSample;

import java.util.UUID;

public class DocumentDto {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public DocumentDto(UUID id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public DocumentDto(UUID id, int documentId, String name, String value) {
        this.id = id;
        this.documentId = documentId;
        this.name = name;
        this.value = value;
    }
}
