package com.publicmethod.ericdewildt.cache

import arrow.core.Option

class EricCache(
    private val ericDao: EricDao,
    private val skillsDao: SkillsDao
) : Cache<CachedEric> {

    override fun getItem(): Option<CachedEric> =
        Option.fromNullable(ericDao.retrieveEric()
            ?.copy(skills = skillsDao.retrieveSkills()
                .map { it.name } ?: listOf()))

    override fun isCached(): Boolean =
        ericDao.retrieveEric() != null

    override fun isStale(): Boolean = true

    override fun saveItem(item: CachedEric) {
        skillsDao.saveSkills(item.skills.map { CachedSkill(it) })
        ericDao.saveEric(item)
    }
}
