package ru.practicum.explore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ViewStats {
    private String app; //Идентификатор сервиса
    private String uri;
    private Long hits; //Количество просмотров

    public ViewStats(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
