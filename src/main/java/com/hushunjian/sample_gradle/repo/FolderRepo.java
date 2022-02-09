package com.hushunjian.sample_gradle.repo;

import com.hushunjian.sample_gradle.entity.FolderEntity;

import java.util.Collection;
import java.util.List;

public interface FolderRepo extends TreeRepo<FolderEntity> {

    List<FolderEntity> findByOutLineStartsWithOrderByOutLine(String outLine);


    List<FolderEntity> findByOutLineInAndOutLineStartsWithOrderByOutLine(Collection<String> parentOutLines, String outLine);


    List<FolderEntity> findByParentIdOrderByOutLine(String parentId);


    List<FolderEntity> findByParentIdAndIdNotOrderByOutLine(String parentId, String id);


    List<FolderEntity> findByParentIdAndIdNotAndOutLineGreaterThanOrderByOutLine(String parentId, String id, String outLine);


    FolderEntity findFirstByParentIdOrderByOutLineDesc(String parentId);


    List<FolderEntity> findByParentIdInOrderByOutLine(Collection<String> parentIds);
}
