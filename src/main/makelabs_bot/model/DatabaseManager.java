/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model;

import io.github.cdimascio.dotenv.Dotenv;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.data_pojo.Contract;
import main.makelabs_bot.model.data_pojo.ContractUser;
import main.makelabs_bot.model.data_pojo.PostWorkData;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseManager {
    private static final String databaseUri =
            "jdbc:mysql://makelabsdatabase.cxkbfyhmxknq.eu-central-1.rds.amazonaws.com:3306/";
    public static String databaseName;
    //todo change aws mysql name and password
    private static DatabaseManager databaseManager;
    private final Connection connection;
    private final Statement statement;

    private DatabaseManager() {
        Dotenv dotenv = Dotenv.load();
        String password = dotenv.get("DB_PASSWORD");
        String user = dotenv.get("DB_USER");
        if (databaseName == null || databaseName.isEmpty())
            databaseName = dotenv.get("DB_NAME");

        Connection tempConnection = null;
        Statement tempStatement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            tempConnection = DriverManager.getConnection(databaseUri + databaseName,
                    user, password);
            tempStatement = tempConnection.createStatement();
            ResultSet resultSet = tempStatement.executeQuery("SELECT now();");
            if (resultSet.first()) {
                String time = resultSet.getTimestamp(1).toString();
                Log.Info(time, Log.DATABASE_MANAGER);
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = tempConnection;
        statement = tempStatement;
        if (connection != null) {
            Log.Info("DatabaseManager successfully initialized", Log.DATABASE_MANAGER);
        } else {
            Log.Info("DatabaseManager has some issue with starting", Log.DATABASE_MANAGER);
        }
    }

    public static DatabaseManager getInstance() {
        if (databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null)
                    databaseManager = new DatabaseManager();
            }
        }
        return databaseManager;
    }

    public List<Contract> getAllAppliedNotPaidContracts() {
        List<Contract> contracts = new LinkedList<>();
        try {
            ResultSet resultSet = statement.executeQuery("select * from contracts where applied is null");
            if (resultSet.first()) {
                Analytics.getInstance().updateDatabaseSelects(1);
                do {
                    contracts.add(parseContractFromResultSet(resultSet));
                } while (resultSet.next());
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public ContractUser getUserById(Long uid) {
        ContractUser user = null;
        if (uid == null)
            return null;
        try (PreparedStatement preparedStatement = connection.
                prepareStatement("select * from users where id=? limit 1")) {

            preparedStatement.setLong(1, uid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                user = parseContractUserFromResultSet(resultSet);
                Analytics.getInstance().updateDatabaseSelects(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public void saveUser(ContractUser user) throws SQLException {
        ContractUser tryToGet = getUserById(user.getId());
        String query;
        if (tryToGet == null)
            query = "insert into users(username,firstname,lastname,usertype,state_uri,messageId,spent_money," +
                    "earned_money,orders_ordered,orders_made,orders_reviewed,orders_gaveoff,payments_accepted,id) " +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        else
            query = "update users set username=?,firstname=?,lastname=?,usertype=?,state_uri=?,messageId=?," +
                    "spent_money=?,earned_money=?,orders_ordered=?,orders_made=?,orders_reviewed=?,orders_gaveoff=?," +
                    "payments_accepted=? where id=?;";

        PostWorkData workData = getWorkData(user.getStateUri());
        if (workData == null)
            throw new SQLException("Cannot save user with invalid state uri");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getFirstname());
            if (user.getLastname() != null)
                preparedStatement.setString(3, user.getLastname());
            else
                preparedStatement.setNull(3, Types.VARCHAR);
            preparedStatement.setInt(4, user.getUserType());
            preparedStatement.setString(5, user.getStateUri());
            preparedStatement.setInt(6, user.getMessageId());
            preparedStatement.setInt(7, user.getSpentMoney());
            preparedStatement.setInt(8, user.getEarnedMoney());
            preparedStatement.setInt(9, user.getOrdersOrdered());
            preparedStatement.setInt(10, user.getOrdersMade());
            preparedStatement.setInt(11, user.getOrdersReviewed());
            preparedStatement.setInt(12, user.getOrdersGaveOff());
            preparedStatement.setInt(13, user.getPaymentsAccepted());
            preparedStatement.setLong(14, user.getId());

            int rows = preparedStatement.executeUpdate();
            if (tryToGet == null)
                Analytics.getInstance().updateDatabaseInserts(rows);
            else
                Analytics.getInstance().updateDatabaseUpdates(rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Integer getMessageId(Long uid) {
        Integer messageId = null;
        if (uid == null)
            return null;
        try (PreparedStatement preparedStatement = connection.
                prepareStatement("select messageId from users where id=? limit 1")) {

            preparedStatement.setLong(1, uid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                messageId = resultSet.getInt("messageId");
                Analytics.getInstance().updateDatabaseSelects(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return messageId;
    }

    public void saveMessageIdForUser(Long uid, Integer messageId) {
        if (uid == null || messageId == null)
            return;

        String query = "update users set messageId=? where id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, messageId);
            preparedStatement.setLong(2, uid);
            int rows = preparedStatement.executeUpdate();

            Analytics.getInstance().updateDatabaseUpdates(rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public PostWorkData getWorkData(String uri) {
        PostWorkData workData = null;
        if (uri == null || uri.isEmpty())
            return null;
        try (PreparedStatement preparedStatement = connection.
                prepareStatement("select * from work_data where uri=? limit 1")) {

            preparedStatement.setString(1, uri);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                workData = parseWorkDataFromResultSet(resultSet);
                Analytics.getInstance().updateDatabaseSelects(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return workData;
    }

    public void saveWorkData(PostWorkData postWorkData) throws SQLException {
        if (postWorkData == null || postWorkData.getUri() == null || postWorkData.getUri().isEmpty())
            return;

        PostWorkData tryToGet = getWorkData(postWorkData.getId());
        String query;
        if (tryToGet == null) {
            ContractUser createdBy = getUserById(postWorkData.getCreatedByUid());
            if (createdBy == null)
                throw new SQLException("Cannot save work data with invalid creator's id");
            query = "insert into work_data(params_json,description,created_by_uid,uri,has_child) " +
                    "values(?,?,?,?,?);";
        } else
            query = "update work_data set params_json=?, description=?,uri=?,has_child=? where id=?";


        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (tryToGet == null) {

                preparedStatement.setString(1, postWorkData.getJsonParams());
                preparedStatement.setString(2, postWorkData.getDescription());
                preparedStatement.setLong(3, postWorkData.getCreatedByUid());
                preparedStatement.setString(4, postWorkData.getUri());
                preparedStatement.setBoolean(5, !postWorkData.isEndpoint());
            } else {
                preparedStatement.setString(1, postWorkData.getJsonParams());
                preparedStatement.setString(2, postWorkData.getDescription());
                preparedStatement.setString(3, postWorkData.getUri());
                preparedStatement.setBoolean(4, !postWorkData.isEndpoint());
                preparedStatement.setLong(5, postWorkData.getId());
            }
            int rows = preparedStatement.executeUpdate();

            if (tryToGet == null)
                Analytics.getInstance().updateDatabaseInserts(rows);
            else
                Analytics.getInstance().updateDatabaseUpdates(rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Contract getContract(Long byId) {
        Contract contract = null;
        if (byId == null)
            return null;
        try (PreparedStatement preparedStatement = connection.
                prepareStatement("select * from contracts where id=? limit 1")) {

            preparedStatement.setLong(1, byId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                contract = parseContractFromResultSet(resultSet);
                Analytics.getInstance().updateDatabaseSelects(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return contract;
    }

    public Long getWorkDataId(PostWorkData postWorkData) {
        Long id = null;
        if (postWorkData == null || postWorkData.getUri() == null || postWorkData.getUri().isEmpty())
            return null;
        try (PreparedStatement preparedStatement = connection.
                prepareStatement("select id from work_data where uri=? limit 1")) {

            preparedStatement.setString(1, postWorkData.getUri());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                id = resultSet.getLong("id");
                Analytics.getInstance().updateDatabaseSelects(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }

    public PostWorkData getWorkData(Long byId) {
        PostWorkData workData = null;
        if (byId == null)
            return null;
        try (PreparedStatement preparedStatement = connection.
                prepareStatement("select * from work_data where id=? limit 1")) {

            preparedStatement.setLong(1, byId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                workData = parseWorkDataFromResultSet(resultSet);
                Analytics.getInstance().updateDatabaseSelects(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return workData;
    }

    public void saveContract(Contract contract) throws SQLException {
        Contract tryToGet = getContract(contract.getId());
        String query;
        if (tryToGet == null)
            query = "insert into contracts(\n" +
                    "customer_uid,name,additional,\n" +
                    "comment,price,work_data_id,\n" +
                    "status,applied,paid,payment_checked_by_uid,\n" +
                    "taken_by_uid,taken,reviewed_by_uid,\n" +
                    "gaveoff,gaveoff_by_uid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        else
            query = "update contracts set customer_uid=?,name=?,additional=?,comment=?,price=?,work_data_id=?,status=?," +
                    "applied=?,paid=?,payment_checked_by_uid=?,taken_by_uid=?,taken=?,reviewed_by_uid=?," +
                    "gaveoff=?,gaveoff_by_uid=? where id=?";

        PostWorkData workData = getWorkData(contract.getWorkDataId());
        ContractUser user = getUserById(contract.getCustomerId());

        if (workData == null)
            throw new SQLException("Cannot save contract with invalid workData id");
        if (user == null)
            throw new SQLException("Cannot save contract with invalid user id");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, contract.getCustomerId());
            preparedStatement.setString(2, contract.getName());
            preparedStatement.setString(3, contract.getAdditional());
            preparedStatement.setString(4, contract.getComment());
            preparedStatement.setFloat(5, contract.getPrice());
            preparedStatement.setLong(6, contract.getWorkDataId());
            preparedStatement.setString(7, contract.getStatus());

            Timestamp applied = (contract.getApplied());
            Timestamp paid = (contract.getPaid());
            Timestamp taken = (contract.getTaken());
            Timestamp gaveoff = (contract.getGaveOff());

            if (applied != null)
                preparedStatement.setTimestamp(8, applied);
            else
                preparedStatement.setNull(8, Types.TIMESTAMP);
            if (paid != null)
                preparedStatement.setTimestamp(9, paid);
            else
                preparedStatement.setNull(9, Types.TIMESTAMP);
            if (taken != null)
                preparedStatement.setTimestamp(12, taken);
            else
                preparedStatement.setNull(12, Types.TIMESTAMP);
            if (gaveoff != null)
                preparedStatement.setTimestamp(14, gaveoff);
            else
                preparedStatement.setNull(14, Types.TIMESTAMP);

            if (contract.getPaymentCheckedByUID() != null)
                preparedStatement.setLong(10, contract.getPaymentCheckedByUID());
            else
                preparedStatement.setNull(10, Types.INTEGER);

            if (contract.getTakenByUID() != null)
                preparedStatement.setLong(11, contract.getTakenByUID());
            else
                preparedStatement.setNull(11, Types.INTEGER);
            if (contract.getReviewByUID() != null)
                preparedStatement.setLong(13, contract.getReviewByUID());
            else
                preparedStatement.setNull(13, Types.INTEGER);
            if (contract.getGaveOffByUID() != null)
                preparedStatement.setLong(15, contract.getGaveOffByUID());
            else
                preparedStatement.setNull(15, Types.INTEGER);
            if (tryToGet != null)
                preparedStatement.setLong(16, contract.getId());// in case we are updating record not inserting

            int rows = preparedStatement.executeUpdate();

            if (tryToGet == null)
                Analytics.getInstance().updateDatabaseInserts(rows);
            else
                Analytics.getInstance().updateDatabaseUpdates(rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isWorkDataUriValid(String uri) {
        if (uri == null || uri.isEmpty())
            return false;
        try (PreparedStatement preparedStatement = connection.
                prepareStatement("select id from work_data where uri=? limit 1")) {

            preparedStatement.setString(1, uri);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                Analytics.getInstance().updateDatabaseSelects(1);
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Long getContractId(Contract contract) {
        Long contractId = null;
        if (contract == null)
            return null;

        if (contract.getCreated() != null) {
            try (PreparedStatement preparedStatement = connection.
                    prepareStatement("select id from contracts where created=? limit 1")) {
                preparedStatement.setTimestamp(1, contract.getCreated());

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    contractId = resultSet.getLong("id");
                    Analytics.getInstance().updateDatabaseSelects(1);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {

            try (PreparedStatement preparedStatement = connection.
                    prepareStatement("select id from contracts where customer_uid=? and name=? " +
                            "and work_data_id=? and price=? and status=? and additional=? limit 1")) {
                preparedStatement.setLong(1, contract.getCustomerId());
                preparedStatement.setString(2, contract.getName());
                preparedStatement.setLong(3, contract.getWorkDataId());
                preparedStatement.setFloat(4, contract.getPrice());
                preparedStatement.setString(5, contract.getStatus());
                preparedStatement.setString(6, contract.getAdditional());

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    contractId = resultSet.getLong("id");
                    Analytics.getInstance().updateDatabaseSelects(1);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return contractId;
    }

    private Contract parseContractFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long customer_uid = resultSet.getLong("customer_uid");
        String name = resultSet.getString("name");
        String additional = resultSet.getString("additional");
        String comment = resultSet.getString("comment");
        Float price = resultSet.getFloat("price");
        Long work_data_id = resultSet.getLong("work_data_id");
        String status = resultSet.getString("status");
        Timestamp applied = resultSet.getTimestamp("applied");
        Timestamp paid = resultSet.getTimestamp("paid");
        Long payment_checked_by_uid = resultSet.getLong("payment_checked_by_uid");
        Long taken_by_uid = resultSet.getLong("taken_by_uid");
        Timestamp taken = resultSet.getTimestamp("taken");
        Long reviewed_by_uid = resultSet.getLong("reviewed_by_uid");
        Timestamp gaveoff = resultSet.getTimestamp("gaveoff");
        Long gaveoff_by_uid = resultSet.getLong("gaveoff_by_uid");
        Timestamp created = resultSet.getTimestamp("created");

        return new Contract(id, customer_uid, work_data_id, name, additional,
                comment, price, status, applied, paid, payment_checked_by_uid,
                taken_by_uid, taken, reviewed_by_uid, gaveoff, gaveoff_by_uid,
                created);
    }

    private PostWorkData parseWorkDataFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String params = resultSet.getString("params_json");
        String description = resultSet.getString("description");
        Long created_by_uid = resultSet.getLong("created_by_uid");
        Timestamp created = resultSet.getTimestamp("created");
        String uri = resultSet.getString("uri");
        Boolean isEndpoint = resultSet.getBoolean("has_child");

        return new PostWorkData(id, params, description, created_by_uid,
                created, uri, isEndpoint);
    }

    private ContractUser parseContractUserFromResultSet(ResultSet resultSet) throws SQLException {
        //id,username,firstname,lastname,usertype,state_uri,messageId,spent_money,earned_money,orders_ordered,orders_made,orders_reviewed,orders_gaveoff,payments_accepted
        int id = resultSet.getInt("id");
        int userType = resultSet.getInt("usertype");
        int messageId = resultSet.getInt("messageId");
        int spent_money = resultSet.getInt("spent_money");
        int earned_money = resultSet.getInt("earned_money");
        int orders_ordered = resultSet.getInt("orders_ordered");
        int orders_made = resultSet.getInt("orders_made");
        int orders_reviewed = resultSet.getInt("orders_reviewed");
        int orders_gaveoff = resultSet.getInt("orders_gaveoff");
        int payments_accepted = resultSet.getInt("payments_accepted");
        String username = resultSet.getString("username");
        String firstname = resultSet.getString("firstname");
        String lastname = resultSet.getString("lastname");
        String stateUri = resultSet.getString("state_uri");

/*
public ContractUser(int id, String username, String firstname, String lastname, int userType, String stateUri,
                        int messageId, int spentMoney, int earnedMoney, int ordersOrdered,
                        int ordersMade, int ordersReviewed, int ordersGaveOff, int paymentsAccepted)
 */
        return new ContractUser(id, username, firstname, lastname, userType, stateUri, messageId, spent_money, earned_money,
                orders_ordered, orders_made, orders_reviewed, orders_gaveoff, payments_accepted);
    }


    public void removeContract(Long contractId) {
        if (contractId == null)
            return;
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE from contracts where id=?")) {
            preparedStatement.setLong(1, contractId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(Long id) {
        if (id == null)
            return;
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE from contracts where customer_uid=?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE from users where id=?");
            preparedStatement1.setLong(1, id);
            preparedStatement1.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeWorkData(String uri) {
        if (uri == null || uri.isEmpty())
            return;

        PostWorkData data = getWorkData(uri);
        if (data != null && data.getId() != null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE from contracts where work_data_id=?")) {
                preparedStatement.setLong(1, data.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE from work_data where uri=?")) {
            preparedStatement.setString(1, uri);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Contract> getAllUserContracts(ContractUser contractUser) {
        List<Contract> contracts = new LinkedList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from contracts where customer_uid=?")) {
            preparedStatement.setLong(1, contractUser.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                Analytics.getInstance().updateDatabaseSelects(1);
                do {
                    contracts.add(parseContractFromResultSet(resultSet));
                } while (resultSet.next());
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public Contract getUnapprovedContract(ContractUser contractUser) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from contracts where customer_uid=? and applied is null")) {
            preparedStatement.setLong(1, contractUser.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                resultSet.close();
                Analytics.getInstance().updateDatabaseSelects(1);
                return parseContractFromResultSet(resultSet);
            }
            resultSet.close();
            Contract freshNew = new Contract(contractUser.getId());
//            saveContract(freshNew);
            return freshNew;//getUnapprovedContract(contractUser);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
