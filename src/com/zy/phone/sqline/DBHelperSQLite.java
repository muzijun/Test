package com.zy.phone.sqline;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库操作类
 * @author Administrator
 *
 */
public class DBHelperSQLite extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "zy.db";
	private static final int DATABASE_VERSION = 1;

	public DBHelperSQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	SQLiteOpenHelper mOpenHelper = this;

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void dbOpen() {

	}

	public void dbClose() {
		try {
			mOpenHelper.getWritableDatabase().close();
		} catch (Exception ex) {
		} finally {
		}
	}
	/**
	 * 
	 * @param _TableName
	 * @return
	 */
	public boolean tabbleIsExist(String _TableName) {
		boolean result = false;
		if (_TableName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
					+ _TableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {

		}
		return result;
	}
	/**
	 * 创建表
	 * @param _TableName
	 * @param _SqlStr
	 * @return
	 */
	public int createTable(String _TableName, String _SqlStr) {
		int rsStr = 0;
		try {
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + _TableName);
			db.execSQL(_SqlStr);
			rsStr = 1;

		} catch (SQLException e) {
			rsStr = -1;

		}
		return rsStr;
	}
	
	public int dropTable(String _TableName, String _SqlStr) {
		int rsStr = 0;
		String sql = "drop table " + _TableName;
		try {
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			db.execSQL(sql);
			rsStr = 1;
		} catch (SQLException e) {
			rsStr = -1;
		}
		return rsStr;
	}
	
	public int execSQL(String _SqlStr) {
		int rsStr = 0;
		try {
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			db.execSQL(_SqlStr);
			rsStr = 1;

		} catch (SQLException e) {

			rsStr = 0;
		}
		return rsStr;
	}

	public void execSQL(List<String> PackageName) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		db.beginTransaction();
		try {
			if (null != PackageName) {
				for (int i = 0; i < PackageName.size(); i++) {
					db.execSQL(PackageName.get(i));
				}
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	/**
	 * 删除
	 * @param _TableName
	 * @param _Condition
	 * @param _Args
	 * @return
	 */
	public int deleteItem(String _TableName, String _Condition, String[] _Args) {
		int rsCount = 0;
		try {
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();

			rsCount = db.delete(_TableName, _Condition, _Args);

		} catch (SQLException e) {
			rsCount = -1;
		}
		return rsCount;
	}
	/**
	 * 查询
	 * @param _TableName
	 * @param _Columns
	 * @param _Selection
	 * @param _SelectionArgs
	 * @param _GroupBy
	 * @param _Having
	 * @param _OrderBy
	 * @return
	 */
	public Cursor select(String _TableName, String[] _Columns,
			String _Selection, String[] _SelectionArgs, String _GroupBy,
			String _Having, String _OrderBy) {
		Cursor cursor;
		SQLiteDatabase db = getReadableDatabase();
		cursor = db.query(_TableName, _Columns, _Selection, _SelectionArgs,
				_GroupBy, _Having, _OrderBy);
		return cursor;
	}
	
}
