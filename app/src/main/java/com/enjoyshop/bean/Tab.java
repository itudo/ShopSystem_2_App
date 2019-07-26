package com.enjoyshop.bean;

/**
 * <pre>
 *     desc   :底部导航栏 图片 文字  framgment 的封装
 *     version: 1.0
 * </pre>
 */


public class Tab {


    private int    title;
    private int    icon;
    private int orderStatus;
    private Class  fragment;


    public Tab(Class fragment, int title, int icon) {
        this.title = title;
        this.icon = icon;
        this.fragment = fragment;
    }
    public Tab(Class fragment, int title, int icon,int order) {
        this.title = title;
        this.icon = icon;
        this.fragment = fragment;
        this.orderStatus = order;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Class getFragment() {
        return fragment;
    }

    public void setFragment(Class fragment) {
        this.fragment = fragment;
    }

    public int getOrder() {
        return orderStatus;
    }

    public void setOrder(int order) {
        this.orderStatus = order;
    }
}
