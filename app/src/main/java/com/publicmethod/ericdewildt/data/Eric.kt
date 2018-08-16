package com.publicmethod.ericdewildt.data

typealias FirstName = String
typealias LastName = String
typealias FullName = String
typealias Description = String
typealias YearsOfExperience = Int
typealias Skill = String
typealias Skills = List<Skill>

typealias EmailAddress = String

typealias LinkedInURL = String

typealias WebsiteURL = String

typealias TwitterHandle = String

typealias GitHubURL = String

data class Eric(
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
