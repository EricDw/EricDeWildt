package com.publicmethod.ericdewildt.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.publicmethod.ericdewildt.data.*
import java.util.*

@Entity(tableName = ericTableName)
data class CachedEric(
    @PrimaryKey(autoGenerate = false)
    var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.firstName)
    var firstName: FirstName = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.lastName)
    var lastName: LastName = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.fullName)
    var fullName: FullName = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.emailAddress)
    var emailAddress: EmailAddress = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.linkedInURL)
    var linkedInURL: LinkedInURL = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.websiteURL)
    var websiteURL: WebsiteURL = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.gitHubURL)
    var gitHubURL: GitHubURL = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.twitter)
    var twitter: TwitterHandle = "",
    var description: Description = "",
    @ColumnInfo(name = com.publicmethod.ericdewildt.cache.yearsOfExperience)
    var yearsOfExperience: YearsOfExperience = 0,
    @Ignore
    val skills: Skills = listOf()
)

