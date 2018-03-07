/**  
* @Title: ConnectionDB.java
* @Package com.wlwl.cube.mysql
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年10月9日 下午4:12:14
* @version V1.0.0  
*/
package com.wlwl.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.config.PropertyResource;
import com.wlwl.model.VehicleInfo;
import com.wlwl.utils.Config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class JdbcUtils {

	private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

	// private Config _config;
	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	public JdbcUtils() {
		// this._config=config;

	}

	/**
	 * 增加、删除、改
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public boolean updateByPreparedStatement(String sql, List<Object> params) throws SQLException {
		Connection connection = null;
		PreparedStatement pstmt = null;

		boolean flag = false;
		int result = -1;
		try {
			connection = C3P0DBUtil.getConnection();
			pstmt = connection.prepareStatement(sql);
			int index = 1;
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(index++, params.get(i));
				}
			}
			result = pstmt.executeUpdate();
			flag = result > 0 ? true : false;

		} catch (SQLException e) {
			logger.error("保存车辆迁移日报表异常!" + e.toString());
		} finally {
			C3P0DBUtil.attemptClose(pstmt);
			C3P0DBUtil.attemptClose(connection);

		}
		return flag;

	}

	/**
	 * 查询单条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> findSimpleResult(String sql, List<Object> params) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		int index = 1;
		try {
			connection = C3P0DBUtil.getConnection();
			pstmt = connection.prepareStatement(sql);
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(index++, params.get(i));
				}
			}
			resultSet = pstmt.executeQuery();// 返回查询结果
			ResultSetMetaData metaData = resultSet.getMetaData();
			int col_len = metaData.getColumnCount();
			while (resultSet.next()) {
				for (int i = 0; i < col_len; i++) {
					String cols_name = metaData.getColumnName(i + 1);
					Object cols_value = resultSet.getObject(cols_name);
					if (cols_value == null) {
						cols_value = "";
					}
					map.put(cols_name, cols_value);
				}
			}
		} catch (SQLException e) {
			logger.error("保存车辆迁移日报表异常!", e);
		} finally {
			C3P0DBUtil.attemptClose(resultSet);
			C3P0DBUtil.attemptClose(connection);
			C3P0DBUtil.attemptClose(pstmt);
		}
		return map;
	}

	/**
	 * 查询多条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> findModeResult(String sql, List<Object> params) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int index = 1;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			connection = C3P0DBUtil.getConnection();
			pstmt = connection.prepareStatement(sql);
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(index++, params.get(i));
				}
			}
			resultSet = pstmt.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int cols_len = metaData.getColumnCount();
			while (resultSet.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < cols_len; i++) {
					String cols_name = metaData.getColumnName(i + 1);
					Object cols_value = resultSet.getObject(cols_name);
					if (cols_value == null) {
						cols_value = "";
					}
					map.put(cols_name, cols_value);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			logger.error("保存车辆迁移日报表异常!", e);
		} finally {
			C3P0DBUtil.attemptClose(resultSet);
			C3P0DBUtil.attemptClose(connection);
			C3P0DBUtil.attemptClose(pstmt);
		}
		return list;
	}

	/**
	 * 通过反射机制查询单条记录
	 * 
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> T findSimpleRefResult(String sql, List<Object> params, Class<T> cls) throws Exception {
		T resultObject = null;
		int index = 1;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			connection = C3P0DBUtil.getConnection();

			pstmt = connection.prepareStatement(sql);
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(index++, params.get(i));
				}
			}
			resultSet = pstmt.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int cols_len = metaData.getColumnCount();
			while (resultSet.next()) {
				// 通过反射机制创建一个实例
				resultObject = cls.newInstance();
				for (int i = 0; i < cols_len; i++) {
					String cols_name = metaData.getColumnName(i + 1);
					Object cols_value = resultSet.getObject(cols_name);
					if (cols_value == null) {
						cols_value = "";
					}
					Field field = cls.getDeclaredField(cols_name);
					field.setAccessible(true); // 打开javabean的访问权限
					field.set(resultObject, cols_value);
				}
			}
		} catch (SQLException e) {
			logger.error("保存车辆迁移日报表异常!", e);
		} finally {
			C3P0DBUtil.attemptClose(resultSet);
			C3P0DBUtil.attemptClose(connection);
			C3P0DBUtil.attemptClose(pstmt);
		}
		return resultObject;

	}

	/**
	 * 通过反射机制查询多条记录
	 * 
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> findMoreRefResult(String sql, List<Object> params, Class<T> cls) throws Exception {
		List<T> list = new ArrayList<T>();
		int index = 1;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			connection = C3P0DBUtil.getConnection();
			pstmt = connection.prepareStatement(sql);
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(index++, params.get(i));
				}
			}
			resultSet = pstmt.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int cols_len = metaData.getColumnCount();
			while (resultSet.next()) {
				// 通过反射机制创建一个实例
				T resultObject = cls.newInstance();
				for (int i = 0; i < cols_len; i++) {
					String cols_name = metaData.getColumnName(i + 1);
					Object cols_value = resultSet.getObject(cols_name);
					if (cols_value == null) {
						cols_value = "";
					}
					Field field = cls.getDeclaredField(cols_name);
					field.setAccessible(true); // 打开javabean的访问权限
					field.set(resultObject, cols_value);
				}
				list.add(resultObject);
			}
		} catch (SQLException e) {
			logger.error("保存车辆迁移日报表异常!", e);
		} finally {
			if (resultSet != null) {
				C3P0DBUtil.attemptClose(resultSet);
			}
			if (connection != null) {
				C3P0DBUtil.attemptClose(connection);
			}
			if (pstmt != null) {
				C3P0DBUtil.attemptClose(pstmt);
			}
		}
		return list;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws SQLException {
		//配置日志目录路径
		try {
			String path = new File(".").getCanonicalPath() + "/resource/log4j.properties";
			
			PropertyConfigurator.configure(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JdbcUtils jdbcUtils = new JdbcUtils();
		String sql = "select vi.unid ,device.device_id ,device.cellphone ,pro.root_proto_unid "
				+ " from cube.BIG_VEHICLE vi "
				+ " inner join cube.BIG_DEVICE_VEHICLE_MAP map on vi.unid=map.vehicle_unid "
				+ " inner join cube.BIG_DEVICE device on device .unid=map.device_unid  "
				+ " inner join cube.BIG_FIBER  pro on vi.fiber_unid =pro.unid";
		List<Object> params = new ArrayList<Object>();
		// params.add("90A62ABEA6DB415D93D17DD31FBD5A1B");
		List<VehicleInfo> list = null;
		try {
			list = (List<VehicleInfo>) jdbcUtils.findMoreRefResult(sql, params, VehicleInfo.class);
			System.out.println(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// RedisUtils util = new RedisUtils();
		// Set<String> set = util.keys(Conf.PERFIX + "*");

		// for (String str : set) {
		// System.out.println(str);
		// }

		// TODO Auto-generated method stub
		// JdbcUtils jdbcUtils = new JdbcUtils();
		// jdbcUtils.getConnection();

		/******************* 增 *********************/
		/*
		 * String sql =
		 * "insert into userinfo (username, pswd) values (?, ?), (?, ?), (?, ?)"
		 * ; List<Object> params = new ArrayList<Object>(); params.add("小明");
		 * params.add("123xiaoming"); params.add("张三"); params.add("zhangsan");
		 * params.add("李四"); params.add("lisi000"); try { boolean flag =
		 * jdbcUtils.updateByPreparedStatement(sql, params);
		 * System.out.println(flag); } catch (SQLException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		/******************* 删 *********************/
		// 删除名字为张三的记录
		/*
		 * String sql = "delete from userinfo where username = ?"; List<Object>
		 * params = new ArrayList<Object>(); params.add("小明"); boolean flag =
		 * jdbcUtils.updateByPreparedStatement(sql, params);
		 */

		/******************* 改 *********************/
		// 将名字为李四的密码改了
		/*
		 * String sql = "update userinfo set pswd = ? where username = ? ";
		 * List<Object> params = new ArrayList<Object>();
		 * params.add("lisi88888"); params.add("李四"); boolean flag =
		 * jdbcUtils.updateByPreparedStatement(sql, params);
		 * System.out.println(flag);
		 */

		/******************* 查 *********************/

		// String sql = "SELECT code,option,value,VALUE_LAST ,status FROM
		// cube.PDA_VEHICLE_DETAIL where fiber_unid=?";
		// List<Object> params = new ArrayList<Object>();
		// params.add("90A62ABEA6DB415D93D17DD31FBD5A1B");
		// List<VehicleStatusBean> list = null;
		// try {
		// list = (List<VehicleStatusBean>) jdbcUtils.findMoreRefResult(sql,
		// params, VehicleStatusBean.class);
		// System.out.println(list);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// 不利用反射查询多个记录
		// String sql2 = "SELECT code,option,value,value_last valueLast,status
		// FROM cube.PDA_VEHICLE_DETAIL ";
		// List<Map<String, Object>> list = jdbcUtils.findModeResult(sql2,
		// null);
		// System.out.println(list);
		//
		// //利用反射查询 单条记录
		// String sql = "select * from userinfo where username = ? ";
		// List<Object> params = new ArrayList<Object>();
		// params.add("李四");
		// UserInfo userInfo;
		// try {
		// userInfo = jdbcUtils.findSimpleRefResult(sql, params,
		// UserInfo.class);
		// System.out.print(userInfo);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}