package com.dude.dms.backend.data

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Changelog(
        var published: LocalDateTime,
        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 99999)
        var body: String,
        var version: String
) : DataEntity() {
        override fun toString() = "Changelog($version)"
}