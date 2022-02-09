package com.hushunjian.sample_gradle.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hushunjian.sample_gradle.entity.TreeEntity;
import com.hushunjian.sample_gradle.repo.TreeRepo;
import com.hushunjian.sample_gradle.util.OutLineUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseTreeService<T extends TreeEntity, BaseTreeRepo extends TreeRepo<T>> {

    @Autowired
    private BaseTreeRepo baseTreeRepo;

    public T findById(String id) {
        return baseTreeRepo.getOne(id);
    }

    public List<T> findChildren(T current) {
        return baseTreeRepo.findChildren(current);
    }
    
    public List<T> findAll() {
        return baseTreeRepo.findAll();
    }

    public List<T> findBranch(T node) {
        return baseTreeRepo.findBranch(node);
    }

    public List<T> findByParentId(String parentId) {
        return baseTreeRepo.findByParentId(parentId);
    }

    public void autoStepMove(T source, T target) {
        sameLevelMove(source, target, -1, true);
    }

    public void move(T source, T target, int step) {
        sameLevelMove(source, target, step, false);
    }

    private void sameLevelMove(T source, T target, int step, boolean isAutoStep) {
        // 校验
        sameLevelCheck(source, target);
        // 父outLine
        String parentOutLine = OutLineUtil.getParentPath(source.getOutLine());
        // preOutLine
        String preOutLine = target == null ? OutLineUtil.getFirst(parentOutLine, source.digit()) : target.getOutLine();
        // 查询出可能需要移动的节点
        List<T> nodes = baseTreeRepo.findNeedMoveNodes(source.getParentId(), source.getId(), preOutLine);
        step = isAutoStep ? autoStep(source, nodes, preOutLine) : step;
        // 处理移动
        move(source, nodes, parentOutLine, preOutLine, step);
    }

    public void crossLevelMove(T source, T target, String targetParentId, int step) {
        crossLevelMove(source, target, targetParentId, step, false);
    }

    public void crossLevelMoveAutoStep(T source, T target, String targetParentId) {
        crossLevelMove(source, target, targetParentId, -1, true);
    }

    private void crossLevelMove(T source, T target, String targetParentId, int step, boolean isAutoStep) {
        // 校验
        crossLevelCheck(source, target, targetParentId);
        // 根据targetParentId查询出parent
        T parent = findById(targetParentId);
        // 父节点outLine
        String parentOutLine = parent == null ? "" : parent.getOutLine();
        // preOutLine
        String preOutLine = target == null ? OutLineUtil.getFirst(parentOutLine, source.digit()) : target.getOutLine();
        // 查询出可能需要移动的节点
        List<T> nodes = baseTreeRepo.findNeedMoveNodes(targetParentId, source.getId(), preOutLine);
        // 修改source的parentId
        source.setParentId(targetParentId);
        step = isAutoStep ? autoStep(source, nodes, preOutLine) : step;
        // 处理移动
        move(source, nodes, parentOutLine, preOutLine, step);
    }

    private void sameLevelCheck(T source, T target) {
        if (target != null && !StringUtils.equals(source.getParentId(), target.getParentId())) {
            throw new RuntimeException("仅限同层级移动!");
        }
    }

    private void crossLevelCheck(T source, T target, String targetParentId) {
        if (StringUtils.equals(source.getParentId(), targetParentId)) {
            throw new RuntimeException("仅限跨层级移动!");
        }
        if (target != null && !StringUtils.equals(targetParentId, target.getParentId())) {
            throw new RuntimeException();
        }
        // 跨层级移动可能有限制,
        if (canNotMove(source, target, targetParentId)) {
            throw new RuntimeException("条件不满足,无法跨层级移动!");
        }
    }

    private int autoStep(T source, List<T> nodes, String preOutLine) {
        // 默认原始步进
        int step = source.step();
        // 有需要移动的节点,重新计算步进
        if (!CollectionUtils.isEmpty(nodes)) {
            long interval = OutLineUtil.calInterval(preOutLine, nodes.get(0).getOutLine());
            if (interval == 1) {
                // 没有间隔了
                step = source.step() == 1 ? 1 : source.step() - 1;
            } else {
                // 取中间数
                step = Math.round(interval >> 1);
            }
        }
        return step;
    }

    private void updateBranchNodeOutLine(Map<String, T> needChangeOutLineNodeMap) {
        List<T> update = Lists.newArrayList();
        // 最后在循环需要处理的节点,将所有的子节点的outLine替换掉
        needChangeOutLineNodeMap.forEach((newOutLine, node) -> {
            // 查询出原来节点下所有的子节点
            findChildren(node).forEach(child -> {
                // replace outLine
                child.setOutLine(child.getOutLine().replaceFirst(node.getOutLine(), newOutLine));
                // 加入要更新的集合里面
                update.add(child);
            });
            // 将本身的替换掉
            node.setOutLine(newOutLine);
            update.add(node);
        });
        // 批量更新 跨层级移动可能会改parentId
        baseTreeRepo.saveAll(update);
    }

    private Map<String, T> checkChange(List<T> nodes, String outLine, T source, String parentOutLine, int step) {
        // 需要变化outLine的节点map
        Map<String, T> needChangeOutLineNodeMap = Maps.newHashMap();
        // source节点是一定回重新生成的
        needChangeOutLineNodeMap.put(outLine, source);
        // 循环nodes,判断节点是否需要重新生成outLine
        for (T node : nodes) {
            if (node.getOutLine().compareTo(outLine) <= 0) {
                // 循环的节点<=outLine,说明当前节点的outLine需要重新生成
                outLine = OutLineUtil.next(parentOutLine, outLine, step, source.digit());
                needChangeOutLineNodeMap.put(outLine, node);
            } else {
                // nodes从小到大排序的,一次不匹配就可以结束循环了
                break;
            }
        }
        return needChangeOutLineNodeMap;
    }

    private void move(T source, List<T> nodes, String parentOutLine, String preOutLine, int step) {
        // 生成source的新outLine
        String outLine = OutLineUtil.next(parentOutLine, preOutLine, step, source.digit());
        // 检查需要移动
        Map<String, T> needChangeOutLineNodeMap = checkChange(nodes, outLine, source, parentOutLine, step);
        // 处理更新逻辑
        updateBranchNodeOutLine(needChangeOutLineNodeMap);
    }

    public void fillTreeBaseInfo(T node, T parent) {
        String parentId = "";
        String parentOutLine = "";
        if (parent != null) {
            parentId = parent.getId();
            parentOutLine = parent.getOutLine();
        }
        // 查询当前父级下最大的一个outLine
        T maxChild = findMaxChild(parentId);
        // 设置属性
        node.setParentId(parentId);
        node.setOutLine(OutLineUtil.next(parentOutLine, maxChild == null ? "" : maxChild.getOutLine(), node.step(), node.digit()));
    }

    public T findMaxChild(String parentId) {
        return baseTreeRepo.findMaxChild(parentId);
    }

    public void save(T node, T parent) {
        // 填充parentId和outLine
        fillTreeBaseInfo(node, parent);
        // 保存
        baseTreeRepo.save(node);
    }

    public void maintainOutLine(List<T> nodes) {
        // 按照parentId分组
        Map<String, List<T>> parentChildrenMap = nodes.stream().collect(Collectors.groupingBy(T::getParentId));
        // parentId是空的作为root
        List<T> roots = parentChildrenMap.get("");
        // 从root开始递归
        String preOutLine = "";
        for (T root : roots) {
            preOutLine = OutLineUtil.next("", preOutLine, root.step(), root.digit());
            root.setOutLine(preOutLine);
            maintainChildrenOutLine(root, parentChildrenMap);
        }
        // 最后批量更新
        baseTreeRepo.saveAll(nodes);
    }

    private void maintainChildrenOutLine(T parent, Map<String, List<T>> parentChildrenMap) {
        List<T> children = parentChildrenMap.get(parent.getId());
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        //
        String preOutLine = "";
        for (T child : children) {
            preOutLine = OutLineUtil.next(parent.getOutLine(), preOutLine, child.step(), child.digit());
            child.setOutLine(preOutLine);
            maintainChildrenOutLine(child, parentChildrenMap);
        }
    }

    public void fillHasChild(List<T> nodes) {
        // 过滤有子级的id
        List<String> existChildIds = filterExistChildIds(nodes.stream().map(T::getId).collect(Collectors.toList()));
        // 循环填充
        nodes.forEach(node -> node.setHasChild(existChildIds.contains(node.getId())));
    }

    public List<T> findByParentIdIn(Collection<String> parentIds) {
        return baseTreeRepo.findByParentIdIn(parentIds);
    }

    public List<String> filterExistChildIds(Collection<String> parentIds) {
        // 查询所有的子级
        List<T> children = findByParentIdIn(parentIds);
        return children.stream().map(T::getParentId).distinct().collect(Collectors.toList());
    }

    public List<T> findByParentIdAndFillHasChild(String parentId) {
        List<T> children = findByParentId(parentId);
        fillHasChild(children);
        return children;
    }

    public boolean canNotMove(T source, T target, String targetParentId) {
        return true;
    }

}
