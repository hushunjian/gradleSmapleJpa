package com.hushunjian.sample_gradle.tree;

import com.hushunjian.sample_gradle.entity.TreeEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface TreeService<T extends TreeEntity> {

    /**
     * 根据id查找
     *
     * @param id 主键id
     * @return 实体对象
     */
    T findById(String id);

    /**
     * 查询当前节点的所有子节点
     *
     * @param current 当前节点
     * @return 所有子节点
     */
    List<T> findChildren(T current);

    /**
     * 查询所有子节点
     *
     * @param id 当前节点id
     * @return 所有子节点
     */
    default List<T> findChildren(String id) {
        return findChildren(findByIdAndCheckNull(id));
    }

    /**
     * 查询所有
     *
     * @return 所有树节点
     */
    List<T> findAll();

    /**
     * 查询节点所在分支
     *
     * @param node 节点
     * @return 父级+本身+所有子级
     */
    List<T> findBranch(T node);

    /**
     * 查询子节点
     *
     * @param parentId 父节点id
     * @return 子节点
     */
    List<T> findByParentId(String parentId);

    /**
     * 查询节点所在分支
     *
     * @param id 节点id
     * @return 父级+本身+所有子级
     */
    default List<T> findBranch(String id) {
        return findBranch(findByIdAndCheckNull(id));
    }

    /**
     * 根据id查询并判断null
     *
     * @param id 主键id
     * @return 实体
     */
    default T findByIdAndCheckNull(String id) {
        T t = findById(id);
        if (t == null) {
            throw new RuntimeException();
        }
        return t;
    }

    /**
     * 移动
     *
     * @param source 选中节点
     * @param target 目标节点
     * @param step 步进
     */
    void move(T source, T target, int step);

    /**
     * 拖动排序
     *
     * @param sourceId 选中树节点id
     * @param targetId 目标位置节点id
     */
    default void move(String sourceId, String targetId, int step) {
        Pair<T, T> pair = findSourceAndTarget(sourceId, targetId);
        move(pair.getKey(), pair.getValue(), step);
    }

    /**
     * 维护outLine
     *
     * @param nodes 需要维护的节点
     */
    void maintainOutLine(List<T> nodes);

    /**
     * 维护outLine
     */
    default void maintainOutLine() {
        maintainOutLine(findAll());
    }

    /**
     * 查询子节点,并填充hasChild标识
     *
     * @param parentId 父节点id
     * @return 子节点
     */
    List<T> findByParentIdAndFillHasChild(String parentId);

    /**
     * 跨层级移动
     *
     * @param source 选中节点
     * @param target 目标位置节点
     * @param targetParentId 目标位置节点parentId
     * @param step 步进
     */
    void crossLevelMove(T source, T target, String targetParentId, int step);

    /**
     * 跨层级移动
     *
     * @param sourceId 选中节点id
     * @param targetId 目标位置节点
     * @param targetParentId 目标位置节点parentId
     * @param step 步进
     */
    default void crossLevelMove(String sourceId, String targetId, String targetParentId, int step) {
        Pair<T, T> pair = findSourceAndTarget(sourceId, targetId);
        crossLevelMove(pair.getKey(), pair.getValue(), targetParentId, step);
    }

    default Pair<T, T> findSourceAndTarget(String sourceId, String targetId) {
        // 选中树节点
        T source = findByIdAndCheckNull(sourceId);
        // 目标位置(拖动到第一的位置上,targetId允许为空)
        T target = null;
        if (StringUtils.isNotBlank(targetId)) {
            target = findByIdAndCheckNull(targetId);
        }
        return Pair.of(source, target);
    }

    /**
     * 同层级自动算步进移动
     *
     * @param source 选中节点
     * @param target 目标位置节点
     */
    void autoStepMove(T source, T target);

    /**
     * 同层级自动算步进移动
     *
     * @param sourceId 选中节点id
     * @param targetId 目标位置节点id
     */
    default void autoStepMove(String sourceId, String targetId) {
        Pair<T, T> pair = findSourceAndTarget(sourceId, targetId);
        autoStepMove(pair.getKey(), pair.getValue());
    }

    /**
     * 跨层级自动算步进移动
     *
     * @param source 选中节点
     * @param target 目标位置节点
     * @param targetParentId 目标位置节点parentId
     */
    void crossLevelMoveAutoStep(T source, T target, String targetParentId);

    /**
     * 跨层级自动算步进移动
     *
     * @param sourceId 选中节点id
     * @param targetId 目标位置节点id
     * @param targetParentId 目标位置节点parentId
     */
    default void crossLevelMoveAutoStep(String sourceId, String targetId, String targetParentId) {
        Pair<T, T> pair = findSourceAndTarget(sourceId, targetId);
        crossLevelMoveAutoStep(pair.getKey(), pair.getValue(), targetParentId);
    }

    default void move(String sourceId, String targetId, String targetParentId, int step, boolean isAutoStep) {
        Pair<T, T> pair = findSourceAndTarget(sourceId, targetId);
        if (StringUtils.equals(pair.getKey().getParentId(), targetParentId)) {
            // 同层级
            if (isAutoStep) {
                autoStepMove(pair.getKey(), pair.getValue());
            } else {
                move(pair.getKey(), pair.getValue(), step);
            }
        } else {
            // 跨层级
            if (isAutoStep) {
                crossLevelMoveAutoStep(pair.getKey(), pair.getValue(), targetParentId);
            } else {
                crossLevelMove(pair.getKey(), pair.getValue(), targetParentId, step);
            }
        }
    }
}
