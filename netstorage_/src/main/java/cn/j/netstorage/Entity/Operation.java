package cn.j.netstorage.Entity;

public enum Operation {
    Rename("Rename"), Delete("delete"), Upload("upload");
    private String operation;

    Operation(String operation) {
        this.operation = operation;
    }

    public String getOperation(){
        return this.operation;
    }

}
