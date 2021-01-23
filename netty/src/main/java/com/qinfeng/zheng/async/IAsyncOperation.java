package com.qinfeng.zheng.async;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 21:41
 * @dec 异步操作接口
 */
public interface IAsyncOperation {
    /**
     * 获取绑定id
     * @return 绑定id
     */
    default int getBindId() {
        return 0;
    }
    /**
     * 操作异步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑
     */
    default void doFinish() {

    }
}
