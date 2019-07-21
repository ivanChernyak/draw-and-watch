package DAOImpl;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class BaseDAO {
	protected JdbcTemplate jdbcTemplate;
	protected PreparedStatement statement;
	protected ResultSet resultSet;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}