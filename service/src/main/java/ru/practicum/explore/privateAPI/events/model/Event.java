package ru.practicum.explore.privateAPI.events.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String annotation; //краткое опис
    private String description; //полное опис

    @Column(name = "created")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Column(name = "published")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean paid; //Нужно ли оплачивать участие

    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    private State state;

    private Integer participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Long views;
    private Integer confirmedRequests;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        return id != null && id.equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
