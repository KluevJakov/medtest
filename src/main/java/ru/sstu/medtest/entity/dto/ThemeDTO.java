package ru.sstu.medtest.entity.dto;

public class ThemeDTO {
    private Long id;
    private String title;
    private Integer estimatedTime;
    private String text;
    private Boolean learned;

    public ThemeDTO(Long id, String title, Integer estimatedTime, String text, Boolean learned) {
        this.id = id;
        this.title = title;
        this.estimatedTime = estimatedTime;
        this.text = text;
        this.learned = learned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getLearned() {
        return learned;
    }

    public void setLearned(Boolean learned) {
        this.learned = learned;
    }
}
