package DAOImpl;

import DAO.CommentDAO;
import DAO.PictureDAO;
import DAO.Statements;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all operations with pictures
 */
public class PictureDAOImpl extends BaseDAO implements PictureDAO {
    /**
     * UserDAOImpl instance
     */
    private UserDAOImpl userDAO;
    /**
     * CommentDAOIml instance
     */
    private CommentDAO commentDAO;

    /**
     * Getter for commentDAO
     *
     * @return CommentDAOImpl instance
     */
    public CommentDAO getCommentDAO() {
        return commentDAO;
    }

    /**
     * Setter for commentDAO
     *
     * @param commentDAO CommentDAOImpl instance
     */
    public void setCommentDAO(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    protected PictureDAOImpl() throws SQLException { // TODO: 7/29/2019 probably should be deleted
    }

    /**
     * Setter for userDAO
     *
     * @param userDAO UserDAOImpl instance
     */
    public void setUserDAO(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Getter for userDAO
     *
     * @return UserDAOImpl instance
     */
    public UserDAOImpl getUserDAO() {
        return userDAO;
    }

    /**
     * Gets Picture instance by its id
     *
     * @param id id of a picture
     * @return Picture instance
     * @throws SQLException when select statement fails
     */
    @Override
    public Picture getPictureById(long id) throws SQLException {
        try {
            getConnection();
            statement = connection.prepareStatement(Statements.GET_PICTURE_BY_ID);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            return parsePicture(resultSet);
        } finally {
            closeAll();
        }
    }

    private Picture parsePicture(ResultSet resultSet) throws SQLException {
        try {
            Clob clob = resultSet.getClob(5);
            return new Picture(
                    resultSet.getLong(1),
                    new SimpleUser(
                            resultSet.getString(2),
                            resultSet.getString(3)),
                    resultSet.getDate(4),
                    clob.getSubString(1, (int) clob.length()));
        } finally {
            closeAll();
        }
    }

    /**
     * Gets all pictures painted by user
     *
     * @param login login of author
     * @return list of pictures
     * @throws SQLException when select statement fails
     */
    @Override
    public List<SimplePicture> getSimplePicturesByLogin(String login) throws SQLException {
        try {
            getConnection();
            statement = connection.prepareStatement(Statements.GET_MAIN_PICTURE_DATA_BY_LOGIN);
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            return parseSimplePictures();
        } finally {
            closeAll();
        }
    }

    @Override
    public List<Picture> getFavoritesPictures(String userName) throws SQLException {
        return null;
    }

    /**
     * Saves picture to database
     *
     * @param login       author of a picture
     * @param createdWhen when a picture was created
     * @param content     picture data
     * @throws SQLException when insert statement fails
     */
    @Override
    public void savePicture(String login, long createdWhen, String content) throws SQLException {
        try {
            getConnection();
            Clob imgClob = connection.createClob(); // TODO: 7/30/2019 probably free() method should be called in finally block to release clob
            imgClob.setString(1, content);
            statement = connection.prepareStatement(Statements.SAVE_PICTURE);
            statement.setString(1, login);
            statement.setDate(2, new Date(createdWhen));
            statement.setClob(3, imgClob);
            statement.execute();
        } finally {
            closeAll();
        }
    }

    @Override
    public void deletePicture(long id) throws SQLException {

    }

    /**
     * Gets list of main data for pictures from ResultSet
     *
     * @return list of pictures
     * @throws SQLException
     */
    private List<SimplePicture> parseSimplePictures() throws SQLException {
        List<SimplePicture> userPictures = new ArrayList<>();
        while (resultSet.next()) {
            Clob clob = resultSet.getClob(4);
            userPictures.add(new SimplePicture(
                    resultSet.getLong(1),
                    clob.getSubString(1, (int) clob.length())
            ));
        }
        return userPictures;
    }

    /**
     * Checks whether picture is like by user
     *
     * @param pictureId id of a picture
     * @param login     login of a user
     * @return true if a picture is liked, false otherwise
     * @throws SQLException when select statement fails
     */
    private boolean isPictureLiked(long pictureId, String login) throws SQLException {
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(Statements.IS_PICTURE_LIKED);
            statement.setLong(1, pictureId);
            statement.setString(2, login);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }

    /**
     * Adds like to picture. If the picture is already likes, then it removes like
     *
     * @param pictureId id of a picture
     * @param login     login of a author
     * @throws SQLException when insert statement fails
     */
    @Override
    public void likeThePicture(long pictureId, String login) throws SQLException {
        try {
            getConnection();
            statement = connection.prepareStatement(isPictureLiked(pictureId, login)
                    ? Statements.REMOVE_LIKE
                    : Statements.ADD_LIKE);
            statement.setLong(1, pictureId);
            statement.setString(2, login);
            statement.execute();
        } finally {
            closeAll();
        }
    }

    /**
     * Gets amount of likes for picture
     *
     * @param pictureId id of a picture
     * @return amount of likes
     * @throws SQLException when select statement fails
     */
    @Override
    public int countLikes(long pictureId) throws SQLException {
        try {
            getConnection();
            statement = connection.prepareStatement(Statements.COUNT_LIKES);
            statement.setLong(1, pictureId);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } finally {
            closeAll();
        }
    }

    /**
     * Gets picture, its likes and comments by id
     *
     * @return PictureModel instance
     * @throws SQLException when select statement fails
     */
    @Override
    public PictureModel getPictureModelById(long id) throws SQLException {
        try {
            return new PictureModel(
                    getPictureById(id),
                    countLikes(id),
                    commentDAO.getCommentsByPictureId(id));
        } finally {
            closeAll();
        }
    }
}
