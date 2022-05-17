package com.polyglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="language")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Language {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String API_ID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        return API_ID != null ? API_ID.equals(language.API_ID) : language.API_ID == null;
    }

    @Override
    public int hashCode() {
        return API_ID != null ? API_ID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Language{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", API_ID='" + API_ID + '\'' +
                '}';
    }
}
