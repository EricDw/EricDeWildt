package com.publicmethod.ericdewildt.data

import com.publicmethod.ericdewildt.cache.CachedEric
import com.publicmethod.ericdewildt.remote.EricDTO

fun EricDTO.mapToEric(): Eric =
        Eric(this.firstName,
                this.lastName,
                this.fullName,
                this.description,
                this.yearsOfExperience,
                this.skills)

fun EricDTO.mapToCachedEric(): CachedEric =
        CachedEric(firstName = this.firstName,
                lastName = this.lastName,
                fullName = this.fullName,
                description = this.description,
                yearsOfExperience = this.yearsOfExperience,
                skills = this.skills)

fun CachedEric.mapToEric(): Eric =
        Eric(this.firstName,
                this.lastName,
                this.fullName,
                this.description,
                this.yearsOfExperience,
                this.skills)
