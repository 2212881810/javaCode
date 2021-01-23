package com.qinfeng.zheng;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.qinfeng.zheng.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 10:57
 * @dec 消息识别器
 */
public final class GameMsgRecognizer {

    private static final Logger logger = LoggerFactory.getLogger(GameMsgRecognizer.class);

    private GameMsgRecognizer() {
    }

    /**
     * 消息编号-> 消息对象字典
     */
    private static final Map<Integer, GeneratedMessageV3> msgCodeAndMsgObjectMap = new HashMap<>();
    /**
     * 消息类-> 消息编号字典
     */
    private static final Map<Class<?>, Integer> msgClassAndMsgCodeMap = new HashMap<>();


    /**
     * 初始化
     */
    public static void init() {
        logger.info("==========完成消息号与消息类之间的映射============");
       /* msgCodeAndMsgObjectMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        msgCodeAndMsgObjectMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        msgCodeAndMsgObjectMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());


        classAndMsgCode.put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        classAndMsgCode.put(GameMsgProtocol.WhoElseIsHereResult.class, GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        classAndMsgCode.put(GameMsgProtocol.UserMoveToResult.class, GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
*/
        Class<?>[] innerClassArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClass : innerClassArray) {

            if (null == innerClass || !GeneratedMessageV3.class.isAssignableFrom(innerClass)) {
                // 不是消息类，直接跳过
                continue;
            }

            String className = innerClass.getSimpleName();
            className = className.toLowerCase();


            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {

                if (msgCode == null) {
                    continue;
                }


                // 消息编码
                String strMsgCode = msgCode.name();

                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(className)) {
                    continue;
                }


                try {
                    // GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance()
                    Object returnObj = innerClass.getDeclaredMethod("getDefaultInstance").invoke(innerClass);
                    logger.info("{} <===> {}", msgCode.getNumber(), innerClass);
                    msgCodeAndMsgObjectMap.put(msgCode.getNumber(), (GeneratedMessageV3) returnObj);
                    msgClassAndMsgCodeMap.put(innerClass, msgCode.getNumber());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

            }
        }


    }

    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 messageV3 = msgCodeAndMsgObjectMap.get(msgCode);
        if (messageV3 == null) {
            return null;
        }
        return messageV3.newBuilderForType();

    }

    public static int getMsgCodeByMsgClass(Class<?> msgClass) {
        if (msgClass == null) {
            return -1;
        }
        Integer msgCode = msgClassAndMsgCodeMap.get(msgClass);

        if (null == msgClass) {
            return -1;

        }

        return msgCode.intValue();
    }
}
