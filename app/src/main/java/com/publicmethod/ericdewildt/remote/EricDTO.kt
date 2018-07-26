package com.publicmethod.ericdewildt.remote

import com.publicmethod.ericdewildt.data.*

data class EricDTO(
        val firstName: FirstName,
        val lastName: LastName,
        val fullName: FullName,
        val description: Description,
        val yearsOfExperience: YearsOfExperience,
        val skills: Skills)
