package com.publicmethod.ericdewildt.data

typealias FirstName = String
typealias LastName = String
typealias FullName = String
typealias Description = String
typealias YearsOfExperience = Int
typealias Skill = String
typealias Skills = List<Skill>

data class Eric(
        val firstName: FirstName,
        val lastName: LastName,
        val fullName: FullName,
        val description: Description,
        val yearsOfExperience: YearsOfExperience,
        val skills: Skills)
