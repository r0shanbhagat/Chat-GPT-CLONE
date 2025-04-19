package com.codentmind.gemlens.core

/**
 * @Details EntityMapper: It helps to create the Model from Existing response source.
 * This way we can prevent any NPE due to service response changes.
 * @Author Roshan Bhagat
 */
interface EntityMapper<Entity, DomainModel> {
    /**
     * Map from entity
     *
     * @param entity
     * @return
     */
    fun mapFromEntity(entity: Entity): DomainModel
}