package org.ssiu.ucp.core.api;

/**
 * Manage table abstraction generated by different engines through UcpTable
 */
public abstract class UcpTable<T> {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    private T holdTable;

    public T getHoldTable() {
        return holdTable;
    }

    public void setHoldTable(T holdTable) {
        this.holdTable = holdTable;
    }

}
