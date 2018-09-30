package com.zjd.zhu.util.generate.mapper;

import com.zjd.zhu.util.str.StringUtil;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GeneratorDDL {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("请输入类的全路径包含类名（多个类用 ,隔开）：");
		String classNames = br.readLine();
		if (!classNames.isEmpty()) {
			classNames = classNames.trim();
			String[] classArray = classNames.split(",");
			for (String className : classArray) {
				Class clazz = Class.forName(className);
//				table(clazz);
//				seq(clazz);
//				primaryKey(clazz);
//				uniqueKey(clazz);

				// 生成mapper文件
//				printMapper(clazz);
				printResultMapper(clazz);
				printBatchInsertMapper(clazz);
			}
		}
	}

	static class Model {
		public String column;

		public String prop;

		public String types;
	}

	@SuppressWarnings({ "rawtypes" })
	public static String getTypesByField(Field field) {
		String res = "VARCHAR";
		Class type = field.getType();
		if (type == String.class) {
			res = "VARCHAR";
		} else if (type == Long.class) {
			res = "BIGINT";
		} else if (type == Integer.class) {
			res = "INTEGER";
		} else if (type == Double.class || type == Float.class) {
			res = "NUMERIC";
		} else if (type == Date.class) {
			res = "TIMESTAMP";
		} else if (type == Boolean.class || type == boolean.class) {
			res = "BIT";
		}
		return res;
	}

	@SuppressWarnings("rawtypes")
	private static List<Model> getColumnsByType(Class clazz) {

		List<Model> ret = new ArrayList<Model>();

		String[] strs = {
				"id", "id", "NUMERIC",
				"createTime", "createTime", "TIMESTAMP",
				"createBy", "create_by","VARCHAR",
				"updateTime", "update_time", "TIMESTAMP",
				"updateBy","update_by", "varchar",
				"status","status", "tinyint"
		};

		boolean isInstanceOfBaseBizEntity = false;
		try {
			Object t = clazz.newInstance();
//			if (t instanceof BaseModel) {
//				isInstanceOfBaseBizEntity = true;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isInstanceOfBaseBizEntity) {
			for (int k = 0; k < strs.length; k = k + 3) {
				Model model = new Model();
				model.column = strs[k];
				model.prop = strs[k + 1];
				model.types = strs[k + 2];
				ret.add(model);
			}
		}

		String columName = "";
		for (Field field : clazz.getDeclaredFields()) {
			if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
				continue;
			}
			Model model = new Model();
			columName = field.getName();

			if (field.isAnnotationPresent(Column.class)
					&& StringUtil.isNotBlank(field.getAnnotation(Column.class)
							.name())) {
				columName = field.getAnnotation(Column.class).name()
						.toLowerCase();
			}
			model.column = columName;
			model.prop = field.getName();
			model.types = getTypesByField(field);
			ret.add(model);
		}

		return ret;
	}

	private static void printMapper(Class clazz) {
		String tableName = JPAHelper.getTableName(clazz).toUpperCase();
		List<Model> list = getColumnsByType(clazz);


		for (int k = 1; k < list.size(); k++) {

		}


		String seqName = JPAHelper.getSequenceName(clazz);

		String typeName = clazz.getSimpleName();
		// typeAlias
//		System.out.println("<typeAlias alias=\"" + typeName + "\" type=\""
//				+ clazz.getName() + "\"/>");
//		System.out.println("<typeAlias alias=\"" + typeName + "SO\" type=\""
//				+ clazz.getName().replace("entity", "so") + "SO\"/>");
//		System.out.println();

		String indentation1 = "    ";
		String indentation2 = "        ";
		String indentation3 = "            ";
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
		sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" "
				+ "\n");
		sb.append("\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">" + "\n");
		sb.append("<mapper namespace=\"" + typeName + "\">" + "\n");
		sb.append("" + "\n");

		sb.append("" + "\n");
		sb.append(indentation1 + " <!-- get data from DB  start -->" + "\n");

		// getById
		sb.append(indentation1
				+ " <select id=\"getById\" parameterType=\"long\" resultType=\""
				+ typeName + "\">" + "\n");
		sb.append(indentation2 + " SELECT * FROM " + tableName
				+ " WHERE ID = #{id}" + "\n");
		sb.append(indentation1 + " </select>" + "\n");
		sb.append("" + "\n");
		// getListByIds
		sb.append(indentation1
				+ " <select id=\"getListByIds\" parameterType=\"List\" resultType=\""
				+ typeName + "\">" + "\n");
		sb.append(indentation2 + " SELECT * FROM " + tableName
				+ " t WHERE t.id in" + "\n");
		sb.append(indentation2
				+ " <foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" close=\")\" separator=\",\">"
				+ "\n");
		sb.append(indentation3 + " #{item}" + "\n");
		sb.append(indentation2 + " </foreach>" + "\n");
		sb.append(indentation1 + " </select> " + "\n");
		sb.append("" + "\n");

		// getAll
		sb.append(indentation1 + " <select id=\"getAll\" resultType=\""
				+ typeName + "\">" + "\n");
		sb.append(indentation2 + " SELECT * FROM " + tableName + " " + "\n");
		sb.append(indentation1 + " </select>" + "\n");
		sb.append("" + "\n");

		// getListByField
		sb.append(indentation1 + " <select id=\"getListByField\" resultType=\""
				+ typeName + "\">" + "\n");
		sb.append(indentation2 + " SELECT * FROM " + tableName
				+ " WHERE ${fieldName} = #{colValue} " + "\n");
		sb.append(indentation1 + " </select>" + "\n");
		sb.append("" + "\n");

		// getListBySo
		sb.append(indentation1 + " <select id=\"getListBySo\" parameterType=\""
				+ typeName + "SO\" resultType=\"" + typeName + "\">" + "\n");
		sb.append(indentation2 + " SELECT t.* FROM " + tableName + " t " + "\n");
		sb.append(indentation2 + " <include refid=\"SO_Where_Clause\" />"
				+ "\n");
		sb.append(indentation1 + " </select>" + "\n");
		sb.append("" + "\n");

		// getCountBySo
		sb.append(indentation1
				+ " <select id=\"getCountBySo\" parameterType=\"" + typeName
				+ "SO\" resultType=\"long\">" + "\n");
		sb.append(indentation2 + " SELECT COUNT(t.id) FROM " + tableName
				+ " t\n");
		sb.append(indentation2 + " <include refid=\"SO_Where_Clause\" />"
				+ "\n");
		sb.append(indentation1 + " </select>" + "\n");
		sb.append("" + "\n");

		// SO_Where_Clause
		sb.append(indentation1 + " <sql id=\"SO_Where_Clause\">" + "\n");
		sb.append(indentation2 + " <where>" + "\n");
		sb.append(indentation2 + " 1 = 1 " + "\n");
		sb.append(indentation3 + " <if test = \"name != null and name!=''\">"
				+ "\n");
		sb.append(indentation3 + "  and name like concat('%',#{name},'%') "
				+ "\n");
		sb.append(indentation3 + " </if>" + "\n");
		sb.append(indentation2 + " </where>" + "\n");
		sb.append(indentation2
				+ " <!-- <include refid=\"Base.Order_By_Clause\" /> -->" + "\n");
		sb.append(indentation1 + " </sql>" + "\n");

		sb.append(indentation1 + " <!-- get data from DB  end -->" + "\n");
		sb.append("" + "\n");

		sb.append(indentation1 + " <!-- insert data into DB  start-->" + "\n");
		// insert
		sb.append(indentation1
				+ " <insert id=\"insert\" useGeneratedKeys=\"true\" keyProperty=\"id\" parameterType=\""
				+ typeName + "\">" + "\n");

		sb.append(indentation2 + " INSERT INTO " + tableName + "(\n");
		for (int k = 1; k < list.size(); k++) {
			Model model = list.get(k);
			if (k == list.size() - 1) {
				sb.append(indentation2 + " <!-- " + k + " --> " + model.column
						+ "\n");
			} else {
				sb.append(indentation2 + " <!-- " + k + " --> " + model.column
						+ ",\n");
			}
		}
		sb.append(indentation2 + ")\n");
		sb.append(indentation2 + " VALUES( \n");
		for (int k = 1; k < list.size(); k++) {
			Model model = list.get(k);
			if (k == list.size() - 1) {
				sb.append(indentation2 + " <!-- " + k + " --> #{ " + model.prop
						+ ":" + model.types + " }\n");
			} else {
				sb.append(indentation2 + " <!-- " + k + " --> #{ " + model.prop
						+ ":" + model.types + " },\n");
			}
		}
		sb.append(indentation2 + ")\n");
		sb.append(indentation1 + "</insert>" + "\n");
		sb.append("" + "\n");

		sb.append(indentation1 + "<!-- insert data into DB  end -->" + "\n");
		sb.append("" + "\n");

		sb.append(indentation1 + "<!-- update data start -->" + "\n");
		// update
		sb.append(indentation1 + " <update id=\"update\" parameterType=\""
				+ typeName + "\">" + "\n");
		sb.append(indentation2 + "  UPDATE " + tableName + " SET \n");
		for (int k = 0; k < list.size(); k++) {
			Model model = list.get(k);
			if (!model.column.equals("optlock") && !model.column.equals("id")) {
				sb.append(indentation3 + " " + model.column + " = #{ "
						+ model.prop + ":" + model.types + " },\n");
			}
		}
		sb.append(indentation3 + " optlock = optlock + 1 \n");
		sb.append(indentation2 + " WHERE \n");
		sb.append(indentation3 + "     ID = #{id} \n");
		sb.append(indentation3 + " AND OPTLOCK = #{optlock} \n");
		sb.append(indentation1 + " </update>" + "\n");
		sb.append("" + "\n");

		sb.append(indentation1 + " <!-- update data end -->" + "\n");
		sb.append("" + "\n");
		sb.append(indentation1 + " <!-- delete data start -->" + "\n");
		sb.append("" + "\n");
		// deleteById
		sb.append(indentation1
				+ " <delete id=\"deleteById\" parameterType=\"long\">" + "\n");
		sb.append(indentation2 + "  DELETE FROM " + tableName
				+ " WHERE ID = #{id} " + "\n");
		sb.append(indentation1 + " </delete>" + "\n");
		sb.append("" + "\n");

		// deleteByIds
		sb.append(indentation1
				+ " <delete id=\"deleteByIds\" parameterType=\"List\">" + "\n");
		sb.append(indentation2 + "  DELETE FROM " + tableName
				+ " t WHERE t.id IN " + "\n");

		sb.append(indentation3
				+ " <foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" close=\")\" separator=\",\">"
				+ "\n");
		sb.append(indentation3 + "   #{item} " + "\n");
		sb.append(indentation3 + " </foreach>" + "\n");

		sb.append(indentation1 + " </delete>" + "\n");
		sb.append("" + "\n");

		sb.append(indentation1 + "<!-- delete data end -->" + "\n");
		sb.append("" + "\n");

		sb.append("</mapper>" + "\n");
		System.out.println(sb.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })

	/**
	 *     <resultMap id="buildingResult" type="com.vanke.pension.bean.floor.Building">
	 *         <result property="id" column="id" />
	 *         <result property="name" column="name" />
	 *         <result property="orgId" column="org_id" />
	 *         <collection property="floorList" fetchType="lazy" ofType="com.vanke.pension.bean.floor.Floor" column="id"
	 *                     javaType="ArrayList" select="com.vanke.pension.dao.FloorMapper.queryListByBuildingId"/>
	 *     </resultMap>
	 * */

	private static void printResultMapper(Class clazz){
		String typeName = clazz.getSimpleName();
		String tableName = JPAHelper.getTableName(clazz).toUpperCase();
		StringBuffer sb = new StringBuffer();
		sb.append("<resultMap id=\""+tableName+"Result\" type=\""+clazz.getName()+"\">\n");
		List<Model> list = getColumnsByType(clazz);
		for (Model model : list) {
			sb.append("\t<result property=\""+model.prop+"\" column=\""+underscoreName(model.column)+"\" />\n");
		}
		sb.append("</resultMap>");
		System.out.println(sb.toString());
	}

	/**
	 * <insert id="batchCreate"  parameterType="java.util.List">
	 *         insert into sample_order
	 *         (`total_Price`,buy_Date,`receiver_Address`,`receiver_name`,`status`,`create_by`,`create_time`,`update_by`,`update_time`)
	 *         values
	 *         <foreach collection="list" item="item" index="index" separator="," >
	 *             (
	 *             #{item.totalPrice},
	 *             #{item.buyDate},
	 *             #{item.receiverAddress},
	 *             #{item.receiverName},
	 *             #{item.status},
	 *             #{item.createBy},
	 *             #{item.createTime},
	 *             #{item.updateBy},
	 *             #{item.updateTime}
	 *             )
	 *         </foreach>
	 *     </insert>
	 * @param clazz
	 */
	private static void printBatchInsertMapper(Class clazz){
		String typeName = clazz.getSimpleName();
		String tableName = JPAHelper.getTableName(clazz).toUpperCase();
		StringBuffer sb = new StringBuffer();
		sb.append("<insert id=\"batchCreate\"  parameterType=\"java.util.List\">\n");
		sb.append("insert into "+tableName);
		sb.append("(\n");
		List<Model> list = getColumnsByType(clazz);
		for (Model model : list) {
			sb.append("\t`"+underscoreName(model.column)+"`,\n");
		}
		sb.replace(0,sb.length(),sb.substring(0,sb.lastIndexOf(",")));
		sb.append(")\n values\n");
		sb.append("<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\" >\n");
		sb.append("(\n");
		for (Model model : list) {
			sb.append("\t#{item."+model.prop+"},\n");
		}
		sb.replace(0,sb.length(),sb.substring(0,sb.lastIndexOf(",")));
		sb.append("\n)\n");
		sb.append("</foreach>\n");
		sb.append("</insert>\n");
		System.out.println(sb.toString());
	}

	public static String underscoreName(String name) {
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			result.append(name.substring(0, 1));
			// 循环处理其余字符
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				// 在大写字母前添加下划线
				if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
					result.append("_");
				}
				// 其他字符直接转成大写
				result.append(s);
			}
		}
		return result.toString().toLowerCase();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String table(Class clazz) {
		StringBuffer sql = new StringBuffer();
		if (clazz.isAnnotationPresent(Table.class)) {
			// 该class存在Table类型的注解，获取指定的表名
			Table table = (Table) clazz.getAnnotation(Table.class);
			String tableName = table.name();
			sql.append("--create table " + tableName + "\n");
			sql.append("create table `" + tableName + "` (");
		}
		Field[] fArr = clazz.getDeclaredFields();
		List<String> columnList = getColumns(fArr);
		// 拼接解析后的成员变量信息成创建表语句
		for (int i = 0; i < columnList.size(); i++) {
			if (i == (columnList.size() - 1)) {
				sql.append("\n" + columnList.get(i) + ",\nPRIMARY KEY (`id`) )");
			} else {
				sql.append("\n" + columnList.get(i) + ",");
			}
		}
		sql.append(";");
		sql.append("\n");
		System.out.println(sql.toString());
		return sql.toString();
	}

	/**
	 * CREATE TABLE `company` (
	 *   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
	 *   `name` varchar(100) NOT NULL COMMENT '公司工商注册名称',
	 *   `credit_code` varchar(20) DEFAULT NULL COMMENT '统一社会信用代码',
	 *   `legal_person` varchar(20) DEFAULT NULL COMMENT '公司法人',
	 *   `mobile` varchar(13) DEFAULT NULL COMMENT '电话',
	 *   `province` varchar(20) DEFAULT NULL COMMENT '省',
	 *   `city` varchar(20) DEFAULT NULL COMMENT '市',
	 *   `area` varchar(100) DEFAULT NULL COMMENT '区',
	 *   `address` varchar(500) DEFAULT NULL COMMENT '地址',
	 *   `bank_name` varchar(20) DEFAULT NULL COMMENT '银行名称',
	 *   `bank_code` varchar(20) DEFAULT NULL COMMENT '开户行',
	 *   `bank_account` varchar(20) DEFAULT NULL COMMENT '银行账号',
	 *   `create_by` varchar(20) DEFAULT NULL COMMENT '创建人',
	 *   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	 *   `update_by` varchar(20) DEFAULT NULL COMMENT '更新人',
	 *   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
	 *   `STATUS` int(11) DEFAULT '1' COMMENT '状态：1：正常；-1：已删除；2：未启用',
	 *   PRIMARY KEY (`id`),
	 *   KEY `Index_company_name` (`NAME`)
	 * ) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='公司表';
	 * 用来解析所有成员变量的方法
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getColumns(Field[] fArr) {
		List<String> result = new ArrayList<String>();
		result.add("`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键'");
		String columnName = "";
		String columnLength = "";
		String columnType = "";
		for (int i = 0; i < fArr.length; i++) {
			Field f = fArr[i];
			columnName = f.getName();
			if ("serialVersionUID".equalsIgnoreCase(columnName)) {
				continue;
			}
			boolean haveLength = true;
			if (f.isAnnotationPresent(Id.class)) {
				// columnName = f.getName();
				columnLength = "19";
				String str = columnName + " number" + "(" + columnLength + ")";
				result.add(str);
			} else {
				if (f.isAnnotationPresent(Column.class)) {
					columnName = f.getAnnotation(Column.class).name();
				}/*
				 * else{ columnName = f.getName(); }
				 */
				Class type = f.getType();
				if (Integer.class == type) {
					columnLength = "10";
					columnType = "int";
				} else if (Long.class == type) {
					columnLength = "19";
					columnType = "bigint";
				} else if (Float.class == type || Double.class == type) {
					columnLength = "19,4";
					columnType = "number";
				} else if (Boolean.class == type) {
					columnLength = "1";
					columnType = "tinyint";
				} else if (Date.class == type) {

					columnType = "datetime";
					haveLength = false;

				} else if (BigDecimal.class == type) {

					columnType = "decimal(10,2)";
					haveLength = false;

				}else {
					columnLength = "255";
					columnType = "varchar";
				}
				String strColumnLength = "";
				if (haveLength) {
					strColumnLength = " ( " + columnLength + " ) ";
				}
				String str = "`"+columnName + "` " + columnType + strColumnLength;
				result.add(str);
			}
		}
		result.add("`create_by` varchar(20) DEFAULT NULL COMMENT '创建人'");
		result.add("`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");
		result.add("`update_by` varchar(20) DEFAULT NULL COMMENT '更新人'");
		result.add("`update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间'");
		result.add("`status` tinyint(1) DEFAULT '1' COMMENT '状态：1：正常；-1：已删除；2：未启用'");
		return result;
	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private static String seq(Class clazz) {
//		StringBuffer sequence = new StringBuffer();
//		if (clazz.isAnnotationPresent(SequenceGenerator.class)) {
//			// 该class存在Table类型的注解，获取指定的表名
//			SequenceGenerator seq = (SequenceGenerator) clazz
//					.getAnnotation(SequenceGenerator.class);
//			String seqName = seq.name();
//			sequence.append("--create sequence " + seqName + "\n");
//			sequence.append("create sequence " + seqName);
//			sequence.append(" minvalue 1 ");
//			sequence.append(" maxvalue 999999999999999999999999999 ");
//			sequence.append(" start with 60001 ");
//			sequence.append(" increment by 1 ");
//			sequence.append(" cache 20; ");
//			sequence.append("\n");
//		}
//		System.out.println(sequence.toString());
//		return sequence.toString();
//	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private static String primaryKey(Class clazz) {
//		StringBuffer key = new StringBuffer();
//
//		if (clazz.isAnnotationPresent(Table.class)) {
//			// 该class存在Table类型的注解，获取指定的表名
//			Table table = (Table) clazz.getAnnotation(Table.class);
//			String tableName = table.name();
//			key.append("--create key:ID " + "\n");
//			key.append("alter table " + tableName + " add primary key (ID);");
//			key.append("\n");
//		}
//		System.out.println(key.toString());
//		return key.toString();
//	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String uniqueKey(Class clazz) {
		StringBuffer key = new StringBuffer();

		if (clazz.isAnnotationPresent(Table.class)) {
			// 该class存在Table类型的注解，获取指定的表名
			Table table = (Table) clazz.getAnnotation(Table.class);
			String tableName = table.name();
			key.append("--create unique key:CODE " + "\n");
			key.append("alter table " + tableName + " add unique (CODE);");
			key.append("\n");
		}
		System.out.println(key.toString());
		return key.toString();
	}
}
