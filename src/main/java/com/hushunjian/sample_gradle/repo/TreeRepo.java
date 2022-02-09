package com.hushunjian.sample_gradle.repo;

import com.hushunjian.sample_gradle.entity.TreeEntity;
import com.hushunjian.sample_gradle.util.OutLineUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TreeRepo<T extends TreeEntity> extends JpaRepository<T, String> {

    List<T> findByOutLineStartsWithOrderByOutLine(String outLine);

    List<T> findByOutLineInAndOutLineStartsWithOrderByOutLine(Collection<String> parentOutLines, String outLine);

    List<T> findByParentIdOrderByOutLine(String parentId);

    List<T> findByParentIdAndIdNotOrderByOutLine(String parentId, String id);

    List<T> findByParentIdAndIdNotAndOutLineGreaterThanOrderByOutLine(String parentId, String id, String outLine);

    T findFirstByParentIdOrderByOutLineDesc(String parentId);

    List<T> findByParentIdInOrderByOutLine(Collection<String> parentIds);

    default Sort outLineAsc() {
        return Sort.by(Sort.Order.asc("out_line"));
    }

    default List<T> findChildren(T current) {
        return findByOutLineStartsWithOrderByOutLine(current.getOutLine());
    }

    default List<T> findAll() {
        return findAll(outLineAsc());
    }

    default List<T> findBranch(T node) {
        return findByOutLineInAndOutLineStartsWithOrderByOutLine(OutLineUtil.getAllParentPath(node.getOutLine()), node.getOutLine());
    }

    default List<T> findByParentId(String parentId) {
        return findByParentIdOrderByOutLine(parentId);
    }

    default List<T> findNeedMoveNodes(String parentId, String id, String outLine) {
        if (StringUtils.isBlank(outLine)) {
            return findByParentIdAndIdNotOrderByOutLine(parentId, id);
        }
        return findByParentIdAndIdNotAndOutLineGreaterThanOrderByOutLine(parentId, id, outLine);
    }

    default T findMaxChild(String parentId) {
        return findFirstByParentIdOrderByOutLineDesc(parentId);
    }

    default List<T> findByParentIdIn(Collection<String> parentIds) {
        return findByParentIdInOrderByOutLine(parentIds);
    }
}
