package com.publicmethod.ericdewildt.remote

import com.publicmethod.ericdewildt.data.*

data class EricDTO(
    val firstName: FirstName,
    val lastName: LastName,
    val fullName: FullName,
    val emailAddress: EmailAddress,
    val linkedInURL: LinkedInURL,
    val websiteURL: WebsiteURL,
    val twitter: TwitterHandle,
    val gitHubURL: GitHubURL,
    val description: Description,
    val yearsOfExperience: YearsOfExperience,
    val skills: Skills
)
