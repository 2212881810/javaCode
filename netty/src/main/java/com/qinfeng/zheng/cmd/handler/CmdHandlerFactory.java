package com.qinfeng.zheng.cmd.handler;

import com.google.protobuf.GeneratedMessageV3;
import com.qinfeng.zheng.handler.GameMsgEncoder;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import com.qinfeng.zheng.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 10:31
 * @dec 命令处理器工厂类
 */
public final class CmdHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(CmdHandlerFactory.class);
    /**
     * 命令处理器字典
     */
    private static Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> handlerMap = new HashMap<>();

    private CmdHandlerFactory() {
    }

    public static void init() {
        logger.info(" ==========完成命令与命令处理器之间的映射============");
    /*    handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
        handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());*/

        // 获取包名称
        String packageName = CmdHandlerFactory.class.getPackage().getName();
        // 获取ICmdHandler 接口的所有实现类
        Set<Class<?>> classes = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);
        for (Class<?> handlerClass : classes) {
            if (handlerClass == null || 0 != (handlerClass.getModifiers() & Modifier.ABSTRACT)) {
                continue;
            }


            Method[] methodArrays = handlerClass.getDeclaredMethods();
            Class<?> cmdClass = null;
            for (Method curMethod : methodArrays) {
                if (null == curMethod || !curMethod.getName().equals("handle")) {
                    continue;
                }

                // 函数参数类型数组
                Class<?>[] parameterTypes = curMethod.getParameterTypes();

                if (parameterTypes.length < 2 ||
                        parameterTypes[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])) {
                    continue;
                }

                cmdClass = parameterTypes[1];
                break;
            }

            if (cmdClass == null) {
                continue;
            }


            try {

                //创建命令处理器实例
                ICmdHandler<?> newHandler = (ICmdHandler<?>) handlerClass.newInstance();
                logger.info("{} <==> {}", cmdClass.getName(),newHandler.getClass());
                handlerMap.put(cmdClass, newHandler);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }


    }

    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClass) {
        if (msgClass == null) {
            return null;
        }


        return handlerMap.get(msgClass);

    }
}
