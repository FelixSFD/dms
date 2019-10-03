package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Person extends DataEntity implements Diffable<Person>, Historical<PersonHistory>, Tagged {

    @NotBlank
    @Size(max = 255)
    protected String firstName;

    @NotBlank
    @Size(max = 255)
    protected String lastName;

    protected LocalDate dateOfBirth;

    @ManyToMany
    private Set<Tag> tags;

    @OneToMany(mappedBy = "person")
    @OrderBy("timestamp")
    private List<PersonHistory> history;

    public Person() {

    }

    public Person(String firstName, String lastName, LocalDate dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public List<PersonHistory> getHistory() {
        return history;
    }

    public void setHistory(List<PersonHistory> history) {
        this.history = history;
    }

    @Override
    public Set<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
