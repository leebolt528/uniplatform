package com.bonc.uni.usou.util;

import com.bonc.uni.usou.entity.ClusterInfo;
import com.bonc.uni.usou.exception.ConnectionException;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.EnableInvoke;
import com.bonc.usdp.odk.logmanager.LogManager;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yedunyao on 2017/8/25.
 */
public class ControllerUtil {

    public static ClusterInfo getClusterInfoFromSession(int id, HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = null;

        try {
            clusterInfo = (ClusterInfo) session.getAttribute(CommonEnum.CLUSTERIDPREFIX +
                    id + CommonEnum.CLUSTERIDSUFFIX);

            if (clusterInfo == null) {
                ResponseException e = new ResponseException("Cluster host is null,  " +
                        "maybe host did not store in DB or session.");
                LogManager.Exception(e);
                throw e;
            }
        } catch (Exception e) {
            ResponseException ex = new ResponseException("Server internal error.Exception: " + e.getMessage());
            LogManager.Exception(ex);
            throw ex;
        }
        return clusterInfo;
    }

    public static void clearClusterSession(int id, HttpSession session) {
        String clusterKey = CommonEnum.CLUSTERIDPREFIX +
                id + CommonEnum.CLUSTERIDSUFFIX;
        session.removeAttribute(clusterKey);
        session.invalidate();
    }

    //轮询
    public static Object pollingCluster(EnableInvoke service, String methodName,
                                        String[] urls, Object...args) throws ResponseException {

        LogManager.method("ControllerUtil invokeService");

        //遍历访问集群的多个client，避免client down掉。
        String url = null;
        for(int i = 0; i < urls.length; i++) {
            url = urls[i];
            String host = "http://" + url;
            Object result = null;
            //反射调用service方法
            try {
                Class clazz = service.getClass();
                Method method = null;
                if (args == null) {
                    method = clazz.getDeclaredMethod(methodName, String.class);
                    result = method.invoke(service, host);
                } else {
                    int len = args.length;
                    Class[] classes = new Class[len + 1];
                    Object[] objects = new Object[len + 1];
                    classes[0] = String.class;
                    objects[0] = host;
                    for (int j = 0; j < len; j++) {
                        classes[j + 1] = args[j].getClass();
                        objects[j + 1] = args[j];
                    }
                    method = clazz.getDeclaredMethod(methodName, classes);
                    result = method.invoke(service, objects);
                }
                return result;
            } catch (NoSuchMethodException e) {
                LogManager.Exception(e);
            } catch (IllegalAccessException e) {
                LogManager.Exception(e);
            } catch (InvocationTargetException e) {
                if (e.getTargetException().getClass() == ResponseException.class) {
                    String message = e.getTargetException().getMessage();
                    throw new ResponseException(message);
                } else if (e.getTargetException() instanceof ConnectionException) {
                    break;
                } else if (e.getTargetException() instanceof Exception) {
                    throw new ResponseException("Server internal error." + e.getTargetException().getMessage());
                }
            }  catch (Exception e) {
                LogManager.Exception(e);
				throw new ResponseException("Server internal error." + e.getMessage());
            }
        }
        throw new ResponseException("Cluster can not be connected.");
    }
}
