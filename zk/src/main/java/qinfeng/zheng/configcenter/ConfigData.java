package qinfeng.zheng.configcenter;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/9 22:44
 * @dec 配置数据, 用对象封装配置数据，以便于在多线程间进行数据传递
 */
public class ConfigData {
    /**
     * 真正的配置数据
     */
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
