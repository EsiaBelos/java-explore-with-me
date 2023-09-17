package ru.practicum.explore.admin.categories.model;

import lombok.*;
import ru.practicum.explore.admin.users.model.User;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        return id != null && id.equals(((Category) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
