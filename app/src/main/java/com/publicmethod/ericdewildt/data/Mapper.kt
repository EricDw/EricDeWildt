package com.publicmethod.ericdewildt.data

import com.publicmethod.ericdewildt.cache.CachedEric
import com.publicmethod.ericdewildt.remote.EricDTO

fun EricDTO.mapToEric(): Eric =
    Eric(
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
        emailAddress = this.emailAddress,
        linkedInURL = this.linkedInURL,
        websiteURL = this.websiteURL,
        twitter = this.twitter,
        gitHubURL = this.gitHubURL,
        description = this.description,
        yearsOfExperience = this.yearsOfExperience,
        skills = this.skills
    )

fun EricDTO.mapToCachedEric(): CachedEric =
    CachedEric(
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
        description = this.description,
        yearsOfExperience = this.yearsOfExperience,
        emailAddress = this.emailAddress,
        linkedInURL = this.linkedInURL,
        websiteURL = this.websiteURL,
        twitter = this.twitter,
        gitHubURL = this.gitHubURL,
        skills = this.skills
    )

fun CachedEric.mapToEric(): Eric =
    Eric(
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
        emailAddress = this.emailAddress,
        linkedInURL = this.linkedInURL,
        websiteURL = this.websiteURL,
        twitter = this.twitter,
        gitHubURL = this.gitHubURL,
        description = this.description,
        yearsOfExperience = this.yearsOfExperience,
        skills = this.skills
    )
