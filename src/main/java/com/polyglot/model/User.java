package com.polyglot.model;

import lombok.*;

import javax.persistence.*;

/**
 * Represents any user of the application.
 */
@Entity
@Table(name = "user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, unique = true, length = 30)
    private String userName;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String emailAddress;

    @OneToOne
    @JoinColumn(name = "native_language_id", nullable = false)
    private Language nativeLanguage;

    public User(String userName, String password, String emailAddress, Language nativeLanguage) {
        this.userName = userName;
        this.password = password;
        this.emailAddress = emailAddress;
        this.nativeLanguage = nativeLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (Id != null ? !Id.equals(user.Id) : user.Id != null) return false;
        if (userName != null ? !userName.equals(user.userName) : user.userName != null)
            return false;
        if (password != null ? !password.equals(user.password) : user.password != null)
            return false;
        return emailAddress != null ? emailAddress.equals(user.emailAddress) : user.emailAddress == null;
    }

    @Override
    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        return result;
    }
}
