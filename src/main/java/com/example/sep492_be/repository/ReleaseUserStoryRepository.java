package com.example.sep492_be.repository;

import com.example.sep492_be.entity.ReleaseEntity;
import com.example.sep492_be.entity.ReleaseUserStoryEntity;
import com.example.sep492_be.entity.UserStoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReleaseUserStoryRepository extends JpaRepository<ReleaseUserStoryEntity, UUID> {
    @Query("""
            SELECT distinct x from ReleaseUserStoryEntity x 
             where x.release.id = :id""")
    List<ReleaseUserStoryEntity> findByReleaseId(UUID id);

    @Query("""
            SELECT distinct  x1 from ReleaseUserStoryEntity x
            inner join x.userStory x1 
             where x.release.id = :id""")
    List<UserStoryEntity> findUserStoryByReleaseId(UUID id);

    @Modifying
    @Query(" delete  from ReleaseUserStoryEntity x  where x.userStory.id = :userStoryId ")
    void deleteByUserStoryId(UUID userStoryId);
}
