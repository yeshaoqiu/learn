package com.org.hm.ds.sort;

import java.util.ArrayList;
import java.util.List;

public class BinaryTreeSort extends AbstractSort {
    /** 根结点 */
    private BSTNode root;

    /**
     * 二叉排序树或者是一棵空树，或者是具有下列性质的二叉树：
     * （1）若左子树不空，则左子树上所有结点的值均小于它的根结点的值；
     * （2）若右子树不空，则右子树上所有结点的值均大于或等于它的根结点的值；
     * （3）左、右子树也分别为二叉排序树；
     *
     */
    BinaryTreeSort(){}

    public BSTNode getRoot() {
        return root;
    }

    public void insert(Integer value){
        if(value == null){ return; }

        BSTNode node = new BSTNode(value);
        insert(node);
    }

    public void insert(BSTNode node){
        if(node == null || node.data == null){ return; }

        if(root == null){
            root = node;
            return;
        }

        BSTNode bstNode = root;
        BSTNode n = null;
        while (bstNode != null){
            n = bstNode;
            if(bstNode.data > node.data){
                bstNode = bstNode.left;
            }else {
                bstNode = bstNode.right;
            }
        }

        node.parent = n;
        if(n.data > node.data){
            n.left = node;
        }else {
            n.right = node;
        }
    }

    public void preOrder(BSTNode root){
        if(root == null){ return; }

        System.out.print(root.data);
        System.out.print(" ");
        preOrder(root.left);
        preOrder(root.right);
    }

    public List<Integer> inOrder(BSTNode root){
        if(root == null){ return null; }

        List<Integer> result = new ArrayList<Integer>();
        List<Integer> leftList = inOrder(root.left);
        if(leftList != null){
            result.addAll(leftList);
        }
        result.add(root.data);
        List<Integer> rightList = inOrder(root.right);
        if(rightList != null){
            result.addAll(rightList);
        }
        return result;
    }

    public void postOrder(BSTNode root){
        if(root == null){ return; }

        postOrder(root.left);
        postOrder(root.right);
        System.out.print(root.data);
        System.out.print(" ");
    }

    public BSTNode search(Integer value){
        if(value == null){ return null; }
        if(root == null){ return null; }

        BSTNode node = root;
        while(node != null){
            if(node.data == value){
                break;
            }else if(node.data > value){
                node = node.left;
            }else {
                node = node.right;
            }
        }
        return node;
    }

    public BSTNode minimum(){
        BSTNode node = root;
        if(node == null){
            return null;
        }

        while(node.left != null){
            node = node.left;
        }
        return node;
    }

    public BSTNode maximum(){
        BSTNode node = root;
        if(node == null){
            return null;
        }

        while(node.right != null){
            node = node.right;
        }
        return node;
    }

    public BSTNode successor(BSTNode node){
        if(node == null){ return null; }

        // 右子树非空，找到最小节点
        if(node.right != null){
            BSTNode n = node.right;
            while (n.left != null){
                n = n.left;
            }
            return n;
        }

        BSTNode p = node.parent;
        while (p != null && node == p.right){
            node = p;
            p = node.parent;
        }
        return p;
    }

    public BSTNode predecessor(BSTNode node){
        if(node == null){ return null; }

        if(node.left != null){
            BSTNode n = node.left;
            while (n.right != null){
                n = n.right;
            }
            return n;
        }

        BSTNode p = node.parent;
        while (p != null && node == p.left){
            node = p;
            p = node.parent;
        }
        return p;
    }

    public BSTNode remove(Integer value){
        if(value == null){ return null; }

        BSTNode node = search(value);
        if(node == null){ return null; }

        //只有右子树
        if(node.left == null){
            if(node.parent == null){
                root = node.right;
            }else if(node.parent.left == node){
                node.parent.left = node.right;
            }else {
                node.parent.right = node.right;
            }
        }else {
            BSTNode cur = node;
            BSTNode delNode = node.left;
            while (delNode.right != null){
                cur = delNode;
                delNode = delNode.right;
            }
            //待删结点没有右子树，把左子树向上移动
            if(cur == node){
                cur.left = delNode.left;
            }else {
                cur.right = delNode.left;
            }
            node.setData(value);
        }

        return node;
    }

    @Override
    public List<Integer> sortList(List<Integer> list) {
        if(list == null || list.isEmpty()){ return list; }

        BinaryTreeSort bst = new BinaryTreeSort();
        for(Integer value : list){
            bst.insert(value);
        }
        return bst.inOrder(bst.getRoot());
    }

    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        BinaryTreeSort bst = new BinaryTreeSort();
        for(Integer value : a){
            bst.insert(value);
        }

        Integer[] r = new Integer[a.length];
        return bst.inOrder(bst.getRoot()).toArray(r);
    }

    protected String getName() {
        return "[binary tree sort]";
    }

    class BSTNode{
        private Integer data;//数据
        private BSTNode left;//左孩子
        private BSTNode right;//右孩子
        private BSTNode parent;//双亲

        public BSTNode(Integer data){
            this.data = data;
        }

        public void setData(Integer data){
            this.data = data;
        }
    }
}
