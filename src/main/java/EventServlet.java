import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>Header</b>
 * <p>
 * Description
 * </p>
 *
 * @author Ruwani Ranthika
 * @since 2025-05-25
 */

@WebServlet("/hello")
public class EventServlet extends HttpServlet {

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventdb", "root", "Ijse@1234");
            PreparedStatement statement = connection.prepareStatement("select * from events");
            ResultSet resultSet = statement.executeQuery();

            List<Map<String, String>> elist = new ArrayList<Map<String, String>>();

            while (resultSet.next()) {
                Map<String, String> events = new HashMap<String, String>();
                events.put("eid", resultSet.getString("eid"));
                events.put("ename", resultSet.getString("ename"));
                events.put("edescription", resultSet.getString("edescription"));
                events.put("edate", resultSet.getString("edate"));
                events.put("eplace", resultSet.getString("eplace"));
                elist.add(events);
            }

            resp.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getWriter(), elist);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        resp.setStatus(HttpServletResponse.SC_OK);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> eventData = mapper.readValue(req.getReader(), Map.class);

            String eid = eventData.get("eid");
            String ename = eventData.get("ename");
            String edescription = eventData.get("edescription");
            String edate = eventData.get("edate");
            String eplace = eventData.get("eplace");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/eventdb", "root", "Ijse@1234");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO events (eid, ename, edescription, edate, eplace) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, eid);
            preparedStatement.setString(2, ename);
            preparedStatement.setString(3, edescription);
            preparedStatement.setString(4, edate);
            preparedStatement.setString(5, eplace);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Event saved successfully");

            resp.setContentType("application/json");
            ObjectMapper map = new ObjectMapper();
            map.writeValue(resp.getWriter(), response);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to save event: " + e.getMessage());
            new ObjectMapper().writeValue(resp.getWriter(), error);
            e.printStackTrace();
        }
    }
}
