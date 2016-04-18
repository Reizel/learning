package services;

import java.lang.reflect.Field;

import java.util.ArrayList;

import model.BaseObject;

public class SqlGenerator<B extends BaseObject> {
	private String tableName;
	private Class<B> genericClass;
	protected ArrayList<String> columnNames = new ArrayList<String>();;

	public SqlGenerator(Class<B> genericClass) {
		this.genericClass = genericClass;
		tableName = genericClass.getAnnotation(javax.persistence.Table.class).name();
		for (Field f : genericClass.getDeclaredFields()) {
			if ((f.getAnnotation(javax.persistence.Column.class)) != null) {
				String annotName = f.getAnnotation(javax.persistence.Column.class).name();
				columnNames.add(annotName);
			}
		}
	}

	public String generateSelect() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		for (String str : columnNames) {

			sql.append(str);
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" FROM ");
		sql.append(tableName);
		return sql.toString();
	}

	public String generateSelectBuId(int id) {
		StringBuilder sql = new StringBuilder(generateSelect());
		sql.append(" WHERE ");
		for (Field f : genericClass.getDeclaredFields()) {
			if (f.getAnnotation(javax.persistence.Id.class) != null) {
				sql.append(f.getAnnotation(javax.persistence.Column.class).name());
			}
		}
		sql.append("=?");
		return sql.toString();
	}

	public String generateInsert() {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (");
		StringBuilder values = new StringBuilder();
		values.append(" VALUES (");
		for (Field f : genericClass.getDeclaredFields()) {
			if (f.getAnnotation(javax.persistence.Id.class) != null) {
				continue;
			}
			String annotName = f.getAnnotation(javax.persistence.Column.class).name();
			sql.append(annotName);
			sql.append(",");
			values.append("?,");
		}
		sql.setCharAt(sql.length() - 1, ')');
		values.setCharAt(values.length() - 1, ')');
		sql.append(values);
		return sql.toString();
	}

	public String generateUpdate() {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ");
		sql.append(tableName);
		sql.append(" SET ");
		StringBuilder whereId = new StringBuilder();
		for (Field f : genericClass.getDeclaredFields()) {
			String annotName = f.getAnnotation(javax.persistence.Column.class).name();
			if (f.getAnnotation(javax.persistence.Id.class) != null) {
				whereId.append(" WHERE ");
				whereId.append(annotName);
				whereId.append("=?");
				continue;
			}
			sql.append(annotName);
			sql.append("=?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(whereId);
		return sql.toString();
	}

	public String generateDelete() {
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
		sql.append(tableName);
		for (Field f : genericClass.getDeclaredFields()) {
			if (f.getAnnotation(javax.persistence.Id.class) != null) {
				sql.append(f.getAnnotation(javax.persistence.Column.class).name());
			}
		}
		sql.append(" = ?");
		return sql.toString();
	}
}