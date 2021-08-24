package org.example.util.remote;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import org.example.exception.ParameterException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @descriptions: 操作远程主机
 * @author: zhangfaquan
 * @date: 2021/8/23 15:51
 * @version: 1.0
 */
public class SSHUtils {

    private SSHUtils() {}

    private static final Logger logger = LoggerFactory.getLogger(SSHUtils.class);

    public static Connection getSSHConnection(String host, String userName, String password) {
        return getSSHConnection(host, userName, password, 22, 0);
    }

    public static Connection getSSHConnection(String host, String userName, String password, int retry) {
        return getSSHConnection(host, userName, password, 22, retry);
    }

    public static Connection getSSHConnection(String host, String userName, String password, int port, int retry) {
        if (StringUtils.isBlank(host) || StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            throw new ParameterException("host、userName 和 password 都不能为空或null!");
        }

        Connection connection = new Connection(host, port);
        int num = 0;
        AtomicBoolean mark = new AtomicBoolean(false);

        Consumer<Connection> consumer = conn -> {

            try {
                if (logger.isDebugEnabled())
                    logger.debug("Try to connect to the host: {}", host);

                conn.connect();
                conn.authenticateWithPassword(userName, password);
                mark.set(true);
                if (logger.isDebugEnabled())
                    logger.debug("Connection created successfully! host: {}", host);
            } catch (Exception e) {
                logger.error("Connection creation failed。host: " + host, e);
            }
        };

        consumer.accept(connection);

        if (mark.get())
            return connection;

        for (int i = 0; i < retry; i++) {

            if (logger.isDebugEnabled())
                logger.debug("retryNum: {}", i);

            consumer.accept(connection);
            if (mark.get())
                break;

            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                //
            }
        }
        return mark.get() ? connection : null;
    }

    public boolean downloadFile(Connection connection, String localDirPath, String remoteFilePath) {
        return downloadFile(connection, localDirPath, remoteFilePath, 0);
    }

    public boolean downloadFile(Connection connection, String localDirPath, String remoteFilePath, int retry) {

        if (connection == null || StringUtils.isBlank(localDirPath) || StringUtils.isBlank(remoteFilePath)) {
            throw new ParameterException("connection、localDirPath 和 remoteFilePath 都不能为空或null!");
        }

        boolean flag = false;
        retry = Math.max(retry, 0);
        for (int i = 0; i <= retry; i++) {
            try {
                File file = new File(localDirPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                SCPClient scpClient = connection.createSCPClient();
                scpClient.get(remoteFilePath, localDirPath);
                flag = true;
                logger.info("download {} successfully。", remoteFilePath);
            } catch (IOException e) {
                logger.error("download " + remoteFilePath + " failed。", e);
            }

            if (flag)
                break;
        }
        return flag;
    }

    public boolean uploadFile(Connection connection, String localFilePath, String remoteFilePath) {
        return uploadFile(connection, localFilePath, remoteFilePath);
    }

    public boolean uploadFile(Connection connection, String localFilePath, String remoteFilePath, int retry) {
        if (connection == null || StringUtils.isBlank(localFilePath) || StringUtils.isBlank(remoteFilePath)) {
            throw new ParameterException("connection、localFilePath 和 remoteFilePath 都不能为空或null!");
        }

        boolean flag = false;
        retry = Math.max(retry, 0);
        for (int i = 0; i <= retry; i++) {
            File file = new File(localFilePath);
            if (!file.exists()) {
                logger.error("The path to upload does not exist");
                break;
            }
            try {
                SCPClient scpClient = connection.createSCPClient();
                scpClient.put(localFilePath, remoteFilePath);
                flag = true;
                logger.info("upload {} successfully。", localFilePath);
            } catch (IOException ioe) {
                logger.error("upload " + localFilePath + " failed。", ioe);
            }

            if (flag)
                break;
        }
        return flag;
    }

    public boolean exec(Connection connection, String cmd) {
        return exec(connection, cmd, 0);
    }

    public boolean exec(Connection connection, String cmd, int retry) {

        if (connection == null || StringUtils.isBlank(cmd)) {
            throw new ParameterException("connection 和cmd 都不能为空或null!");
        }
        boolean flag = false;
        retry = Math.max(retry, 0);
        for (int i = 0; i <= retry; i++) {
            try {
                Session session = connection.openSession();
                session.execCommand(cmd);// 执行命令
                flag = true;
            } catch (Exception e) {
                logger.error("Fail to execute the order: " + cmd, e);
            }
            if (flag)
                break;
        }

        return flag;
    }

    public void close(Connection connection) {
        if (connection != null) {
            connection.close();
        }
    }
}
