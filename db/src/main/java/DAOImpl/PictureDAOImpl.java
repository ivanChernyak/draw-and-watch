package DAOImpl;

import DAO.PictureDAO;
import DAO.Statements;
import model.Comment;
import model.Picture;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PictureDAOImpl extends BaseDAO implements PictureDAO {
    private UserDAOImpl userDAO;
    private Connection connection;

    private void getConnection() throws SQLException {
        connection = jdbcTemplate.getDataSource().getConnection();
    }

    protected PictureDAOImpl() throws SQLException {
    }

    public void setUserDAO(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }

    //@Override
    public Picture getPictureById(long id) throws SQLException {
        statement = jdbcTemplate.getDataSource().getConnection().prepareStatement(Statements.GET_PICTURE_BY_ID);
        statement.setLong(1, id);
        List<Picture> list = parsePictures(statement.executeQuery());
        return list == null || list.size() == 0 ? null : list.get(0);
    }

    @Override
    public List<Picture> getPictureByUser(String userName) throws SQLException{
        if (connection == null || connection.isClosed()) getConnection();
        try {
            statement = connection.prepareStatement(Statements.GET_PICTURE_BY_USER);
            statement.setString(1, userName);
            return parsePictures(statement.executeQuery());

        } finally {
            //if (statement != null) statement.close();
            connection.close();
        }
    }

    @Override
    public List<Picture> getFavoritesPictures(String userName) throws SQLException {
        return null;
    }

    @Override
    public void savePicture(String userName, long createdWhen, String content) throws SQLException{
        if (connection == null || connection.isClosed()) getConnection();
        try {
            Clob imgClob = connection.createClob();
            imgClob.setString(1, content);
            statement = connection.prepareStatement(Statements.SAVE_PICTURE);
            statement.setString(1, userName);
            statement.setDate(2, new Date(createdWhen));
            statement.setClob(3, imgClob);
            statement.execute();
        } finally {
            //if (statement != null) statement.close();
            connection.close();
        }
    }

    @Override
    public void deletePicture(long id) throws SQLException {

    }

    private List<Picture> parsePictures(ResultSet resultSet) throws SQLException {
        List<Picture> userPictures = new ArrayList<>();
        if (resultSet != null) {
            while (resultSet.next()) {
                Clob clob = resultSet.getClob(4);
                userPictures.add(new Picture(
                        resultSet.getLong(1),
                        userDAO.getUserProfile(resultSet.getString(2)),
                        resultSet.getDate(3),
                        clob.getSubString(1, (int) clob.length())
                ));
            }
        }
        return userPictures;
    }

    @Override
    public void addCommentToPicture(long pictureId, String login, String comment) throws SQLException {
        if (connection == null || connection.isClosed()) getConnection();
        try {
            Long parent = getLastCommentForPicture(pictureId);
            statement = connection.prepareStatement(Statements.ADD_COMMENT_TO_PICTURE);
            statement.setLong(1, pictureId);
            if (parent != null) {
                statement.setLong(2, parent.longValue());
            } else {
                statement.setNull(2, Types.BIGINT);
            }
            statement.setString(3, login);
            statement.setString(4, comment);
            statement.execute();
        } finally {
            connection.close();
        }
    }

    @Override
    public Long getLastCommentForPicture(long pictureId) throws SQLException {
        if (connection == null || connection.isClosed()) getConnection();
        try {
            statement = connection.prepareStatement(Statements.GET_LAST_COMMENT_FOR_PICTURE);
            statement.setLong(1, pictureId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getLong(1) : null;
        } finally {
            //connection.close();
        }
    }

    @Override
    public List<Comment> getCommentsForPicture(long pictureId) throws SQLException {
        if (connection == null || connection.isClosed()) getConnection();
        try {
            statement = connection.prepareStatement(Statements.GET_COMMENTS_FOR_PICTURE);
            statement.setLong(1, pictureId);
            return parseComments(statement.executeQuery());
        } finally {
            connection.close();
        }
    }

    private List<Comment> parseComments(ResultSet resultSet) throws SQLException {
        List<Comment> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new Comment(
                    resultSet.getLong(1),
                    resultSet.getLong(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5)
            ));
        }
        return list;
    }

    private boolean isPictureLiked(long pictureId, String login) throws SQLException {
        statement = connection.prepareStatement(Statements.IS_PICTURE_LIKED);
        statement.setLong(1, pictureId);
        statement.setString(2, login);
        return statement.executeQuery().next();
    }

    @Override
    public void likeThePicture(long pictureId, String login) throws SQLException {
        if (connection == null || connection.isClosed()) getConnection();
        try {
            statement = isPictureLiked(pictureId, login)
                    ? connection.prepareStatement(Statements.REMOVE_LIKE)
                    : connection.prepareStatement(Statements.ADD_LIKE);
            statement.setLong(1, pictureId);
            statement.setString(2, login);
            statement.execute();
        } finally {
            statement.close();
        }
    }

    @Override
    public int countLikes(long pictureId) throws SQLException {
        if (connection == null || connection.isClosed()) getConnection();
        try {
            statement = connection.prepareStatement(Statements.COUNT_LIKES);
            statement.setLong(1, pictureId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } finally {
            connection.close();
        }
    }
}
