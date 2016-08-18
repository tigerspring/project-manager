package com.vcg.code.gennerator.mybatis;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TblUtil {
	
	/**
	 * 表字段结构操作
	 */
	public static void alertTblColunmName(Connection conn, String tblName, String oldName, String newName,
			String colunmType) throws Exception {
		String sql = "ALTER TABLE " + tblName + " CHANGE " + oldName + " " + newName + " " + colunmType;
		conn.createStatement().execute(sql);
	}

	public static void dropTblColunm(Connection conn, String tblName, String colunmName) throws Exception {
		String sql = "ALTER TABLE " + tblName + " DROP " + colunmName;
		conn.createStatement().execute(sql);
	}

	public static void addTblColunm(Connection conn, String tblName, String colunmName, String colunmType)
			throws SQLException {
		String sql = "ALTER TABLE " + tblName + " ADD " + colunmName + " " + colunmType;
		conn.createStatement().execute(sql);
	}

	public static void alertTblColunmOrder(Connection conn, String tblName, String colunmName, String afterColunm)
			throws Exception {
		int colunmSize = TblUtil.getColunmLength(conn, tblName, colunmName);
		String colunmType = TblUtil.getColunmType(conn, tblName, colunmName);
		String sql = "ALTER TABLE " + tblName + "  CHANGE " + colunmName + " " + colunmName + " " + colunmType + "("
				+ colunmSize + ")  AFTER " + afterColunm;
		conn.createStatement().execute(sql);
	}

	public static void reNameTbl(Connection conn, String oldTblName, String newTblName) throws SQLException {
		String sql = "ALTER TABLE " + oldTblName + " RENAME " + newTblName;
		conn.createStatement().execute(sql);
	}

	public static boolean existTbl(Connection conn, String tblName) throws Exception {
		return getTbls(conn).contains(tblName);
	}

	public static void clearTbl(Connection conn, String tblName) throws Exception {
		String sql = "TRUNCATE TABLE " + tblName;
		conn.createStatement().execute(sql);
	}

	public static void dropTbl(Connection conn, String tblName) throws SQLException {
		String sql = "DROP TABLE " + tblName;
		conn.createStatement().execute(sql);
	}

	public static void alertPrimaryKey(Connection conn, String tblName, String colunm) throws SQLException {
		String sql = "ALTER TABLE " + tblName + " ADD PRIMARY KEY(" + colunm + ")";
		conn.createStatement().execute(sql);
	}

	public static void alertColunmNotnull(Connection conn, String tblName, String colunm) throws SQLException {
		String sql = "ALERT TABLE " + tblName + " MODIFY COLUMN " + colunm + " int not null";
		conn.createStatement().execute(sql);
	}

	/**
	 * 自动搜寻字段类型为varchar|int|bigint,Date并建立索引
	 * 
	 * @param conn
	 * @param tblName
	 * @throws Exception
	 */
	public static void createProtoTypeIndex(Connection conn, String tblName) throws Exception {
		List<String> colunms = getColunms(conn, tblName);

		List<String> filterBigInt = filterBigInt(conn, tblName, colunms);
		List<String> filterIntList = filterIntList(conn, tblName, colunms);
		List<String> filterVarcharList = filterVarcharList(conn, tblName, colunms);
		List<String> filterDateList = filterDateList(conn, tblName, colunms);
		List<String> filterCharList = filterCharList(conn, tblName, colunms);
		List<String> filterTinyIntList = filterTinyIntList(conn, tblName, colunms);

		createIndex(conn, tblName, filterBigInt, "bigIntColunm");
		createIndex(conn, tblName, filterIntList, "intColunm");
		createIndex(conn, tblName, filterVarcharList, "varcharColunm");
		createIndex(conn, tblName, filterDateList, "dateColunm");
		createIndex(conn, tblName, filterCharList, "charColunm");
		createIndex(conn, tblName, filterTinyIntList, "tinyintColunm");

		List<String> withOutBlobColunm = filterAllColunmWithOutBlob(conn, tblName, colunms);
		for (String colunm : withOutBlobColunm) {
			createSingleIndex(conn, tblName, colunm);
		}

	}

	public static void createIndex(Connection conn, String tblName, List<String> colunmList, String indexName)
			throws SQLException {
		for (int i = 0; i < colunmList.size(); i++) {
			String index = colunmList.get(i);
			if (index.equals("")) {
				continue;
			}
			String sql = "ALTER TABLE " + tblName + " ADD INDEX " + indexName + i + "(" + index + ")";
			try {
				conn.createStatement().execute(sql);
			} catch (Exception e) {
				System.out.println("为" + tblName + "字段" + index + "建立多列普通索引失败,字段长度过长!");
			}
			System.out.println("为" + tblName + "字段" + index + "建立多列普通索引成功!");
		}
	}

	public static void createSingleIndex(Connection conn, String tblName, String colunm) throws SQLException {
		String createIntIndex = "ALTER TABLE " + tblName + " ADD INDEX " + colunm + " (" + colunm + ")";
		try {
			conn.createStatement().execute(createIntIndex);
		} catch (Exception e) {
			System.out.println("为" + tblName + "字段" + colunm + "建立单列索引失败!");
			return;
		}
		System.out.println("为" + tblName + "字段" + colunm + "建立单列索引成功!");
	}

	public static void createUniIndex(Connection conn, String tblName, String unique) throws Exception {
		String sql = "ALTER TABLE " + tblName + " ADD UNIQUE (" + unique + ")";
		conn.createStatement().execute(sql);
		System.out.println("为" + tblName + "建立唯一索引成功!");
	}

	public static void createPriMaryIndex(Connection conn, String tblName) throws Exception {
		String primaryKey = getPrimaryKey(conn, tblName);
		String sql = "ALTER TABLE " + tblName + " ADD PRIMARY KEY  (" + primaryKey + ")";
		conn.createStatement().execute(sql);
		System.out.println("为" + tblName + "建立主键索引成功!");
	}

	public static void createFullIndex(Connection conn, String tblName, String fullText) throws Exception {
		String sql = "ALTER TABLE " + tblName + " ADD FULLTEXT (" + fullText + ")";
		conn.createStatement().execute(sql);
		System.out.println("为" + tblName + "建立全文索引成功!");
	}

	// 自动为带有id字段的列建立索引
	public static void createIndexForContainsId(Connection conn, String tblName) throws Exception {
		List<String> filterId = filterId(conn, tblName);
		for (String id : filterId) {
			createSingleIndex(conn, tblName, id);
		}
	}

	// 过滤字段类型为varchar|int|bigint类型,
	public static List<String> filterInt(Connection conn, String tblName, List<String> colunms) throws Exception {
		return filterType(conn, tblName, colunms, "int");
	}

	public static List<String> filterBigInt(Connection conn, String tblName, List<String> colunms) throws Exception {
		return filterType(conn, tblName, colunms, "bigint");
	}

	public static List<String> filterDate(Connection conn, String tblName, List<String> colunms) throws Exception {
		List<String> list = new ArrayList<String>();
		List<String> timestamp = filterType(conn, tblName, colunms, "timestamp");
		List<String> date = filterType(conn, tblName, colunms, "date");
		list.addAll(timestamp);
		list.addAll(date);
		return list;
	}

	public static List<String> filterVarchar(Connection conn, String tblName, List<String> colunms) throws Exception {
		return filterType(conn, tblName, colunms, "varchar");
	}

	public static List<String> filterChar(Connection conn, String tblName, List<String> colunms) throws Exception {
		return filterType(conn, tblName, colunms, "char");
	}

	public static List<String> filterTinyInt(Connection conn, String tblName, List<String> colunms) throws Exception {
		return filterType(conn, tblName, colunms, "tinyint");
	}

	public static List<String> filterAllColunmWithOutBlob(Connection conn, String tblName, List<String> colunms)
			throws Exception {
		TreeSet<String> list = new TreeSet<String>();
		for (String colunm : colunms) {
			String colunmType = getColunmType(conn, tblName, colunm).toLowerCase();
			if (colunmType.contains("char")) {
				list.add(colunm);
			}
			if (colunmType.contains("varchar")) {
				list.add(colunm);
			}
			if (colunmType.contains("date")) {
				list.add(colunm);
			}
			if (colunmType.contains("timestamp")) {
				list.add(colunm);
			}
			if (colunmType.contains("int")) {
				list.add(colunm);
			}
			if (colunmType.contains("bigint")) {
				list.add(colunm);
			}
			if (colunmType.contains("tinyint")) {
				list.add(colunm);
			}
		}
		return new ArrayList<String>(list);
	}

	public static List<String> filterIntList(Connection conn, String tblName, List<String> colunms) throws Exception {
		List<String> filterIndex = filterInt(conn, tblName, colunms);
		return subColunmList(filterIndex, 15);
	}

	public static List<String> filterTinyIntList(Connection conn, String tblName, List<String> colunms)
			throws Exception {
		List<String> filterIndex = filterInt(conn, tblName, colunms);
		return subColunmList(filterIndex, 15);
	}

	public static List<String> filterBigIntList(Connection conn, String tblName, List<String> colunms)
			throws Exception {
		List<String> filterIndex = filterInt(conn, tblName, colunms);
		return subColunmList(filterIndex, 15);
	}

	public static List<String> filterDateList(Connection conn, String tblName, List<String> colunms) throws Exception {
		List<String> filterIndex = filterDate(conn, tblName, colunms);
		return subColunmList(filterIndex, 15);
	}

	public static List<String> filterVarcharList(Connection conn, String tblName, List<String> colunms)
			throws Exception {
		List<String> filterIndex = filterVarchar(conn, tblName, colunms);
		return subColunmList(filterIndex, 3);
	}

	public static List<String> filterCharList(Connection conn, String tblName, List<String> colunms) throws Exception {
		List<String> filterIndex = filterVarchar(conn, tblName, colunms);
		return subColunmList(filterIndex, 10);
	}

	public static List<String> filterType(Connection conn, String tblName, List<String> indexs, String type)
			throws Exception {
		List<String> filterList = new ArrayList<String>();
		for (String index : indexs) {
			String colunmType = getColunmType(conn, tblName, index);
			String lotype = colunmType.toLowerCase();
			if (lotype.equals(type)) {
				filterList.add(index);
			}
		}
		return filterList;
	}

	public static List<String> subColunmList(List<String> filterIndex, int indexLeng) {
		int size = filterIndex.size() / indexLeng;
		int num = indexLeng;

		List<String> list = new ArrayList<String>();
		if (size == 0) {
			list.add(listToString(filterIndex));
			return list;
		}

		for (int i = 0; i < size; i++) {
			List<String> subList = filterIndex.subList(i * num, i * num + num);
			list.add(listToString(subList));
		}

		if (filterIndex.size() % num != 0) {
			List<String> subList = filterIndex.subList(size * num, filterIndex.size());
			list.add(listToString(subList));
		}
		return list;
	}

	public static List<String> filterId(Connection conn, String tblName) throws Exception {
		List<String> colunms = getColunms(conn, tblName);
		List<String> filterList = new ArrayList<String>();
		for (String colunm : colunms) {
			if (colunm.toLowerCase().contains("id")) {
				filterList.add(colunm);
			}
		}
		return filterList;

	}

	/**
	 * 增量插入 只支持 mysql
	 * 
	 * @param srcDB
	 *            源数据库
	 * @param destDB
	 *            目标数据库
	 * @param srcTbl
	 *            源表
	 * @param destTbl
	 *            目标表
	 * @return
	 * @throws Exception
	 */
	public static long incrementInsert(Connection srcDB, Connection destDB, String srcTbl, String destTbl)
			throws Exception {

		if (destTbl == null) {
			destTbl = srcTbl;
		}

		String srcTblCountSql = "select count(*) from " + srcTbl;
		String destTblCountSql = "select count(*) from " + destTbl;

		Statement srcSta = srcDB.createStatement();
		Statement destSta = destDB.createStatement();

		srcSta.execute(srcTblCountSql);
		destSta.execute(destTblCountSql);

		ResultSet srcRes = srcSta.getResultSet();
		ResultSet destRes = destSta.getResultSet();

		long srcTblCount = 0;
		long destTblCount = 0;

		while (srcRes.next()) {
			srcTblCount = srcRes.getLong(1);
		}

		while (destRes.next()) {
			destTblCount = destRes.getLong(1);
		}

		long IncrementCount = srcTblCount - destTblCount;

		if ((srcTblCount - destTblCount) == 0) {
			return 0;
		}

		List<String> colunms = getColunms(destDB, destTbl);
		String colunmsStr = listToString(colunms);
		String struggles = listToStruggles(colunms);
		String insertSql = "insert into " + destTbl + " (" + colunmsStr + ") values (" + struggles + ")";
		PreparedStatement preSta = destDB.prepareStatement(insertSql);

		String selectSql = "select " + colunmsStr + " from " + srcTbl + " limit " + destTblCount + "," + IncrementCount;
		srcSta.execute(selectSql);

		ResultSet selectRes = srcSta.getResultSet();
		while (selectRes.next()) {
			for (int i = 1; i <= colunms.size(); i++) {
				Object object = selectRes.getObject(i);
				preSta.setObject(i, object);
				preSta.addBatch();
			}
		}

		int[] num = preSta.executeBatch();
		long allNum = 0;

		for (int i : num) {
			allNum += i;
		}

		return allNum;
	}

	public static void insertByResultSet(ResultSet srcResultSet, Connection srcConn, Connection destConn, String sql)
			throws SQLException, Exception {

		PreparedStatement sta = destConn.prepareStatement(sql);

		int countStruggle = TblUtil.getSqlStrugglesCount(sql);

		List<String> srcTbls = getTbls(srcConn);
		List<String> destTbls = getTbls(destConn);

		String srcCharset = getTblCharset(srcConn, srcTbls.size() > 0 ? srcTbls.get(0) : null);
		String destCharset = getTblCharset(destConn, destTbls.size() > 0 ? destTbls.get(0) : null);

		while (srcResultSet.next()) {
			for (int i = 1; i <= countStruggle; i++) {
				Object object = srcResultSet.getObject(i);

				if (object != null && object.equals("")) {
					object = null;
				}

				if (object instanceof Date) {
					Date date = (Date) object;
					long c = date.getTime() - 31507200000L;
					if (c < 0) {
						object = null;
					}
				}

				if (object instanceof String && !srcCharset.equals(destCharset)) {
					String s = (String) object;
					object = new String(s.getBytes(srcCharset), destCharset);
				}
				sta.setObject(i, object);
			}
			sta.addBatch();
		}
		sta.executeBatch();

		srcResultSet.close();
	}

	public static Map<Connection, Map<String, Map<String, List<Object>>>> TBL_CACHE_MAP = new LinkedHashMap<Connection, Map<String, Map<String, List<Object>>>>();

	public static Map<String, Map<String, List<Object>>> getTblsAndMeta(Connection conn) throws Exception {

		Map<String, Map<String, List<Object>>> map = TBL_CACHE_MAP.get(conn);
		if (map != null) {
			return map;
		}

		map = new LinkedHashMap<String, Map<String, List<Object>>>();
		List<String> tbls = getMysqlTbls(conn);

		// 3. 提取表的名字。
		for (String tblName : tbls) {
			Map<String, List<Object>> colunmNameMap = getColunmAndMeta(conn, tblName);
			map.put(tblName, colunmNameMap);
		}
		TBL_CACHE_MAP.put(conn, map);
		return map;
	}

	public static long getTblCount(Connection srcDB, String srcTbl) throws SQLException {
		String countSql = "select count(*) from " + srcTbl;
		Statement countSta = srcDB.createStatement();
		countSta.execute(countSql);

		ResultSet countRes = countSta.getResultSet();
		long count = 0;
		while (countRes.next()) {
			count = countRes.getLong(1);
		}
		return count;
	}

	public static String getTblCharset(Connection conn, String tblName) throws SQLException {
		String driverName = conn.getMetaData().getDriverName().toLowerCase();
		if (driverName.contains("microsoft")) {
			return "gbk";
		}

		if (tblName == null) {
			return "iso-8859-1";
		}

		Statement sta = conn.createStatement();

		String sql = "show table status from " + conn.getCatalog() + " like '%" + tblName + "%'";
		sta.execute(sql);

		ResultSet resultSet = sta.getResultSet();

		String charset = "";

		while (resultSet.next()) {
			charset = resultSet.getString(15);
		}

		if (charset.toLowerCase().contains("utf8")) {
			return "gbk";
		}
		if (charset.toLowerCase().contains("gbk")) {
			return "gbk";
		}
		if (charset.toLowerCase().contains("gb2312")) {
			return "gb2312";
		}
		if (charset.toLowerCase().contains("iso-8859-1")) {
			return "iso-8859-1";
		}
		if (charset.toLowerCase().contains("latin1")) {
			return "iso-8859-1";
		}

		return "iso-8859-1";
	}

	public static List<String> getTbls(Connection conn) throws Exception {
		if (conn.getMetaData().getDriverName().toLowerCase().contains("mysql")) {
			return getMysqlTbls(conn);
		}

		return getSqlServerTbls(conn);

	}

	public static List<String> getMysqlTbls(Connection conn) throws Exception {
		List<String> tblList = new ArrayList<String>();
		ResultSet tbls = conn.getMetaData().getTables(null, null, null, null);

		// 3. 提取表的名字。
		while (tbls.next()) {
			tblList.add(tbls.getString("TABLE_NAME"));
		}
		return tblList;
	}

	public static List<String> getSqlServerTbls(Connection conn) throws Exception {
		Statement statement = conn.createStatement();
		String database = conn.getCatalog();
		statement.execute("Select NAME FROM " + database + "..SysObjects Where XType='U' ORDER BY Name");
		ResultSet tbls = statement.getResultSet();

		List<String> tblList = new ArrayList<String>();

		// 3. 提取表的名字。
		while (tbls.next()) {
			tblList.add(tbls.getString(1));
		}
		return tblList;
	}

	public static String getPrimaryKey(Connection conn, String tblName) throws SQLException {
		ResultSet resultSet = conn.getMetaData().getPrimaryKeys(null, null, tblName);

		String primaryKey = "";
		while (resultSet.next()) {
			primaryKey = resultSet.getString(4);
		}

		return primaryKey;

	}

	public static Map<String, List<Object>> getColunmAndMeta(Connection conn, String tblName) throws Exception {

		ResultSet colRet = conn.getMetaData().getColumns(null, null, tblName, null);
		Map<String, List<Object>> colunmNameMap = new LinkedHashMap<String, List<Object>>();

		while (colRet.next()) {

			// 字段名称
			String columnName = colRet.getString("COLUMN_NAME");
			// 字段类型
			String columnType = colRet.getString("TYPE_NAME");
			// 字段长度
			int datasize = colRet.getInt("COLUMN_SIZE");
			// DECIMAL精度
			int digits = colRet.getInt("DECIMAL_DIGITS");

			List<Object> colunmMeta = new ArrayList<Object>();
			colunmNameMap.put(columnName, colunmMeta);

			colunmMeta.add(columnType);
			colunmMeta.add(datasize);
			colunmMeta.add(digits);
		}
		return colunmNameMap;
	}

	public static List<String> getColunms(Connection conn, String tblName) throws Exception {
		ResultSet colRet = conn.getMetaData().getColumns(null, null, tblName, null);
		List<String> colunmList = new ArrayList<String>();
		while (colRet.next()) {
			colunmList.add(colRet.getString("COLUMN_NAME"));
		}
		return colunmList;
	}

	public static List<Object> getColunmInfo(Connection conn, String tblName, String colunmName) throws Exception {
		Map<String, List<Object>> colunmAndMeta = getColunmAndMeta(conn, tblName);
		return colunmAndMeta.get(colunmName);
	}

	public static int getColunmLength(Connection conn, String tblName, String colunmName) throws Exception {
		List<Object> infoList = getColunmInfo(conn, tblName, colunmName);
		return Integer.parseInt(infoList.get(1).toString());
	}

	public static String getColunmType(Connection conn, String tblName, String colunmName) throws Exception {
		List<Object> infoList = getColunmInfo(conn, tblName, colunmName);
		return infoList.get(0).toString();
	}

	// 获取历史数据库表名
	public static List<String> getLogTblNames(Connection conn, List<String> filterTable) throws Exception {

		List<String> tbList = new ArrayList<String>();
		List<String> filterList = new ArrayList<String>();
		ResultSet tables = conn.getMetaData().getTables(null, null, null, null);

		while (tables.next()) {
			String tbl_Name = tables.getString("TABLE_NAME");

			if (tbl_Name.indexOf("bak") == -1) {
				tbList.add(tbl_Name);
			}
		}

		// 过滤需要的表
		for (String table : tbList) {
			for (String filter : filterTable) {
				if (table.contains(filter)) {
					filterList.add(table);
				}
			}
		}

		return filterList;
	}

	public static <T> String listToString(List<T> strs) {
		String inIds = "";

		for (T id : strs) {
			inIds += id + ",";
		}

		if (inIds.length() > 1) {
			inIds = inIds.substring(0, inIds.length() - 1);
		}

		return inIds;
	}

	public static String getMysqlOrMSSqlColunmByConn(Connection conn, String tblName) throws Exception {
		String driver = conn.getMetaData().getDriverName().toLowerCase();
		List<String> colunms = getColunms(conn, tblName);

		if (driver.contains("mysql")) {
			return listToMySqlColunmStr(colunms);
		}

		if (driver.contains("microsoft")) {
			return listToSqlServerColunmStr(colunms);
		}

		return listToString(colunms);

	}

	public static <T> String listToSqlServerColunmStr(List<T> colunms) {
		String inIds = "";

		for (T id : colunms) {
			inIds += "[" + id + "]" + ",";
		}

		if (inIds.length() > 1) {
			inIds = inIds.substring(0, inIds.length() - 1);
		}

		return inIds;
	}

	public static <T> String listToMySqlColunmStr(List<T> colunms) {
		String inIds = "";

		for (T id : colunms) {
			inIds += "`" + id + "`" + ",";
		}

		if (inIds.length() > 1) {
			inIds = inIds.substring(0, inIds.length() - 1);
		}

		return inIds;
	}

	public static String listToStruggles(List<String> colunms) {
		String colunmStr = "";

		for (int i = 0; i < colunms.size(); i++) {
			colunmStr += "?,";
		}

		if (colunmStr.length() > 1) {
			colunmStr = colunmStr.substring(0, colunmStr.length() - 1);
		}

		return colunmStr;
	}

	public static int getStrCommaCount(String str) {
		return str.split(",").length;
	}

	public static String getStrugglesByStr(String str) {
		String[] strs = str.split(",");
		List<String> list = new ArrayList<String>();
		for (String s : strs) {
			list.add(s);
		}
		return listToStruggles(list);
	}

	public static String getSqlStrugglesSqlStr(String sql) {
		if (sql == null) {
			return "";
		}
		int indexOf = sql.indexOf("?");
		int lastIndexOf = sql.lastIndexOf("?");
		String struggle = sql.substring(indexOf, lastIndexOf + 1);
		return struggle;
	}

	public static List<String> getColunmTypes(Connection conn, String tblName) throws SQLException {
		List<String> list = new ArrayList<String>();
		Statement sta = conn.createStatement();

		String driverName = conn.getMetaData().getDriverName();
		if (driverName.contains("mysql")) {
			sta.execute("select * from  " + tblName + " limit 0,1");
		} else {
			sta.execute("select top 1 * from " + tblName);
		}

		ResultSet res = sta.getResultSet();
		int columnCount = res.getMetaData().getColumnCount();

		while (res.next()) {
			for (int i = 1; i <= columnCount; i++) {
				String colunmType = res.getMetaData().getColumnClassName(i);
				list.add(colunmType);
			}
		}
		return list;

	}

	public static int getSqlStrugglesCount(String sql) {
		String strugglesStr = getSqlStrugglesSqlStr(sql);
		String[] count = strugglesStr.split(",");
		return count.length;
	}

	public static Connection getConnectionByURL(String className, String url) {
		Connection conn = null;
		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getMethod("getConnectionByUrl", String.class);
			Object object = method.invoke(null, url);
			if (object instanceof Connection) {
				conn = (Connection) object;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}

	public static List<Object> mapToList(List<Map<String, Object>> list, String colunm) {

		Set<Object> set = new TreeSet<Object>();

		for (Map<String, Object> map : list) {
			Object object = map.get(colunm);
			if (object == null) {
				continue;
			}
			set.add(object);
		}
		return new ArrayList<Object>(set);
	}

	public void getAllTbles() {
		String sql = "select table_name from information_schema.tables where table_schema='webtest'";
		System.out.println(sql);
	}

	public static Calendar cal = Calendar.getInstance();// 使用日历类

	public static String getDateStrAndBak() {
		return getDate() + (UUID.randomUUID().toString().split("-"))[0];
	}

	public static String getDate() {
		int hour = cal.get(Calendar.HOUR);// 得到小时
		int minute = cal.get(Calendar.MINUTE);// 得到分钟
		int second = cal.get(Calendar.SECOND);// 得到秒
		return getCureentYear() + "" + getCureentMonth() + "" + getCureentDay() + hour + minute + second;
	}

	public static int getCureentMonth() {
		return cal.get(Calendar.MONTH) + 1;
	}

	public static int getCureentYear() {
		return cal.get(Calendar.YEAR);// 得到年
	}

	public static int getCureentDay() {
		return cal.get(Calendar.DAY_OF_MONTH);// 得到天
	}

	public static List<String> getColumnInfo(Connection conn, String tblName) throws SQLException {
		ResultSet columns = conn.getMetaData().getColumns(null, null, tblName, null);
		List<String> lists = new ArrayList<String>();
		while (columns.next()) {
			String string = columns.getString("REMARKS");
			lists.add(string);
		}
		return lists;

	}

}
