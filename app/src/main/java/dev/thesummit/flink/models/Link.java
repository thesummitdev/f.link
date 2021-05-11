package dev.thesummit.flink.models;

import dev.thesummit.flink.FlinkApplication;
import dev.thesummit.flink.database.DatabaseObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.json.JSONObject;

public class Link implements DatabaseObject {

  private UUID id;
  private String url;
  private String tags;
  private Boolean unread;

  private static final String INSERT_QUERY =
      "INSERT INTO links (url, tags, unread) VALUES (?,?,?) RETURNING id;";
  private static final String DELETE_QUERY = "DELETE FROM links WHERE id=?;";
  private static final String UPDATE_QUERY = "UPDATE links SET url=?, tags=?, unread=? WHERE id=? ";

  /**
   * Construct a Link instance.
   *
   * <p><b>Warning: Does not save the instance to the database. call .add() to commit to database.
   * </b>
   *
   * <p>The id property will remain unset until the instance is added to the database.
   *
   * @param url the url.
   * @param tags the tags.
   * @param unread whether the link is marked as unread.
   */
  public Link(String url, String tags, Boolean unread) {
    this.url = url;
    this.tags = tags;
    this.unread = unread;
  }

  public static Link get(String uidd) throws SQLException {
    return null;
  }

  public static List<Link> getAll(List<String> uidds) throws SQLException {
    return null;
  }

  /**
   * Adds this link instance to the database.
   *
   * <p>Fetches a connection from the application connection pool and attempts to add this link to
   * the database. Will append the database generated id to this instance when successful.
   *
   * @throws SQLException
   */
  @Override
  public void add() throws SQLException {
    Connection conn = FlinkApplication.getContext().pool.getConnection();

    PreparedStatement statement = conn.prepareStatement(Link.INSERT_QUERY);
    statement.setString(1, this.url);
    statement.setString(2, this.tags);
    statement.setBoolean(3, this.unread);

    ResultSet rs = statement.executeQuery();
    rs.next(); // Position the RS for the returned ID.
    this.setId(rs.getObject("id", UUID.class));

    statement.close();
    FlinkApplication.getContext().pool.releaseConnection(conn);
  }

  /**
   * Removes this link from the database.
   *
   * <p>Fetches a connection from the application connection pool and attempts to remove this link
   * from the database.
   *
   * @param id The id of the link to delete.
   * @throws SQLException
   */
  public static void delete(String id) throws SQLException {
    Connection conn = FlinkApplication.getContext().pool.getConnection();

    PreparedStatement statement = conn.prepareStatement(Link.DELETE_QUERY);
    statement.setString(1, id);
    statement.execute();

    statement.close();
    FlinkApplication.getContext().pool.releaseConnection(conn);
  }

  /**
   * Updates the corresponding link row in the database to match this instance.
   *
   * <p>Fetches a connection from the application connection pool and attempts to update the current
   * link row in database.
   *
   * @throws SQLException
   */
  @Override
  public void update() throws SQLException {
    Connection conn = FlinkApplication.getContext().pool.getConnection();

    PreparedStatement statement = conn.prepareStatement(Link.UPDATE_QUERY);
    statement.setString(1, this.url);
    statement.setString(2, this.tags);
    statement.setBoolean(3, this.unread);
    statement.setObject(4, this.id);
    statement.execute();

    statement.close();
    FlinkApplication.getContext().pool.releaseConnection(conn);
  }

  /**
   * Make a human-readable string representaiton of this Link object.
   *
   * <p>${id} / ${url} / {$tags}
   *
   * @return A printable, displayable, transmittable representation of the Link.
   */
  @Override
  public String toString() {
    return String.format(
        "Link id %s, with a url of %s and tags of %s", this.id, this.url, this.tags);
  }

  /**
   * Make a JSON compliant object from the link instance.
   *
   * @return The JSONObject.
   */
  public JSONObject toJSONObject() {
    JSONObject obj =
        new JSONObject()
            .put("id", this.id)
            .put("url", this.url)
            .put("tags", this.tags)
            .put("unread", this.unread);

    return obj;
  }

  /**
   * Create a Link instance from a SQL ResultSet row.
   *
   * <p>Does not call rs.next() before or after, operates on the current row.
   *
   * @param rs A result set row.
   * @return The link.
   */
  public static Link fromResultSet(ResultSet rs) throws SQLException {
    Link l = new Link(rs.getString("url"), rs.getString("tags"), rs.getBoolean("unread"));
    l.setId(rs.getObject("id", UUID.class));
    return l;
  }

  /**
   * Getter for the link.id field.
   *
   * @return The id value.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Internal Setter for the link.id field.
   *
   * <p><b> Warning: Must be a UUID </b>
   *
   * @param id The id to set.
   */
  private void setId(UUID id) {
    this.id = id;
  }

  /**
   * Getter for the link.url field.
   *
   * @return The url value.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Setter for the link.url field.
   *
   * <p><b> Warning: Must be a valid URL </b>
   *
   * @param url The url to set.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Getter for the link.tags field.
   *
   * @return The tags value.
   */
  public String getTags() {
    return tags;
  }

  /**
   * Setter for the link.tags field.
   *
   * @param tags The tags to set.
   */
  public void setTags(String tags) {
    this.tags = tags;
  }

  /**
   * Getter for the link.unread field.
   *
   * @return The unread value.
   */
  public Boolean getUnread() {
    return unread;
  }

  /**
   * Setter for the link.unread field.
   *
   * @param unread The unread value to set.
   */
  public void setUnread(Boolean unread) {
    this.unread = unread;
  }
}
