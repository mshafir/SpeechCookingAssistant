/*   This file is part of SpeechCookingAssistant.
 *
 *   SpeechCookingAssistant is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, version 3
 *
 *   SpeechCookingAssistant is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with SpeechCookingAssistant.  If not, see <http://www.gnu.org/licenses/>.  
 * 
 *   Copyright 2011 Michael Shafir
 *   Michael.Shafir@gmail.com
 */
package recipe.db;

import java.sql.*;
import java.io.File;

/**
 * 
 * @author Michael
 */
public class SqliteDB {
	Connection con;

	public SqliteDB(String path) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String fullpath = "jdbc:sqlite:" + (new File(path)).getAbsolutePath();
		System.out.println(fullpath);
		con = DriverManager.getConnection(fullpath);
	}

	protected ResultSet executeQuery(String query) throws SQLException {
		Statement s = con.createStatement();
		return s.executeQuery(query);
	}

	public void disconnect() throws SQLException {
		if (con != null)
			con.close();
	}
}
