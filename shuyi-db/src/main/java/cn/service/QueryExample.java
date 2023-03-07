package cn.service;

public class QueryExample<T> {

//    @ApiModelProperty(value = "将查询到的数据更新成实体非null属性")
private T record;
//    @ApiModelProperty(value = "example查询条件")
private Object example;

public T getRecord() {
        return record;
        }

public void setRecord(T record) {
        this.record = record;
        }

public Object getExample() {
        return example;
        }

public void setExample(Object example) {
        this.example = example;
        }

}
