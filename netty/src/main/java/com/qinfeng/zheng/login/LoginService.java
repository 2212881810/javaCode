package com.qinfeng.zheng.login;

import com.qinfeng.zheng.async.AsyncOperationProcessor;
import com.qinfeng.zheng.async.IAsyncOperation;
import com.qinfeng.zheng.login.db.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 18:49
 * @dec 登录服务
 */
public class LoginService {
    /**
     * 日志服务
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private static final Map<String, UserEntity> userMap = new ConcurrentHashMap<>();

    static {
        userMap.put("admin", new UserEntity(1, "admin", "123", "Hero_Shaman"));
        userMap.put("root", new UserEntity(2, "root", "123", "Hero_Shaman"));
    }

    private static final AtomicInteger ids = new AtomicInteger(2);
    /**
     * 单例对象
     */
    static private final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public LoginService getInstance() {
        return _instance;
    }

    // 回调函数版本
/*
    public void userLogin(String userName, String password, Function<UserEntity, Void> callBack) {
        if (null == userName ||
                null == password) {
            return;
        }

        AsyncOperationProcessor.getInstance().process(() -> {
            UserEntity userEntity = userMap.get(userName);

            if (userEntity == null) {
                userEntity = new UserEntity();
                userEntity.userId = ids.getAndIncrement();
                userEntity.userName = userName;
                userEntity.password = password;
                userEntity.heroAvatar = "Hero_Shaman";
                userMap.put(userName, userEntity);

            }
            LOGGER.info("模拟数据库操作线程：{}", Thread.currentThread().getName());

            if (callBack != null) {
                callBack.apply(userEntity);
            }
        });
    }
*/

    /**
     * 用户登陆： 与AttkCmd是同1个线程操作操作，如果是操作数据库，涉及到网络io， 极易造成阻塞
     *
     * @param userName
     * @param password
     * @return
     */
    /*public UserEntity userLogin(String userName, String password) {
        if (null == userName ||
                null == password) {
            return null;
        }


        UserEntity userEntity = userMap.get(userName);

        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.userId = ids.getAndIncrement();
            userEntity.userName = userName;
            userEntity.password = password;
            userEntity.heroAvatar = "Hero_Shaman";
            userMap.put(userName, userEntity);

        }
        LOGGER.info("模拟数据库操作线程：{}",Thread.currentThread().getName());
        return userEntity;

//        try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
//            // 获取 DAO
//            IUserDao dao = mySqlSession.getMapper(IUserDao.class);
//            // 获取用户实体
//            UserEntity userEntity = dao.getByUserName(userName);
//
//            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());
//
//            if (null != userEntity) {
//                if (!password.equals(userEntity.password)) {
//                    throw new RuntimeException("密码错误");
//                }
//            } else {
//                userEntity = new UserEntity();
//                userEntity.userName = userName;
//                userEntity.password = password;
//                userEntity.heroAvatar = "Hero_Shaman";
//
//                dao.insertInto(userEntity);
//            }
//
//            return userEntity;
//        } catch (Exception ex) {
//            // 记录错误日志
//            LOGGER.error(ex.getMessage(), ex);
//            return null;
//        }
    }*/
    public void userLogin(String userName, String password, Function<UserEntity, Void> callBack) {
        if (null == userName || null == password) {
            return;
        }


        AsyncGetUserEntity asyncOp = new AsyncGetUserEntity(userName, password) {
            @Override
            public int getBindId() {
                // 通过userName的最后1个字母来分配线程池
                return userName.charAt(userName.length() - 1);
            }

            @Override
            public void doFinish() {
                if (callBack != null) {
                    callBack.apply(this.getUserEntity());
                }
            }
        };

        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    private class AsyncGetUserEntity implements IAsyncOperation {

        private String userName;
        private String password;

        private UserEntity _userEntity;

        public AsyncGetUserEntity(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }


        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public void doAsync() {

            UserEntity userEntity = userMap.get(userName);


            if (userEntity == null) {
                userEntity = new UserEntity();
                userEntity.userId = ids.getAndIncrement();
                userEntity.userName = userName;
                userEntity.password = password;
                userEntity.heroAvatar = "Hero_Shaman";
                userMap.put(userName, userEntity);
            }


            _userEntity = userEntity;
            LOGGER.info("模拟数据库操作线程：{}", Thread.currentThread().getName());
        }
    }
}
