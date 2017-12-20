package com.zy.phone.sqline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * �������ݿ��
 * @author Administrator
 *
 */
public class SqlPackageName {
	DBHelperSQLite dbhelper;

	public DBHelperSQLite getDbhelper() {
		return dbhelper;
	}

	public void setDbhelper(DBHelperSQLite dbhelper) {
		this.dbhelper = dbhelper;
	}

	public SqlPackageName(Context context) {
		dbhelper = new DBHelperSQLite(context);
	}
	/**
	 * �鿴�Ƿ���ڱ�
	 * @return
	 */
	public boolean TabbleIsExist() {
		return dbhelper.tabbleIsExist("PackageName");
	}
	/**
	 * ������
	 * @return
	 */
	public int CreateTable() {
		int rsStr = 0;
		String _SqlStr = "CREATE TABLE PackageName (" + " PName text not null,"
				+ " PNO text not null," + " CreateTime text not null);";
		rsStr = dbhelper.createTable("PackageName", _SqlStr);
		return rsStr;
	}
	/**
	 * �������
	 * @param PName
	 * @param PNO
	 * @return
	 */
	public int Add(String PName, String PNO) {
		int rsStr = 0;

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date_Now = sDateFormat.format(new java.util.Date());
		String _SqlStr = "insert into PackageName ( PName,PNO, CreateTime)"
				+ " values('" + PName + "','" + PNO + "','" + date_Now + "');";
		rsStr = dbhelper.execSQL(_SqlStr);
		return rsStr;
	}
	/**
	 * sqline ���ﴦ��
	 * @param PName
	 * @return
	 */
	public List<String> Add(List<String> PName) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date_Now = sDateFormat.format(new java.util.Date());
		List<String> list_sql = new ArrayList<String>();
		for (int i = 0; i < PName.size(); i++) {
			list_sql.add("insert into PackageName (PName,PNO,CreateTime) values ('"
					+ PName.get(i)
					+ "','"
					+ "0"
					+ "','"
					+ date_Now
					+ "');");
		}
		dbhelper.execSQL(list_sql);
		return null;
	}
	/**
	 * ��ȡ����
	 * @param _Selection
	 * @param _SelectionArgs
	 * @param _GroupBy
	 * @param _Having
	 * @param _OrderBy
	 * @return
	 */
	public String GetDate(String _Selection, String[] _SelectionArgs,
			String _GroupBy, String _Having, String _OrderBy) {
		String strData = "";
		String _TableName = "PackageName";
		String[] _Columns = new String[] { "PName", "PNO", "CreateTime" };
		Cursor cursor = dbhelper.select(_TableName, _Columns, _Selection,
				_SelectionArgs, _GroupBy, _Having, _OrderBy);

		strData = "";
		int i = 0;
		try {
			strData = "{\"TotalCount\":[{\"TotalCount\":\"" + cursor.getCount()
					+ "\"}],\"Data\":[";
			while (cursor.moveToNext()) {

				String PName = cursor.getString(cursor.getColumnIndex("PName"));
				String PNO = cursor.getString(cursor.getColumnIndex("PNO"));
				String _CreateTime = cursor.getString(cursor
						.getColumnIndex("CreateTime"));
				if (i != 0) {
					strData += ",";
				}
				strData += "{";
				strData += "\"PName\":\"" + PName + "\",";
				strData += "\"PNO\":\"" + PNO + "\",";
				strData += "\"CreateTime\":\"" + _CreateTime + "\"";
				strData += "}";
				i++;
			}
			cursor.close();
			strData += "]}";
		} catch (Exception ex) {
			strData = "-1";

		}
		if (i == 0) {
			strData = "0";
		}
		return strData;
	}
	/**
	 * ��ѯ�ֶ�
	 * @param tabName
	 * @return
	 */
	public boolean tabIsExist(String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		Cursor cursor = null;
		try {

			String sql = "select count(*) as c from sqlite_master where name ='"
					+ tabName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * ɾ��������
	 * @param PName
	 * @return
	 */
	public int DeleteByGUID(String PName) {
		int rsStr = 0;
		if (PName != "") {
			rsStr = dbhelper.deleteItem("PackageName", "  PName =?",
					new String[] { PName });
		} else {
			rsStr = dbhelper.deleteItem("PackageName", null, null);
		}
		return rsStr;
	}
	/**
	 * ɾ����
	 * @param PName
	 * @return
	 */
	public int DropTable() {
		int rsStr = 0;
		rsStr = dbhelper.dropTable("PackageName", "");
		return rsStr;
	}
	/**
	 * �ر����ݿ�
	 */
	public void close() {
		dbhelper.close();
	}

}
